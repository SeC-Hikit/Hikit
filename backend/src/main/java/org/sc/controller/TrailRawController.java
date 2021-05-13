package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailRawDto;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.response.TrailRawResponseHelper;
import org.sc.manager.TrailRawManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailRawController.PREFIX)
public class TrailRawController {

    public static final String PREFIX = "raw";

    private final TrailRawManager trailRawManager;
    private final TrailRawResponseHelper trailRawResponseHelper;

    public TrailRawController(final TrailRawManager trailRawManager,
                              final TrailRawResponseHelper trailRawResponseHelper) {
        this.trailRawManager = trailRawManager;
        this.trailRawResponseHelper = trailRawResponseHelper;
    }

    @Operation(summary = "Retrieve raw trails")
    @GetMapping
    public TrailRawResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit
    ) {
        return trailRawResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailRawManager.get(skip, limit),
                        trailRawManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve a single raw trail")
    @GetMapping("/{id}")
    public TrailRawResponse getById(final @PathVariable String id) {
        return trailRawResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailRawManager.getById(id),
                        trailRawManager.count(),
                        Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Delete a single raw trail")
    @DeleteMapping("/{id}")
    public TrailRawResponse deleteById(final @PathVariable String id) {
        return trailRawResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailRawManager.deleteById(id),
                        trailRawManager.count(),
                        Constants.ZERO, Constants.ONE);
    }
}
