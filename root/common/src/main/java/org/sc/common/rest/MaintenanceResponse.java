package org.sc.common.rest;

import java.util.Collections;
import java.util.List;

public class MaintenanceResponse extends RESTResponse {
    List<Maintenance> maintenanceList;

    public MaintenanceResponse(){}

    public MaintenanceResponse(List<Maintenance> maintenanceList) {
        super(Status.OK, Collections.emptySet());
        this.maintenanceList = maintenanceList;
    }

    public List<Maintenance> getMaintenanceList() {
        return maintenanceList;
    }

}
