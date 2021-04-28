package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.sc.common.rest.MediaDto;
import org.sc.common.rest.response.MediaResponse;
import org.sc.controller.response.MediaResponseHelper;
import org.sc.manager.MediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(org.sc.controller.MediaController.PREFIX)
public class MediaController {

    public final static String PREFIX = "/media";
    public static final String EMPTY_ID_ERROR = "Empty Id";

    private final MediaManager mediaManager;
    private final MediaResponseHelper mediaResponseHelper;

    @Autowired
    public MediaController(final MediaManager mediaManager,
                           final MediaResponseHelper mediaResponseHelper) {
        this.mediaManager = mediaManager;
        this.mediaResponseHelper = mediaResponseHelper;
    }

    @Operation(summary = "Retrieve media")
    @GetMapping("/{id}")
    public MediaResponse getById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return mediaResponseHelper
                    .constructResponse(Collections.singleton(EMPTY_ID_ERROR),
                            Collections.emptyList(), mediaManager.count(),
                            Constants.ZERO, Constants.ONE);
        }
        List<MediaDto> medias = mediaManager.getById(id);
        return mediaResponseHelper
                .constructResponse(Collections.emptySet(), medias, mediaManager.count(),
                        Constants.ZERO, Constants.ONE);
    }
}
