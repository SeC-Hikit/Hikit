package org.sc.controller;

import org.sc.data.AccessibilityNotification;
import org.sc.data.Maintenance;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AccessibilityResponse extends RESTResponse {
    final List<AccessibilityNotification> maintenanceList;

    public AccessibilityResponse(List<AccessibilityNotification> maintenanceList) {
        super(Status.OK, Collections.emptySet());
        this.maintenanceList = maintenanceList;
    }

    public List<AccessibilityNotification> getMaintenanceList() {
        return maintenanceList;
    }
}
