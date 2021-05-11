package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.MaintenanceResponse;
import org.sc.controller.response.MaintenanceResponseHelper;
import org.sc.manager.MaintenanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.logging.Logger;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(MaintenanceController.PREFIX)
public class MaintenanceController {

    public final static String PREFIX = "/maintenance";

    private final static Logger LOGGER = Logger
            .getLogger(MaintenanceController.class.getName());

    private final MaintenanceResponseHelper maintenanceResponseHelper;
    private final MaintenanceManager maintenanceManager;

    @Autowired
    public MaintenanceController(final MaintenanceManager maintenanceManager,
                                 final MaintenanceResponseHelper maintenanceResponseHelper) {
        this.maintenanceManager = maintenanceManager;
        this.maintenanceResponseHelper = maintenanceResponseHelper;
    }

    @Operation(summary = "Retrieve all future maintenance")
    @GetMapping("/future")
    public MaintenanceResponse getFutureMaintenance(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return maintenanceResponseHelper
                .constructResponse(emptySet(), maintenanceManager.getFuture(skip, limit),
                        maintenanceManager.countFutureMaintenance(), skip, limit);
    }

    @Operation(summary = "Retrieve all past maintenance")
    @GetMapping("/past")
    public MaintenanceResponse getPastMaintenance(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return maintenanceResponseHelper
                .constructResponse(emptySet(), maintenanceManager.getPast(skip, limit),
                        maintenanceManager.countFutureMaintenance(), skip, limit);
    }

    @Operation(summary = "Retrieve past maintenance by trail ID")
    @GetMapping("/past/{id}")
    public MaintenanceResponse getPastMaintenanceById(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return maintenanceResponseHelper
                .constructResponse(emptySet(), maintenanceManager.getPastMaintenanceForTrailId(id, skip, limit),
                        Constants.ONE, skip, limit);
    }

    @Operation(summary = "Count all past maintenance")
    @GetMapping("/past/count")
    public CountResponse getCountPast() {
        final long count = maintenanceManager.countPastMaintenance();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @Operation(summary = "Count all future maintenance")
    @GetMapping("/future/count")
    public CountResponse getCountFuture() {
        final long count = maintenanceManager.countFutureMaintenance();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @Operation(summary = "Count all maintenance")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = maintenanceManager.countMaintenance();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }
}
