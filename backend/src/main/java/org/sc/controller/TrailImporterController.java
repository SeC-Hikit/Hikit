package org.sc.controller;

import org.sc.common.rest.Status;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.TrailImportDto;
import org.sc.common.rest.TrailPreparationModelDto;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.AppProperties;
import org.sc.data.TrailImport;
import org.sc.data.validator.TrailImportValidator;
import org.sc.manager.TrailImporterManager;
import org.sc.manager.GpxManager;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @Autowired
    public TrailImporterController(final GpxManager gpxManager,
                                   final TrailImporterManager trailImporterManager,
                                   final TrailImportValidator trailValidator,
                                   final AppProperties appProperties) {
        this.gpxManager = gpxManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        uploadDir = new File(appProperties.getTempStorage());
    }

    @PostMapping(path = "/read",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailPreparationModelDto readGpxFile(@RequestAttribute("file") MultipartFile gpxFile) throws IOException {
        final Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        try (final InputStream input = gpxFile.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return gpxManager.getTrailPreparationFromGpx(tempFile);
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
