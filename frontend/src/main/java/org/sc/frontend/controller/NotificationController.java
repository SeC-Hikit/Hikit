package org.sc.frontend.controller;

import org.sc.common.rest.controller.AccessibilityNotificationUnresolved;
import org.sc.common.rest.controller.AccessibilityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationManager notificationManager;

    @Autowired
    public NotificationController(final NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @GetMapping("/unsolved")
    public AccessibilityResponse getNotificationsUnsolved() throws IOException {
        return notificationManager.getNotificationUnsolved();
    }

    @GetMapping("/solved/{from}/{to}")
    public AccessibilityResponse getPaginatedNotificationSolved(@PathVariable int from,
                                                                @PathVariable int to) throws IOException {
        return notificationManager.getNotificationSolved(from, to);
    }

    @GetMapping("/{code}")
    public List<AccessibilityNotificationUnresolved> getNotificationUnsolvedForTrail(@PathVariable String code) throws IOException {
        return notificationManager.getNotificationsUnsolvedForTrail(code);
    }

}
