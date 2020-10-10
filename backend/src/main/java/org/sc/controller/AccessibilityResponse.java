package org.sc.controller;

import org.sc.data.AccessibilityNotification;

import java.util.Collections;
import java.util.List;

public class AccessibilityResponse extends RESTResponse {
    final List<AccessibilityNotification> accessibilityNotifications;

    public AccessibilityResponse(List<AccessibilityNotification> accessibilityNotifications) {
        super(Status.OK, Collections.emptySet());
        this.accessibilityNotifications = accessibilityNotifications;
    }

    public List<AccessibilityNotification> getAccessibilityNotifications() {
        return accessibilityNotifications;
    }
}
