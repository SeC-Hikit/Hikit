package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.sc.common.rest.MediaDto;
import org.sc.common.rest.response.MediaResponse;
import org.hikit.common.response.ControllerPagination;
import org.sc.controller.response.MediaResponseHelper;
import org.sc.manager.MediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(org.sc.controller.MediaController.PREFIX)
public class MediaController {

    public final static String PREFIX = "/media";
    public static final String EMPTY_ID_ERROR = "Empty Id";

    private final MediaManager mediaManager;
    private final MediaResponseHelper mediaResponseHelper;
    private final ControllerPagination controllerPagination;

    @Autowired
    public MediaController(final MediaManager mediaManager,
                           final MediaResponseHelper mediaResponseHelper,
                           final ControllerPagination controllerPagination) {
        this.mediaManager = mediaManager;
        this.mediaResponseHelper = mediaResponseHelper;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Retrieve media")
    @GetMapping
    public MediaResponse getMedia(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                 @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                                 @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return mediaResponseHelper
                .constructResponse(emptySet(), mediaManager.getMedia(skip, limit, realm),
                        mediaManager.countMedia(realm), skip, limit);
    }

    @Operation(summary = "Retrieve media by ID")
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
