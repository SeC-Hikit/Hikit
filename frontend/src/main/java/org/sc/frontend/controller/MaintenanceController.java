package org.sc.frontend.controller;

import org.sc.common.rest.controller.MaintenanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceManager maintenanceManager;

    @Autowired
    public MaintenanceController(final MaintenanceManager maintenanceManager) {
        this.maintenanceManager = maintenanceManager;
    }

    @GetMapping("/future")
    public MaintenanceResponse getFutureMaintenance() throws IOException {
        return maintenanceManager.getFutureMaintenance();
    }

    @GetMapping("/past/{from}/{to}")
    public MaintenanceResponse getPastMaintenance(@PathVariable Integer from, @PathVariable Integer to) throws IOException {
        return maintenanceManager.getPastMaintenance(from, to);
    }


}
