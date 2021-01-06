package org.sc.common.rest;

import java.util.Collections;
import java.util.List;

public class AccessibilityUnresolvedResponse extends RESTResponse {
    List<AccessibilityNotificationUnresolved> accessibilityNotifications;

    public AccessibilityUnresolvedResponse(){}

    public AccessibilityUnresolvedResponse(final List<AccessibilityNotificationUnresolved> accessibilityNotifications) {
        super(Status.OK, Collections.emptySet());
        this.accessibilityNotifications = accessibilityNotifications;
    }

    public List<AccessibilityNotificationUnresolved> getAccessibilityNotifications() {
        return accessibilityNotifications;
    }
}
