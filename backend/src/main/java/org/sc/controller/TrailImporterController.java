package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailRawDto;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.configuration.AppProperties;
import org.sc.data.validator.TrailImportValidator;
import org.sc.manager.TrailFileManager;
import org.sc.manager.TrailImporterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping(TrailImporterController.PREFIX)
public class TrailImporterController {

    public final static String PREFIX = "/import";
    public static final String REQUEST_CONTAINS_MISSING_NAMES_ERROR = "File is empty";

    public File uploadDir;

    private final TrailFileManager trailFileManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;
    private final AppProperties appProperties;
    private final ControllerPagination controllerPagination;

    @Autowired
    public TrailImporterController(final TrailFileManager trailFileManager,
                                   final TrailImporterManager trailImporterManager,
                                   final TrailImportValidator trailValidator,
                                   final AppProperties appProperties,
                                   final ControllerPagination controllerPagination) {
        this.trailFileManager = trailFileManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
        this.appProperties = appProperties;
        this.controllerPagination = controllerPagination;
    }


    @PostConstruct
    public void init() {
        uploadDir = new File(appProperties.getTempStorage());
    }

    @Operation(summary = "Read GPX trail file")
    @PostMapping(path = "/read",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailRawResponse readGpxFile(@RequestAttribute("file") MultipartFile gpxFile) throws IOException {
        return processUploadedFiles(Collections.singletonList(gpxFile));
    }

    @PostMapping(path = "/read-bulk",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailRawResponse readBulkGpxFile(@RequestParam("files") MultipartFile[] files) throws IOException {
        return processUploadedFiles(Arrays.asList(files));
    }

    private TrailRawResponse processUploadedFiles(@RequestParam("files") List<MultipartFile> files) {
        final Map<String, Optional<Path>> originalFileNamesToTempPaths
                = trailFileManager.getGPXFilesTempPathList(files);

        if (originalFileNamesToTempPaths.isEmpty()) {
            return constructResponse(singleton(REQUEST_CONTAINS_MISSING_NAMES_ERROR), emptyList(),
                    trailImporterManager.countTrailRaw(),
                    Constants.ZERO, Constants.ONE);
        }

        final Map<String, Path> originalFileNamesToExistingPaths = originalFileNamesToTempPaths
                .entrySet().stream().filter(path ->
                        path.getValue().isPresent()).collect(toMap(Map.Entry::getKey,
                        path -> originalFileNamesToTempPaths.get(path).orElseThrow(IllegalStateException::new)));

        final List<TrailRawDto> trailRawDtos = originalFileNamesToExistingPaths
                .keySet()
                .parallelStream()
                .map(originalFilename -> {
                    final String uniqueFileName = trailFileManager.makeUniqueFileName(originalFilename);
                    final Path rawGpxPath = trailFileManager.saveRawGpx(uniqueFileName, originalFileNamesToExistingPaths.get(originalFilename));
                    return trailFileManager.getTrailRawModel(uniqueFileName, originalFilename, rawGpxPath);
                }).collect(toList());

        List<TrailRawDto> savedTrails = trailRawDtos.stream().map(
                trailImporterManager::saveRaw
        ).collect(toList());

        final int size = savedTrails.size();
        return constructResponse(emptySet(), savedTrails, size,
                Constants.ZERO, size);
    }

    private TrailRawResponse constructResponse(Set<String> errors,
                                               List<TrailRawDto> dtos,
                                               long totalCount,
                                               int skip,
                                               int limit) {
        if (!errors.isEmpty()) {
            return new TrailRawResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailRawResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}