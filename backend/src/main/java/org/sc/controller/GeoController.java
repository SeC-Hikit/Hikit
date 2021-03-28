package org.sc.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.geo.GeoMultilineDto;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collections;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(GeoController.PREFIX)
public class GeoController {

    private final TrailManager trailManager;

    @Autowired
    public GeoController(TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    public final static String PREFIX = "/geo";

    @Operation(summary = "Find all existing trail intersections for a given GEO-JSON multi-line")
    @PostMapping("/intersect")
    public TrailIntersectionResponse findTrailIntersection(@RequestBody GeoMultilineDto geoMultilineDto,
                                                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        // TODO: add validation

        trailManager.findIntersection(geoMultilineDto, skip, limit);

        // TODO
        throw new NotImplementedException();
    }
}
