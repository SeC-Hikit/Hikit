package org.sc.controller;

import org.sc.common.rest.controller.Maintenance;
import org.sc.common.rest.controller.MaintenanceResponse;
import org.sc.common.rest.controller.RESTResponse;
import org.sc.common.rest.controller.Status;
import org.sc.data.MaintenanceDAO;
import org.sc.importer.MaintenanceCreationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;

@RestController
@RequestMapping(MaintenanceController.PREFIX)
public class MaintenanceController {

    public final static String PREFIX = "/maintenance";
    private final static Logger LOGGER = Logger.getLogger(MaintenanceController.class.getName());

    private final MaintenanceCreationValidator maintenanceValidator;
    private final MaintenanceDAO maintenanceDao;


    @Autowired
    public MaintenanceController(final MaintenanceDAO maintenanceDao,
                                 final MaintenanceCreationValidator maintenanceValidator) {
        this.maintenanceDao = maintenanceDao;
        this.maintenanceValidator = maintenanceValidator;
    }

    @GetMapping("/future")
    public MaintenanceResponse getFutureMaintenance() {
        return new MaintenanceResponse(maintenanceDao.getFuture());
    }

    @GetMapping("/past")
    public MaintenanceResponse getPastMaintenance() {
        return new MaintenanceResponse(maintenanceDao.getPast());
    }

    @DeleteMapping("/delete/{code}")
    public RESTResponse deleteMaintenance(@PathVariable String id) {
        boolean isDeleted = maintenanceDao.delete(id);
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            LOGGER.warning(format("Could not delete maintenance with id %s", id));
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No maintenance was found with id '%s'", id))));
        }
    }

    @PutMapping("/save")
    public RESTResponse createMaintenance(@RequestBody Maintenance request) {
        final Set<String> errors = maintenanceValidator.validate(request);
        if(errors.isEmpty()) {
            maintenanceDao.upsert(request);
            return new RESTResponse();
        }
        return new RESTResponse(errors);
    }
}
