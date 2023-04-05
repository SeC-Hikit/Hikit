package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.PoiResponse;
import org.hikit.common.response.ControllerPagination;
import org.sc.controller.response.PoiResponseHelper;
import org.sc.manager.PoiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(POIController.PREFIX)
public class POIController {

    public final static String PREFIX = "/poi";

    private final PoiManager poiManager;
    private final PoiResponseHelper poiResponseHelper;
    private final ControllerPagination controllerPagination;

    @Autowired
    public POIController(final PoiManager poiManager,
                         final PoiResponseHelper poiResponseHelper,
                         final ControllerPagination controllerPagination) {
        this.poiManager = poiManager;
        this.poiResponseHelper = poiResponseHelper;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Count all POIs in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = poiManager.count();
        return new CountResponse(Status.OK, emptySet(), new CountDto(count));
    }

    @Operation(summary = "Retrieve POIs")
    @GetMapping
    public PoiResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                           @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return poiResponseHelper.constructResponse(emptySet(), poiManager.getPoiPaginated(skip, limit, realm),
                poiManager.countByRealm(realm), skip, limit);
    }

    @Operation(summary = "Retrieve POI by ID")
    @GetMapping("/{id}")
    public PoiResponse get(@PathVariable String id) {
        return poiResponseHelper.constructResponse(emptySet(), poiManager.getPoiByID(id),
                poiManager.count(), Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Retrieve POI by trail id")
    @GetMapping("/trail/{id}")
    public PoiResponse getByTrail(@PathVariable String id,
                                  @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return poiResponseHelper.constructResponse(emptySet(), poiManager.getPoiByTrailId(id, skip, limit),
                poiManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve POI by macro-type")
    @GetMapping("/type/{type}")
    public PoiResponse getByMacro(@PathVariable String type,
                                  @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return poiResponseHelper.constructResponse(emptySet(), poiManager.getPoiByMacro(type, skip, limit),
                poiManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve POI by name or tag")
    @GetMapping("/name/{name}")
    public PoiResponse getByNameOrTags(@PathVariable String name,
                                       @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                       @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return poiResponseHelper.constructResponse(emptySet(), poiManager.getPoiByName(name, skip, limit),
                poiManager.count(), skip, limit);
    }
}
