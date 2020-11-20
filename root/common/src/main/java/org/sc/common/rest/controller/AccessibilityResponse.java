package org.sc.common.rest.controller;

import java.util.Collections;
import java.util.List;

public class AccessibilityResponse extends RESTResponse {
    List<AccessibilityNotification> accessibilityNotifications;

    public AccessibilityResponse(){}

    public AccessibilityResponse(List<AccessibilityNotification> accessibilityNotifications) {
        super(Status.OK, Collections.emptySet());
        this.accessibilityNotifications = accessibilityNotifications;
    }

    public List<AccessibilityNotification> getAccessibilityNotifications() {
        return accessibilityNotifications;
    }
}
