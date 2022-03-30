package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.response.TrailRawResponseHelper;
import org.sc.manager.TrailRawManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(TrailRawController.PREFIX)
public class TrailRawController {

    public static final String PREFIX = "raw";

    private final TrailRawManager trailRawManager;
    private final TrailRawResponseHelper trailRawResponseHelper;
    private final ControllerPagination controllerPagination;

    public TrailRawController(final TrailRawManager trailRawManager,
                              final TrailRawResponseHelper trailRawResponseHelper,
                              final ControllerPagination controllerPagination) {
        this.trailRawManager = trailRawManager;
        this.trailRawResponseHelper = trailRawResponseHelper;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Retrieve raw trails")
    @GetMapping
    public TrailRawResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm
    ) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailRawResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailRawManager.get(skip, limit, realm),
                        trailRawManager.count(realm), skip, limit);
    }

    @Operation(summary = "Retrieve a single raw trail")
    @GetMapping("/{id}")
    public TrailRawResponse getById(final @PathVariable String id) {
        return trailRawResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailRawManager.getById(id),
                        Constants.ONE,
                        Constants.ZERO, Constants.ONE);
    }
}
