package org.sc.controller;

import org.sc.common.rest.RESTResponse;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailPreparationModel;
import org.sc.common.rest.TrailResponse;
import org.sc.data.TrailImport;
import org.sc.data.validator.TrailImportValidator;
import org.sc.data.validator.TrailImporterManager;
import org.sc.service.GpxManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Set;

import static java.lang.String.format;

@RestController
@RequestMapping(TrailImporterController.PREFIX)
public class TrailImporterController {

    public final static String PREFIX = "/import";

    public static final String TMP_FOLDER = "tmp";
    public static final File UPLOAD_DIR = new File(TMP_FOLDER);

    private final GpxManager gpxManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;

    @Autowired
    public TrailImporterController(final GpxManager gpxManager,
                                   final TrailImporterManager trailImporterManager,
                                   final TrailImportValidator trailValidator) {
        this.gpxManager = gpxManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
    }

    @PostMapping(path = "/read",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailPreparationModel readGpxFile(@RequestAttribute("file") MultipartFile gpxFile) throws IOException {
        final Path tempFile = Files.createTempFile(UPLOAD_DIR.toPath(), "", "");
        try (final InputStream input = gpxFile.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return gpxManager.getTrailPreparationFromGpx(tempFile);
    }

    @PutMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RESTResponse importTrail(@RequestBody TrailImport request) {
        final Set<String> errors = trailValidator.validate(request);
        final TrailResponse.TrailRestResponseBuilder trailRestResponseBuilder = TrailResponse.
                TrailRestResponseBuilder.aTrailRestResponse().withTrails(Collections.emptyList()).withMessages(errors);
        if (errors.isEmpty()) {
            trailImporterManager.save(request);
            return trailRestResponseBuilder.withStatus(Status.OK).build();
        }
        return trailRestResponseBuilder.withMessages(errors).withStatus(Status.ERROR).build();
    }

}
