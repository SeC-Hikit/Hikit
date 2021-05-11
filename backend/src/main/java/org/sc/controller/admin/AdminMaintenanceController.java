package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.MaintenanceDto;
import org.sc.common.rest.response.MaintenanceResponse;
import org.sc.controller.Constants;
import org.sc.controller.MaintenanceController;
import org.sc.controller.response.MaintenanceResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.MaintenanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.controller.admin.Constants.PREFIX_MAINTENANCE;

@RestController
@RequestMapping(PREFIX_MAINTENANCE)
public class AdminMaintenanceController {

    private final static Logger LOGGER = Logger
            .getLogger(MaintenanceController.class.getName());

    private final GeneralValidator generalValidator;
    private final MaintenanceResponseHelper maintenanceResponseHelper;
    private final MaintenanceManager maintenanceManager;

    @Autowired
    public AdminMaintenanceController(final MaintenanceManager maintenanceManager,
                                      final GeneralValidator generalValidator,
                                      final MaintenanceResponseHelper maintenanceResponseHelper) {
        this.maintenanceManager = maintenanceManager;
        this.generalValidator = generalValidator;
        this.maintenanceResponseHelper = maintenanceResponseHelper;
    }

    @Operation(summary = "Create a new maintenance")
    @PutMapping
    public MaintenanceResponse create(
            @RequestBody MaintenanceDto request) {
        final Set<String> errors = generalValidator.validate(request);
        if (errors.isEmpty()) {
            return maintenanceResponseHelper
                    .constructResponse(emptySet(), maintenanceManager.create(request),
                            org.sc.controller.Constants.ONE, org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return maintenanceResponseHelper
                .constructResponse(errors, emptyList(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Delete maintenance")
    @DeleteMapping("/{id}")
    public MaintenanceResponse deleteMaintenance(
            @PathVariable String id) {
        final Set<String> errors = generalValidator.validateUpdateMaintenance(id);

        if (errors.isEmpty()) {
            final List<MaintenanceDto> deleted =
                    maintenanceManager.delete(id);
            return maintenanceResponseHelper
                    .constructResponse(emptySet(),
                            deleted, Constants.ONE, Constants.ZERO, Constants.ONE);
        }
        return maintenanceResponseHelper
                .constructResponse(errors,
                        emptyList(),
                        Constants.ZERO, Constants.ZERO, Constants.ONE);
    }
}
