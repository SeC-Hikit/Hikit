package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.common.rest.assembler.TrailDTOAssembler;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.AppProperties;
import org.sc.data.validator.TrailImportValidator;
import org.sc.manager.GpxBulkManager;
import org.sc.manager.TrailFileManager;
import org.sc.manager.TrailImporterManager;
import org.sc.manager.GpxManager;
import org.sc.manager.data.CreateGpxTrailsData;
import org.sc.manager.data.CreateGpxTrailsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;

@RestController
@RequestMapping(TrailImporterController.PREFIX)
public class TrailImporterController {

    public final static String PREFIX = "/import";

    public File uploadDir;

    private final TrailFileManager trailFileManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;
    private final AppProperties appProperties;
    private final GpxBulkManager gpxBulkManager;
    private final ControllerPagination controllerPagination;

    @Autowired
    public TrailImporterController(final TrailFileManager trailFileManager
                                   final GpxBulkManager gpxBulkManager,
                                   final TrailImporterManager trailImporterManager,
                                   final TrailImportValidator trailValidator,
                                   final AppProperties appProperties,
                                   final ControllerPagination controllerPagination) {
        this.trailFileManager = trailFileManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
        this.appProperties = appProperties;
        this.gpxBulkManager = gpxBulkManager;
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
        if (gpxFile == null || gpxFile.getOriginalFilename() == null) {
            return constructResponse(singleton("File is empty"), emptyList(),
                    trailImporterManager.countTrailRaw(),
                    Constants.ZERO, Constants.ONE);
        }

        // TODO: add validation

        final Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        try (final InputStream input = gpxFile.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        final String originalFilename = gpxFile.getOriginalFilename();
        final String uniqueFileName = trailFileManager.makeUniqueFileName(originalFilename);
        final Path rawGpxPath = trailFileManager.saveRawGpx(uniqueFileName, tempFile);
        final TrailRawDto trailPreparationFromGpx = trailFileManager.getTrailRawModel(uniqueFileName, originalFilename, rawGpxPath);
        final List<TrailRawDto> importedRawTrail = trailImporterManager.saveRaw(trailPreparationFromGpx);
        return constructResponse(emptySet(), importedRawTrail, trailImporterManager.countTrailRaw(),
                Constants.ZERO, Constants.ONE);
    }

    @PostMapping(path = "/read-bulk",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailsReadBulkResponseDTO readBulkGpxFile(@RequestParam("files") MultipartFile[] files) throws IOException {


        Map<String, Optional<Path>> nameOptionalTempPathMap = getGPXFilesTempPathList(Arrays.asList(files));

        Map<String, Path> validGPXToInsert = nameOptionalTempPathMap.entrySet().stream().filter(entry -> entry.getValue().isPresent()).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().get()));

        CreateGpxTrailsData createBulkData = new CreateGpxTrailsData();
        createBulkData.getFilesGpxMap().putAll(validGPXToInsert);

        CreateGpxTrailsResult result = gpxBulkManager.createTrailFromGpxBulkImport(createBulkData);

        return buildReadBulkResponse(result);
    }

    private TrailsReadBulkResponseDTO buildReadBulkResponse(CreateGpxTrailsResult result) {

        TrailsReadBulkResponseDTO response = new TrailsReadBulkResponseDTO();

        List<Trail> trails = result.getCreatedTrail().entrySet().stream().filter(value -> value != null).map(x -> x.getValue()).collect(Collectors.toList());
        List<String> filesWithErrorNames = result.getCreatedTrail().entrySet().stream().filter(value -> value == null).map(x -> x.getKey()).collect(Collectors.toList());

        response.setTrailsResult(TrailDTOAssembler.toPreparationModelDTOList(trails));
        response.setFilesNameWithError(filesWithErrorNames);

        return response;
    }

    private Map<String, Optional<Path>> getGPXFilesTempPathList(List<MultipartFile> files) {

        Map<String, Optional<Path>> result = new HashMap<>();

        files.forEach(gpxFile -> {
            try {
                Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
                try (final InputStream input = gpxFile.getInputStream()) {
                    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    result.put(gpxFile.getOriginalFilename(), Optional.of(tempFile));
                }

            } catch (IOException e) {
                result.put(gpxFile.getName(), Optional.empty());
            }
        });

        return result;
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