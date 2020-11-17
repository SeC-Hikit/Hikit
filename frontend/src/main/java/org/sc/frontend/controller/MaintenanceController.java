package org.sc.frontend.controller;

import com.google.inject.Inject;
import org.sc.common.rest.controller.JsonHelper;
import org.sc.common.rest.controller.MaintenanceResponse;
import org.sc.common.rest.controller.PublicController;
import spark.Request;
import spark.Response;

import java.io.IOException;

import static java.lang.String.format;
import static org.sc.frontend.controller.TrailController.PREFIX;
import static spark.Spark.get;

public class MaintenanceController implements PublicController {


    private final MaintenanceManager maintenanceManager;

    @Inject
    public MaintenanceController(final MaintenanceManager maintenanceManager) {
        this.maintenanceManager = maintenanceManager;
    }

    private MaintenanceResponse getFutureMaintenance(Request request, Response response) throws IOException {
        return maintenanceManager.getFutureMaintenance();
    }

    private MaintenanceResponse getPastMaintenance(Request request, Response response) throws IOException {
        final String from = request.params(":from");
        final String to = request.params(":to");
        return maintenanceManager.getPastMaintenance(Integer.parseInt(from), Integer.parseInt(to));
    }

    public void init() {
        get(format("%s/maintenance/future", PREFIX), this::getFutureMaintenance, JsonHelper.json());
        get(format("%s/maintenance/past/:from/:to", PREFIX), this::getPastMaintenance, JsonHelper.json());
    }

}
