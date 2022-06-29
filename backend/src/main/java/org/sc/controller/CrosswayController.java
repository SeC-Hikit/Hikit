package org.sc.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.controller.response.ControllerPagination;
import org.sc.controller.response.PlaceResponseHelper;
import org.sc.service.PlaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(AccessibilityNotificationController.PREFIX)
public class CrosswayController {

    public final static String PREFIX = "/crossway";

    private final PlaceResponseHelper placeResponseHelper;
    private PlaceService placeService;

    public CrosswayController(PlaceResponseHelper placeResponseHelper,
                              PlaceService placeService) {
        this.placeResponseHelper = placeResponseHelper;
        this.placeService = placeService;
    }

    @Operation(summary = "Retrieve dynamic crossways")
    @GetMapping
    public PlaceResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                             @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                             @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        return placeResponseHelper.constructResponse(emptySet(),
                placeService.fetchPaginated(skip, limit, realm), placeService.countByRealm(realm), skip, limit);
    }


}
