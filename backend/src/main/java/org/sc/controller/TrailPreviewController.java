package org.sc.controller;

import org.sc.common.rest.controller.RESTResponse;
import org.sc.common.rest.controller.Status;
import org.sc.common.rest.controller.TrailPreviewRestResponse;
import org.sc.manager.TrailManager;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping(TrailPreviewController.PREFIX)
public class TrailPreviewController {

    public final static String PREFIX = "/preview";
    public static final String EMPTY_CODE_VALUE_ERROR_MESSAGE = "Empty code value";

    private final TrailManager trailManager;

    public TrailPreviewController(final TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    @GetMapping
    public TrailPreviewRestResponse getAllPreview() {
        return new TrailPreviewRestResponse(trailManager.allPreview());
    }

    @GetMapping("/{code}")
    public RESTResponse getPreviewByCode(@PathVariable String code) {
        if(StringUtils.hasLength(code)) {
            return new TrailPreviewRestResponse(Collections.emptyList(), Status.ERROR, Collections.singleton(EMPTY_CODE_VALUE_ERROR_MESSAGE));
        }
        return new TrailPreviewRestResponse(trailManager.previewByCode(code));
    }


}
