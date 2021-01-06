package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.manager.TrailManager;
import org.sc.service.GpxManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailController.PREFIX)
public class TrailController {

    public final static String PREFIX = "/trail";

    public static final String EMPTY_CODE_VALUE_ERROR_MESSAGE = "Empty code value";

    public static final String TMP_FOLDER = "tmp";
    public static final File UPLOAD_DIR = new File(TMP_FOLDER);

    private final GpxManager gpxManager;
    private final TrailManager trailManager;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;

    @Autowired
    public TrailController(final GpxManager gpxManager,
                           final TrailManager trailManager,
                           final TrailImporterManager trailImporterManager,
                           final TrailImportValidator trailValidator) {
        this.gpxManager = gpxManager;
        this.trailManager = trailManager;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
    }

    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count,
            @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(trailManager.get(light, page, count));
    }

    @GetMapping("/{code}")
    public TrailResponse getByCode(@PathVariable String code, @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(trailManager.getByCode(code, light));
    }

    @DeleteMapping("/{code}")
    public RESTResponse deleteByCode(@PathVariable String code,
                                     @RequestParam(required = false, defaultValue = "false") boolean isPurged) {
        boolean isDeleted = trailManager.delete(code, isPurged);
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No trail deleted with code '%s'", code))));
        }
    }

    @GetMapping("/download/{code}")
    public FileDownloadResponse getDownloadableLink(@PathVariable String code) {
        if (!StringUtils.hasText(code)) {
            return new FileDownloadResponse("", Status.ERROR, Collections.singleton(EMPTY_CODE_VALUE_ERROR_MESSAGE));
        }
        final List<Trail> byCode = trailManager.getByCode(code, false);
        if (!byCode.isEmpty()) {
            return new FileDownloadResponse(trailManager.getDownloadableLink(code));
        }
        return new FileDownloadResponse("", Status.ERROR, Collections.singleton("Trail does not exist"));
    }

}
