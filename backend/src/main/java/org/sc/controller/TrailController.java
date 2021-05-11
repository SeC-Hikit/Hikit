package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.RectangleDto;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.response.TrailResponseHelper;
import org.sc.data.validator.*;
import org.sc.manager.TrailImporterManager;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailController.PREFIX)
public class TrailController {

    public final static String PREFIX = "/trail";

    protected final TrailManager trailManager;
    protected final GeneralValidator generalValidator;
    protected final TrailResponseHelper trailResponseHelper;
    protected final TrailImporterManager trailImporterManager;
    protected final AuthFacade authenticationProvider;

    @Autowired
    public TrailController(final TrailManager trailManager,
                           final GeneralValidator generalValidator,
                           final TrailResponseHelper trailResponseHelper,
                           final TrailImporterManager trailImporterManager,
                           final AuthFacade authFacade) {
        this.trailManager = trailManager;
        this.generalValidator = generalValidator;
        this.trailResponseHelper = trailResponseHelper;
        this.trailImporterManager = trailImporterManager;
        this.authenticationProvider = authFacade;
    }

    @Operation(summary = "Retrieve trail")
    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = "false") Boolean light,
            @RequestParam(required = false, defaultValue = "*") String realm) {
        return trailResponseHelper
                .constructResponse(Collections.emptySet(), trailManager.get(light, skip, limit, realm),
                        trailManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve trail by ID")
    @GetMapping("/{id}")
    public TrailResponse getById(@PathVariable String id,
                                 @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return trailResponseHelper
                .constructResponse(Collections.emptySet(), trailManager.getById(id, light),
                        trailManager.count(),
                        Constants.ONE, Constants.ONE);
    }

    @Operation(summary = "Retrieve trail by place ID")
    @GetMapping("/place/{id}")
    public TrailResponse getByPlaceId(@PathVariable String id,
                                      @RequestParam(required = false, defaultValue = "false") Boolean light,
                                      @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                      @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        List<TrailDto> byPlaceRefId = trailManager.getByPlaceRefId(id, light, skip, limit);
        return trailResponseHelper.constructResponse(Collections.emptySet(), byPlaceRefId,
                trailManager.count(),
                skip, limit);
    }

    @Operation(summary = "Count all trails in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailManager.count();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }


    @Operation(summary = "Find geo-located trails within a defined polygon")
    @PostMapping("/geolocate")
    public TrailResponse geoLocateTrail(@RequestBody RectangleDto rectangleDto) {

        final Set<String> errors = generalValidator.validate(rectangleDto);

        if (errors.isEmpty()) {
            final List<TrailDto> foundTrails = trailManager.findTrailsWithinRectangle(rectangleDto);
            return trailResponseHelper.constructResponse(emptySet(), foundTrails,
                    foundTrails.size(), Constants.ZERO, Constants.ONE);
        }

        return trailResponseHelper.constructResponse(errors, emptyList(),
                Constants.ZERO, Constants.ZERO, Constants.ONE);
    }

}
