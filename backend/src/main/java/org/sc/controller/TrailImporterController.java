package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.common.rest.assembler.TrailDTOAssembler;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.AppProperties;
import org.sc.data.model.Trail;
import org.sc.data.validator.TrailImportValidator;
import org.sc.manager.GpxBulkManager;
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

import static java.lang.String.format;

@RestController
@RequestMapping(TrailImporterController.PREFIX)
public class TrailImporterController {

    public final static String PREFIX = "/import";

    public File uploadDir;

    private final GpxManager gpxManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;
    private final AppProperties appProperties;
    private final GpxBulkManager gpxBulkManager;

    @Autowired
    public TrailImporterController(final GpxManager gpxManager,
                                   final GpxBulkManager gpxBulkManager,
                                   final TrailImporterManager trailImporterManager,
                                   final TrailImportValidator trailValidator,
                                   final AppProperties appProperties) {
        this.gpxManager = gpxManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
        this.appProperties = appProperties;
        this.gpxBulkManager = gpxBulkManager;
    }

    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailImporterManager.countImport();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @PostConstruct
    public void init() {
        uploadDir = new File(appProperties.getTempStorage());
    }

    @PostMapping(path = "/read",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailPreparationModelDto readGpxFile(@RequestParam("file") MultipartFile gpxFile) throws IOException {
        final Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        try (final InputStream input = gpxFile.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return gpxManager.getTrailPreparationFromGpx(tempFile);
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

            } catch(IOException e) {
                result.put(gpxFile.getName(), Optional.empty());
            }
        });

        return result;
    }



    @PutMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailResponse importTrail(@RequestBody TrailImportDto request) {
        final Set<String> errors = trailValidator.validate(request);
        if (errors.isEmpty()) {
            List<TrailDto> savedTrail = trailImporterManager.save(request);
            return new TrailResponse(Status.OK, errors, savedTrail);
        }
        return new TrailResponse(Status.ERROR, errors, Collections.emptyList());
    }

}
