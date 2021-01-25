package org.sc.controller;

import org.sc.common.rest.Status;
import org.sc.common.rest.response.TrailPreviewResponse;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailPreviewController.PREFIX)
public class TrailPreviewController {

    public final static String PREFIX = "/preview";

    private final TrailManager trailManager;

    @Autowired
    public TrailPreviewController(final TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    @GetMapping
    public TrailPreviewResponse getAllPreview(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                              @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new TrailPreviewResponse(Status.OK, Collections.emptySet(), trailManager.getPreviews(page, count));
    }

    @GetMapping("/{code}")
    public TrailPreviewResponse getPreviewByCode(@PathVariable String code) {
        return new TrailPreviewResponse(Status.OK, Collections.emptySet(), trailManager.previewByCode(code));
    }


}
