package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.TrailIntersectionDto;
import org.sc.common.rest.TrailMappingDto;
import org.sc.common.rest.geo.GeoLineDto;
import org.sc.common.rest.geo.RectangleDto;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.common.rest.response.TrailMappingResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.controller.response.TrailIntersectionHelper;
import org.sc.controller.response.TrailPreviewResponseHelper;
import org.sc.controller.response.TrailResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.TrailManager;
import org.sc.processor.TrailSimplifierLevel;
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
    private final TrailIntersectionHelper trailIntersectionHelper;
    private final TrailResponseHelper trailResponseHelper;
    private final GeneralValidator generalValidator;
    private final ControllerPagination controllerPagination;
    private final TrailPreviewResponseHelper trailPreviewRespHelper;

    @Autowired
    public GeoTrailController(final TrailManager trailManager,
                              final GeneralValidator generalValidator,
                              final TrailIntersectionHelper trailIntersectionHelper,
                              final TrailResponseHelper trailResponseHelper,
                              final TrailPreviewResponseHelper trailPreviewResponseHelper,
                              final ControllerPagination controllerPagination) {
        this.trailManager = trailManager;
        this.trailIntersectionHelper = trailIntersectionHelper;
        this.generalValidator = generalValidator;
        this.trailResponseHelper = trailResponseHelper;
        this.controllerPagination = controllerPagination;
        this.trailPreviewRespHelper = trailPreviewResponseHelper;
    }

    @Operation(summary = "Find all existing trail intersections for a given multi-coordinate line")
    @PostMapping("/intersect")
    public TrailIntersectionResponse findTrailIntersection(@RequestBody GeoLineDto geoLineDto,
                                                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        final Set<String> validate = generalValidator.validate(geoLineDto);
        controllerPagination.checkSkipLim(skip, limit);

        if (!validate.isEmpty()) return trailIntersectionHelper.constructResponse(validate, emptyList(), 0, skip, limit);

        final List<TrailIntersectionDto> intersections =
                trailManager.findIntersection(geoLineDto, skip, limit);
        return trailIntersectionHelper.constructResponse(emptySet(), intersections, intersections.size(), skip, limit);
    }

    @Operation(summary = "Find geo-located trails within a defined rectangle")
    @PostMapping("/locate")
    public TrailResponse geoLocateTrail(@RequestBody RectangleDto rectangleDto,
                                        @RequestParam(defaultValue = "MEDIUM") TrailSimplifierLevel level, @RequestParam(defaultValue = "false") boolean isDraftTrailVisible) {

        final Set<String> errors = generalValidator.validate(rectangleDto);

        if (errors.isEmpty()) {
            final List<TrailDto> foundTrails = trailManager.findTrailsWithinRectangle(rectangleDto, level, isDraftTrailVisible);
            return trailResponseHelper.constructResponse(emptySet(), foundTrails,
                    foundTrails.size(), Constants.ZERO, Constants.ONE);
        }

        return trailResponseHelper.constructResponse(errors, emptyList(),
                Constants.ZERO, Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Find geo-located trails mapping IDs within a defined rectangle")
    @PostMapping("/locate-id")
    public TrailMappingResponse geoLocateTrail(@RequestBody RectangleDto rectangleDto){

        final Set<String> errors = generalValidator.validate(rectangleDto);
        if(!errors.isEmpty()) {
            return new TrailMappingResponse(Status.ERROR, errors, emptyList(), 1L,
                    Constants.ONE, 0, 100);
        }
        final List<TrailMappingDto> dtos = trailManager.findTrailMappingsWithinRectangle(rectangleDto);
        return trailPreviewRespHelper.constructMappingResponse(errors, dtos, dtos.size(), 0, 100);
    }

}
