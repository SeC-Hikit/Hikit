package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.MaintenanceResponse;
import org.hikit.common.response.ControllerPagination;
import org.sc.controller.response.MaintenanceResponseHelper;
import org.sc.manager.MaintenanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.logging.Logger;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(MaintenanceController.PREFIX)
public class MaintenanceController {

    public final static String PREFIX = "/maintenance";

    private final static Logger LOGGER = Logger
            .getLogger(MaintenanceController.class.getName());

    private final MaintenanceResponseHelper maintenanceResponseHelper;
    private final MaintenanceManager maintenanceManager;
    private final ControllerPagination controllerPagination;

    @Autowired
    public MaintenanceController(final MaintenanceManager maintenanceManager,
                                 final MaintenanceResponseHelper maintenanceResponseHelper,
                                 final ControllerPagination controllerPagination) {
        this.maintenanceManager = maintenanceManager;
        this.maintenanceResponseHelper = maintenanceResponseHelper;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Retrieve all future maintenance")
    @GetMapping("/future")
    public MaintenanceResponse getFutureMaintenance(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return maintenanceResponseHelper
                .constructResponse(emptySet(), maintenanceManager.getFuture(skip, limit, realm),
                        maintenanceManager.countFutureMaintenance(realm), skip, limit);
    }

    @Operation(summary = "Retrieve all past maintenance")
    @GetMapping("/past")
    public MaintenanceResponse getPastMaintenance(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return maintenanceResponseHelper
                .constructResponse(emptySet(), maintenanceManager.getPast(skip, limit, realm),
                        maintenanceManager.countFutureMaintenance(realm), skip, limit);
    }

    @Operation(summary = "Retrieve past maintenance by trail ID")
    @GetMapping("/past/{id}")
    public MaintenanceResponse getPastMaintenanceById(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return maintenanceResponseHelper
                .constructResponse(emptySet(), maintenanceManager.getPastMaintenanceForTrailId(id, skip, limit),
                        Constants.ONE, skip, limit);
    }

    @Operation(summary = "Count all past maintenance")
    @GetMapping("/past/count")
    public CountResponse getCountPast(
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        final long count = maintenanceManager.countPastMaintenance(realm);
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @Operation(summary = "Count all future maintenance")
    @GetMapping("/future/count")
    public CountResponse getCountFuture(
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        final long count = maintenanceManager.countFutureMaintenance(realm);
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @Operation(summary = "Count all maintenance")
    @GetMapping("/count")
    public CountResponse getCount(
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        final long count = maintenanceManager.countMaintenance(realm);
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }
}
