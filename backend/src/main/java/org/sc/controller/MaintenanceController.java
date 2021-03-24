package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.MaintenanceCreationDto;
import org.sc.common.rest.MaintenanceDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.MaintenanceResponse;
import org.sc.data.validator.MaintenanceValidator;
import org.sc.manager.MaintenanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(MaintenanceController.PREFIX)
public class MaintenanceController {

    public final static String PREFIX = "/maintenance";

    private final static Logger LOGGER = Logger
            .getLogger(MaintenanceController.class.getName());

    private final MaintenanceValidator maintenanceValidator;
    private final ControllerPagination controllerPagination;
    private final MaintenanceManager maintenanceManager;

    @Autowired
    public MaintenanceController(final MaintenanceManager maintenanceManager,
                                 final MaintenanceValidator maintenanceValidator,
                                 final ControllerPagination controllerPagination) {
        this.maintenanceManager = maintenanceManager;
        this.maintenanceValidator = maintenanceValidator;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Retrieve all future maintenance")
    @GetMapping("/future")
    public MaintenanceResponse getFutureMaintenance(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), maintenanceManager.getFuture(skip, limit),
                maintenanceManager.countFutureMaintenance(), skip, limit);
    }

    @Operation(summary = "Retrieve all past maintenance")
    @GetMapping("/past")
    public MaintenanceResponse getPastMaintenance(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), maintenanceManager.getPast(skip, limit),
                maintenanceManager.countFutureMaintenance(), skip, limit);
    }

    @Operation(summary = "Retrieve past maintenance by trail ID")
    @GetMapping("/past/{id}")
    public MaintenanceResponse getPastMaintenanceById(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), maintenanceManager.getPastMaintenanceForTrailId(id, skip, limit),
                Constants.ONE, skip, limit);
    }

    @Operation(summary = "Create a new maintenance")
    @PutMapping
    public MaintenanceResponse create(
            @RequestBody MaintenanceCreationDto request) {
        final Set<String> errors = maintenanceValidator.validate(request);
        if (errors.isEmpty()) {
            return constructResponse(emptySet(), maintenanceManager.upsert(request),
                    Constants.ONE, Constants.ZERO, Constants.ONE);
        }
        return constructResponse(errors, emptyList(),
                Constants.ZERO, Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Delete maintenance")
    @DeleteMapping("/{id}")
    public MaintenanceResponse deleteMaintenance(
            @PathVariable String id) {
        List<MaintenanceDto> deleted = maintenanceManager.delete(id);
        if (deleted.isEmpty()) {
            return constructResponse(Collections.singleton("No maintenance was found with id '%s'"),
                    deleted, Constants.ZERO, Constants.ZERO, Constants.ONE);
        }
        return constructResponse(emptySet(),
                deleted, Constants.ONE, Constants.ZERO, Constants.ONE);
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

    private MaintenanceResponse constructResponse(Set<String> errors,
                                                  List<MaintenanceDto> dtos,
                                                  long totalCount,
                                                  int skip,
                                                  int limit) {
        if (!errors.isEmpty()) {
            return new MaintenanceResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new MaintenanceResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
