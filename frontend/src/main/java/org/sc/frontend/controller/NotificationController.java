package org.sc.frontend.controller;

import com.google.inject.Inject;
import org.sc.common.rest.controller.*;
import spark.Request;
import spark.Response;

import java.io.IOException;

import static java.lang.String.format;
import static org.sc.frontend.controller.TrailController.PREFIX;
import static spark.Spark.get;

public class NotificationController implements PublicController {


    private final NotificationManager notificationManager;

    @Inject
    public NotificationController(final NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    private AccessibilityResponse getNotificationsUnsolved(Request request, Response response) throws IOException {
        return notificationManager.getNotificationUnsolved();
    }

    private AccessibilityResponse getPaginatedNotificationSolved(Request request, Response response) throws IOException {
        final String from = request.params(":from");
        final String to = request.params(":to");
        return notificationManager.getNotificationSolved(Integer.parseInt(from), Integer.parseInt(to));
    }

    private AccessibilityResponse getNotificationForTrail(Request request, Response response) throws IOException {
        final String code = request.params(":code");
        return notificationManager.getNotificationsForTrail(code);
    }

    public void init() {
        get(format("%s/notifications/unsolved", PREFIX), this::getNotificationsUnsolved, JsonHelper.json());
        get(format("%s/notifications/solved/:from/:to", PREFIX), this::getPaginatedNotificationSolved, JsonHelper.json());
        get(format("%s/notifications/:code", PREFIX), this::getNotificationForTrail, JsonHelper.json());
    }

}
