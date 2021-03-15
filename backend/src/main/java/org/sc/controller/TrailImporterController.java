package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.common.rest.response.TrailResponse;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(TrailImporterController.PREFIX)
public class TrailImporterController {

    public final static String PREFIX = "/import";

    public File uploadDir;

    private final TrailFileManager trailFileManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;
    private final AppProperties appProperties;

    @Autowired
    public TrailImporterController(final TrailFileManager trailFileManager,
                                   final TrailImporterManager trailImporterManager,
                                   final TrailImportValidator trailValidator,
                                   final AppProperties appProperties) {
        this.trailFileManager = trailFileManager;
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
    public TrailRawResponse readGpxFile(@RequestAttribute("file") MultipartFile gpxFile) throws IOException {
        if(gpxFile == null || gpxFile.getOriginalFilename() == null) {
            return new TrailRawResponse(Status.ERROR, Collections.singleton("File is empty"),
                    Collections.emptyList());
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
        return new TrailRawResponse(Status.OK, Collections.emptySet(), trailImporterManager.saveRaw(trailPreparationFromGpx));
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

    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailImporterManager.countImport();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

}
