package org.sc.controller;

import org.sc.common.rest.Maintenance;
import org.sc.common.rest.MaintenanceResponse;
import org.sc.common.rest.RESTResponse;
import org.sc.common.rest.Status;
import org.sc.data.repository.MaintenanceDAO;
import org.sc.data.validator.MaintenanceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(MaintenanceController.PREFIX)
public class MaintenanceController {

    public final static String PREFIX = "/maintenance";
    private final static Logger LOGGER = Logger.getLogger(MaintenanceController.class.getName());

    private final MaintenanceValidator maintenanceValidator;
    private final MaintenanceDAO maintenanceDao;


    @Autowired
    public MaintenanceController(final MaintenanceDAO maintenanceDao,
                                 final MaintenanceValidator maintenanceValidator) {
        this.maintenanceDao = maintenanceDao;
        this.maintenanceValidator = maintenanceValidator;
    }

    @GetMapping("/future")
    public MaintenanceResponse getFutureMaintenance(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                                    @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new MaintenanceResponse(maintenanceDao.getFuture(page, count));
    }

    @GetMapping("/past")
    public MaintenanceResponse getPastMaintenance(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new MaintenanceResponse(maintenanceDao.getPast(page, count));
    }

    @DeleteMapping("/{id}")
    public RESTResponse deleteMaintenance(@PathVariable String id) {
        boolean isDeleted = maintenanceDao.delete(id);
        if (!isDeleted) {
            LOGGER.warning(format("Could not delete maintenance with id '%s'", id));
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No maintenance was found with id '%s'", id))));
        }
        return new RESTResponse();
    }

    @PutMapping
    public RESTResponse upsertMaintenance(@RequestBody Maintenance request) {
        final Set<String> errors = maintenanceValidator.validate(request);
        if(errors.isEmpty()) {
            maintenanceDao.upsert(request);
            return new RESTResponse();
        }
        return new RESTResponse(errors);
    }
}
