package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.PlaceResponse;
import org.hikit.common.response.ControllerPagination;
import org.sc.controller.response.PlaceResponseHelper;
import org.sc.data.validator.*;
import org.sc.manager.PlaceManager;
import org.sc.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(PlaceController.PREFIX)
public class PlaceController {
    public final static String PREFIX = "/place";

    private final PlaceManager placeManager;
    private final PlaceResponseHelper placeResponseHelper;
    private final ControllerPagination controllerPagination;
    private final GeneralValidator generalValidator;
    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceManager placeManager,
                           PlaceResponseHelper placeResponseHelper,
                           ControllerPagination controllerPagination,
                           GeneralValidator generalValidator, PlaceService placeService) {
        this.placeManager = placeManager;
        this.placeResponseHelper = placeResponseHelper;
        this.controllerPagination = controllerPagination;
        this.generalValidator = generalValidator;
        this.placeService = placeService;
    }

    @Operation(summary = "Retrieve places")
    @GetMapping
    public PlaceResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                             @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                             @RequestParam(required = false) Boolean isDynamicShowing,
                             @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return placeResponseHelper.constructResponse(emptySet(),
                placeService.fetchPaginated(skip, limit, realm, isDynamicShowing),
                placeService.countByRealm(realm, isDynamicShowing), skip, limit);
    }

    @Operation(summary = "Retrieve place by ID")
    @GetMapping("/{id}")
    public PlaceResponse get(@PathVariable String id) {
        return placeResponseHelper.constructResponse(emptySet(),
                placeManager.getById(id),
                placeManager.count(),
                Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Retrieve place by alternative names or tags")
    @GetMapping("/name/{name}")
    public PlaceResponse getLikeNameOrTags(@PathVariable String name,
                                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                           @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return placeResponseHelper.constructResponse(emptySet(),
                placeManager.getLikeNameOrTags(name, skip, limit, realm),
                placeManager.countByNameOrTags(name, realm), skip, limit);
    }

    @Operation(summary = "Geo-locate places based on their location and a given radius range, specified in meters")
    @PostMapping("/geolocate")
    public PlaceResponse geolocatePlace(@RequestBody PointGeolocationDto pointGeolocationDto,
                                        @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                        @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        final Set<String> errors = generalValidator.validate(pointGeolocationDto);

        if (errors.isEmpty()) {
            final CoordinatesDto coordinatesDto = pointGeolocationDto.getCoordinatesDto();
            final List<PlaceDto> results = placeManager.getNearPoint(
                    coordinatesDto.getLongitude(), coordinatesDto.getLatitude(),
                    pointGeolocationDto.getDistance(), skip, limit);
            return placeResponseHelper.constructResponse(emptySet(),
                    results, results.size(), skip, limit);
        }
        return placeResponseHelper.constructResponse(errors, emptyList(),
                placeManager.count(), skip, limit);
    }

}
