package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.sc.common.rest.MediaDto;
import org.sc.common.rest.response.MediaResponse;
import org.sc.configuration.AppProperties;
import org.sc.controller.response.MediaResponseHelper;
import org.sc.data.validator.GeneralValidator;
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

import static org.sc.controller.admin.Constants.PREFIX_MEDIA;

@RestController
@RequestMapping(PREFIX_MEDIA)
public class AdminMediaController {
    public final static String PREFIX = "/media";
    public static final String EMPTY_ID_ERROR = "Empty Id";
    public static final String FILE_IS_EMPTY_ERROR = "File is empty";

    private final GeneralValidator generalValidator;
    private final MediaManager mediaManager;
    private final AppProperties appProperties;
    private final MediaResponseHelper mediaResponseHelper;

    public File uploadDir;

    @PostConstruct
    public void init() {
        uploadDir = new File(appProperties.getTempStorage());
    }

    @Autowired
    public AdminMediaController(final GeneralValidator generalValidator,
                           final MediaManager mediaManager,
                           final AppProperties appProperties,
                           final MediaResponseHelper mediaResponseHelper) {
        this.generalValidator = generalValidator;
        this.mediaManager = mediaManager;
        this.appProperties = appProperties;
        this.mediaResponseHelper = mediaResponseHelper;
    }

    @Operation(summary = "Add media")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MediaResponse upload(@RequestAttribute("file") MultipartFile file) throws IOException {
        if (file == null || file.getOriginalFilename() == null) {
            return mediaResponseHelper
                    .constructResponse(Collections.singleton(FILE_IS_EMPTY_ERROR),
                            Collections.emptyList(), mediaManager.count(),
                            org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        final String originalFileName = file.getOriginalFilename();
        final String extension = mediaManager.getExtensionFromName(originalFileName);
        final Path tempFile = Files.createTempFile(uploadDir.toPath(), "", extension);
        final Set<String> validationErrors =
                generalValidator.validateFileName(originalFileName);
        try (final InputStream input = file.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        validationErrors.addAll(generalValidator.validate(tempFile.toFile()));

        if (validationErrors.isEmpty()) {
            final List<MediaDto> saveResult = mediaManager.save(originalFileName, tempFile);
            return mediaResponseHelper
                    .constructResponse(Collections.emptySet(), saveResult, mediaManager.count(),
                            org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return mediaResponseHelper
                .constructResponse(validationErrors, Collections.emptyList(), mediaManager.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Remove media")
    @DeleteMapping("/{id}")
    public MediaResponse deleteById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return mediaResponseHelper
                    .constructResponse(Collections.singleton(EMPTY_ID_ERROR),
                            Collections.emptyList(), mediaManager.count(),
                            org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        List<MediaDto> medias = mediaManager.deleteById(id);
        return mediaResponseHelper
                .constructResponse(Collections.emptySet(), medias, mediaManager.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }
}
