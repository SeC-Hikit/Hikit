package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.common.rest.response.MediaCreationResponse;
import org.sc.configuration.AppProperties;
import org.sc.data.validator.FileNameValidator;
import org.sc.data.validator.MediaValidator;
import org.sc.manager.MediaManager;
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
@RequestMapping(org.sc.controller.MediaController.PREFIX)
public class MediaController {

    public final static String PREFIX = "/media";

    public File uploadDir;

    private final MediaValidator mediaValidator;
    private final FileNameValidator fileNameValidator;
    private final MediaManager mediaManager;
    private final AppProperties appProperties;


    @Autowired
    public MediaController(final MediaValidator mediaValidator,
                           final FileNameValidator fileNameValidator,
                           final MediaManager mediaManager,
                           final AppProperties appProperties) {
        this.mediaValidator = mediaValidator;
        this.fileNameValidator = fileNameValidator;
        this.mediaManager = mediaManager;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        uploadDir = new File(appProperties.getTempStorage());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MediaCreationResponse upload(@RequestAttribute("file") MultipartFile file) throws IOException {
        final Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        final String originalFileName = file.getOriginalFilename();
        final Set<String> validationErrors =
                fileNameValidator.validate(originalFileName);
        try (final InputStream input = file.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        validationErrors.addAll(mediaValidator.validate(tempFile.toFile()));

        if (validationErrors.isEmpty()) {
            final List<MediaDto> saveResult = mediaManager.save(originalFileName, tempFile);
            return new MediaCreationResponse(Status.OK, Collections.emptySet(), saveResult);
        }
        return new MediaCreationResponse(Status.ERROR, validationErrors, Collections.emptyList());
    }


}
