package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.Logger;
import org.sc.common.rest.TrailMappingDto;
import org.sc.common.rest.TrailRawDto;
import org.sc.common.rest.response.TrailMappingResponse;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.configuration.auth.AuthData;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.response.TrailPreviewResponseHelper;
import org.sc.controller.response.TrailRawResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.job.CompressImageJob;
import org.sc.manager.TrailFileManager;
import org.sc.processor.GpxFileHandlerHelper;
import org.sc.service.TrailImporterService;
import org.sc.util.FileProbeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.controller.Constants.ONE;
import static org.sc.controller.Constants.ZERO;
import static org.sc.controller.admin.Constants.PREFIX_IMPORT;
import static org.sc.util.FileProbeUtil.GPX_MIME_TYPE;

@RestController
@RequestMapping(PREFIX_IMPORT)
public class AdminTrailImporterController {

    private static final Logger LOGGER = getLogger(AdminTrailImporterController.class);

    public static final String REQUEST_CONTAINS_MISSING_NAMES_ERROR = "File is empty";

    private final TrailFileManager trailFileManager;
    private final TrailImporterService trailImporterService;
    private final TrailRawResponseHelper trailRawResponseHelper;
    private TrailPreviewResponseHelper trailPreviewResponseHelper;
    private final FileProbeUtil fileProbeUtil;
    private final GpxFileHandlerHelper gpxFileHandlerHelper;
    private GeneralValidator generalValidator;
    private final AuthFacade authFacade;


    @Autowired
    public AdminTrailImporterController(final TrailFileManager trailFileManager,
                                        final TrailImporterService trailImporterService,
                                        final TrailRawResponseHelper trailRawResponseHelper,
                                        final TrailPreviewResponseHelper trailResponseHelper,
                                        final FileProbeUtil fileProbeUtil,
                                        final GpxFileHandlerHelper gpxFileHandlerHelper,
                                        final GeneralValidator generalValidator,
                                        final AuthFacade authFacade) {
        this.trailFileManager = trailFileManager;
        this.trailImporterService = trailImporterService;
        this.trailPreviewResponseHelper = trailResponseHelper;
        this.fileProbeUtil = fileProbeUtil;
        this.trailRawResponseHelper = trailRawResponseHelper;
        this.gpxFileHandlerHelper = gpxFileHandlerHelper;
        this.generalValidator = generalValidator;
        this.authFacade = authFacade;
    }

    @Operation(summary = "Returns mappings for trails that match the trail coordinates")
    @PostMapping("/check")
    public TrailMappingResponse checkMatchingTrails(@RequestBody final TrailRawDto trailDto) {
        final Set<String> validateErrors = generalValidator.validate(trailDto);
        if(validateErrors.isEmpty()) {
            final List<TrailMappingDto> dtos = trailImporterService.mappingMatchingTrail(trailDto);
            return trailPreviewResponseHelper.constructMappingResponse(emptySet(), dtos, dtos.size(), 0, Integer.MAX_VALUE);
        }
        return trailPreviewResponseHelper.constructMappingResponse(validateErrors, emptyList(), 0, 0, Integer.MAX_VALUE);
    }

    @Operation(summary = "Read and import one GPX trail file")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailRawResponse importGpx(@RequestParam("file") MultipartFile gpxFile) {
        return processUploadedFiles(Collections.singletonList(gpxFile));
    }

    @Operation(summary = "Read and import multiple GPX trail files")
    @PostMapping(path = "/bulk",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailRawResponse importMassiveGpx(@RequestParam("files") MultipartFile[] files) {
        return processUploadedFiles(Arrays.asList(files));
    }

    private TrailRawResponse processUploadedFiles(final List<MultipartFile> files) {
        final AuthData authData = authFacade.getAuthHelper().getAuthData();

        final Map<String, Optional<Path>> originalFileNamesToTempPaths
                = trailFileManager.getGPXFilesTempPathList(files);

        if (originalFileNamesToTempPaths.isEmpty()) {
            return trailRawResponseHelper.
                    constructResponse(singleton(REQUEST_CONTAINS_MISSING_NAMES_ERROR), emptyList(),
                            ZERO, ZERO, ONE);
        }

        final Map<String, Path> originalFileNamesToExistingPaths = originalFileNamesToTempPaths
                .entrySet().stream()
                .filter(path -> path.getValue().isPresent())
                .collect(toMap(Map.Entry::getKey,
                        path -> originalFileNamesToTempPaths
                                .get(path.getKey())
                                .orElseThrow(IllegalStateException::new)));

        warnOnGpxMimeMismatch(originalFileNamesToExistingPaths);

        final Map<String, Path> gpxValidFiles = originalFileNamesToExistingPaths
                .entrySet().stream()
                .filter(nameToPath -> gpxFileHandlerHelper.canRead(nameToPath.getValue()))
                .collect(toMap(Map.Entry::getKey,
                        path -> originalFileNamesToExistingPaths
                                .get(path.getKey())));


        final List<TrailRawDto> trailRawDtos = gpxValidFiles
                .keySet()
                .parallelStream()
                .map(originalFilename -> {
                    final String uniqueFileName = trailFileManager.makeUniqueFileName(originalFilename);
                    final Path rawGpxPath = trailFileManager.saveRawGpx(uniqueFileName, originalFileNamesToExistingPaths.get(originalFilename));
                    return trailFileManager
                            .getTrailRawModel(uniqueFileName, originalFilename, rawGpxPath, authData);
                }).collect(toList());

        final List<TrailRawDto> savedTrails = trailRawDtos.stream().map(
                trailImporterService::saveRaw
        ).collect(toList());

        final int size = savedTrails.size();


        if (size != originalFileNamesToExistingPaths.size()) {
            final Set<String> notProcessedFiles = findNotProcessedFiles(originalFileNamesToExistingPaths.keySet(),
                    savedTrails.stream().map(a -> a.getFileDetails().getOriginalFilename()).collect(Collectors.toSet()));
            return trailRawResponseHelper.constructResponse(notProcessedFiles, savedTrails, size,
                    ZERO, size);
        }

        return trailRawResponseHelper.constructResponse(emptySet(), savedTrails, size,
                ZERO, size);
    }

    private void warnOnGpxMimeMismatch(Map<String, Path> originalFileNamesToExistingPaths) {
        originalFileNamesToExistingPaths
                .entrySet()
                .forEach(nameToPath -> {
                    final boolean doesFileMatchXmlMime = fileProbeUtil.getFileMimeType(nameToPath.getValue().toFile(),
                            nameToPath.getKey()).equals(GPX_MIME_TYPE);
                    if(!doesFileMatchXmlMime) LOGGER.warn(format("File with name '%s' does not match XML mime type...", nameToPath));
                });
    }

    private Set<String> findNotProcessedFiles(Set<String> initialFilenames, Set<String> savedFilenames) {
        return initialFilenames.stream().filter(a -> !savedFilenames.contains(a)).collect(Collectors.toSet());
    }

}