package org.sc.controller;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.sc.data.AccessibilityNotification;
import org.sc.data.AccessibilityNotificationDAO;
import org.sc.data.Maintenance;
import org.sc.data.helper.GsonBeanHelper;
import org.sc.data.helper.JsonHelper;
import org.sc.importer.AccessibilityCreationValidator;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.sc.configuration.ConfigurationProperties.API_PREFIX;

public class AccessibilityNotificationController implements PublicController {

    private final static Logger LOGGER = Logger.getLogger(AccessibilityNotificationController.class.getName());
    private final static String PREFIX = API_PREFIX + "/accessibility";

    private final GsonBeanHelper gsonBeanHelper;
    private final AccessibilityCreationValidator accessibilityValidator;
    private final AccessibilityNotificationDAO accessibilityDAO;


    @Inject
    public AccessibilityNotificationController(final GsonBeanHelper gsonBeanHelper,
                                               final AccessibilityNotificationDAO accessibilityDao,
                                               final AccessibilityCreationValidator accessibilityValidator) {
        this.accessibilityDAO = accessibilityDao;
        this.gsonBeanHelper = gsonBeanHelper;
        this.accessibilityValidator = accessibilityValidator;
    }

    private RESTResponse getSolved(Request request, Response response) {
        return new AccessibilityResponse(accessibilityDAO.getSolved());
    }

    private RESTResponse getNotSolved(Request request, Response response) {
        return new AccessibilityResponse(accessibilityDAO.getNotSolved());
    }

    private RESTResponse deleteAccessibilityNotification(Request request, Response response) {
        final String requestId = request.params(":id");
        boolean isDeleted = accessibilityDAO.delete(new ObjectId(requestId));
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No accessibility notification was found with id '%s'", requestId))));
        }
    }

    private RESTResponse createMaintenance(Request request, Response response) {
        AccessibilityNotification maintenance = convertRequestToAccessibilityNotification(request);
        final Set<String> errors = accessibilityValidator.validate(request);
        if(errors.isEmpty()) {
            accessibilityDAO.upsert(maintenance);
            return new RESTResponse();
        }
        return new RESTResponse(errors);
    }

    public void init() {
        Spark.get(format("%s/solved", PREFIX), this::getSolved, JsonHelper.json());
        Spark.get(format("%s/unsolved", PREFIX), this::getNotSolved, JsonHelper.json());
        Spark.delete(format("%s/delete/:id", PREFIX), this::deleteAccessibilityNotification, JsonHelper.json());
        Spark.put(format("%s/save", PREFIX), this::createMaintenance, JsonHelper.json());
    }

    private AccessibilityNotification convertRequestToAccessibilityNotification(final Request request) {
        final String requestBody = request.body();
        return Objects.requireNonNull(gsonBeanHelper.getGsonBuilder())
                .fromJson(requestBody, AccessibilityNotification.class);
    }
}
