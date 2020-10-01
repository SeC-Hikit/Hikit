package org.sc.controller;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.sc.data.Maintenance;
import org.sc.data.MaintenanceDAO;
import org.sc.data.helper.GsonBeanHelper;
import org.sc.data.helper.JsonHelper;
import org.sc.importer.MaintenanceCreationValidator;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.*;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.sc.configuration.ConfigurationProperties.API_PREFIX;

public class MaintenanceController implements PublicController {

    private final static Logger LOGGER = Logger.getLogger(MaintenanceController.class.getName());
    private final static String PREFIX = API_PREFIX + "/maintenance";

    private final GsonBeanHelper gsonBeanHelper;
    private final MaintenanceCreationValidator maintenanceValidator;
    private final MaintenanceDAO maintenanceDao;


    @Inject
    public MaintenanceController(final GsonBeanHelper gsonBeanHelper,
                                 final MaintenanceDAO maintenanceDao,
                                 final MaintenanceCreationValidator maintenanceValidator) {
        this.maintenanceDao = maintenanceDao;
        this.gsonBeanHelper = gsonBeanHelper;
        this.maintenanceValidator = maintenanceValidator;
    }

    private RESTResponse getFutureMaintenance(Request request, Response response) {
        return new MaintenanceResponse(maintenanceDao.getFuture());
    }

    private RESTResponse getPastMaintenance(Request request, Response response) {
        return new MaintenanceResponse(maintenanceDao.getPast());
    }

    private RESTResponse deleteMaintenance(Request request, Response response) {
        final String requestId = request.params(":id");
        boolean isDeleted = maintenanceDao.delete(new ObjectId(requestId));
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            LOGGER.warning(format("Could not delete maintenance with id %s", requestId));
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No maintenance was found with id '%s'", requestId))));
        }
    }

    private RESTResponse createMaintenance(Request request, Response response) {
        Maintenance maintenance = convertRequestToMaintenance(request);
        Set<String> errors = maintenanceValidator.validate(request);
        if(errors.isEmpty()) {
            maintenanceDao.upsert(maintenance);
            return new RESTResponse();
        }
        return new RESTResponse(errors);
    }

    public void init() {
        Spark.get(format("%s/future", PREFIX), this::getFutureMaintenance, JsonHelper.json());
        Spark.get(format("%s/past", PREFIX), this::getPastMaintenance, JsonHelper.json());
        Spark.delete(format("%s/delete/:id", PREFIX), this::deleteMaintenance, JsonHelper.json());
        Spark.put(format("%s/save", PREFIX), this::createMaintenance, JsonHelper.json());
    }

    private Maintenance convertRequestToMaintenance(final Request request) {
        final String requestBody = request.body();
        return Objects.requireNonNull(gsonBeanHelper.getGsonBuilder())
                .fromJson(requestBody, Maintenance.class);
    }
}
