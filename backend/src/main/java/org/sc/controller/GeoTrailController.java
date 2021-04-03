package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailIntersectionDto;
import org.sc.common.rest.geo.GeoLineDto;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.data.validator.GeoLineValidator;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(GeoTrailController.PREFIX)
public class GeoTrailController {

    public final static String PREFIX = "/geo-trail";

    private final TrailManager trailManager;
    private final ControllerPagination controllerPagination;
    private final GeoLineValidator geoLineValidator;


    @Autowired
    public GeoTrailController(final TrailManager trailManager,
                              final GeoLineValidator geoLineValidator,
                              final ControllerPagination controllerPagination) {
        this.trailManager = trailManager;
        this.controllerPagination = controllerPagination;
        this.geoLineValidator = geoLineValidator;
    }

    @Operation(summary = "Find all existing trail intersections for a given multi-coordinate line")
    @PostMapping("/intersect")
    public TrailIntersectionResponse findTrailIntersection(@RequestBody GeoLineDto geoLineDto,
                                                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        final Set<String> validate = geoLineValidator.validate(geoLineDto);

        if (!validate.isEmpty()) return constructTrailResponse(validate, emptyList(), 0, skip, limit);

        final List<TrailIntersectionDto> intersections =
                trailManager.findIntersection(geoLineDto, skip, limit);
        return constructTrailResponse(emptySet(), intersections, intersections.size(), skip, limit);
    }

    private TrailIntersectionResponse constructTrailResponse(final Set<String> errors,
                                                             final List<TrailIntersectionDto> dtos,
                                                             final long totalCount,
                                                             final int skip,
                                                             final int limit) {
        if (!errors.isEmpty()) {
            return new TrailIntersectionResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailIntersectionResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
