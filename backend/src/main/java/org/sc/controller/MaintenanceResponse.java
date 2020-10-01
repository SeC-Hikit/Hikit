package org.sc.controller;

import org.sc.data.Maintenance;
import org.sc.data.Trail;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MaintenanceResponse extends RESTResponse {
    final List<Maintenance> maintenanceList;

    public MaintenanceResponse(List<Maintenance> maintenanceList) {
        super(Status.OK, Collections.emptySet());
        this.maintenanceList = maintenanceList;
    }

    public List<Maintenance> getMaintenanceList() {
        return maintenanceList;
    }

}
