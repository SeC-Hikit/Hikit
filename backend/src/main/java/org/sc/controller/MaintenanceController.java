package org.sc.controller;

import com.google.inject.Inject;
import org.sc.common.rest.controller.*;
import org.sc.common.rest.controller.helper.GsonBeanHelper;
import org.sc.data.MaintenanceDAO;
import org.sc.importer.MaintenanceCreationValidator;
import spark.Request;
import spark.Response;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;
import static org.sc.common.config.ConfigurationProperties.API_PREFIX;
import static org.sc.controller.TrailController.BAD_REQUEST_STATUS_CODE;
import static spark.Spark.*;

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

    private RESTResponse getPastPaged(final Request request, final Response response) {
        final int from = Integer.parseInt(request.params(":from"));
        final int to = Integer.parseInt(request.params(":to"));
        if(from <= to){
            return new MaintenanceResponse(maintenanceDao.getPast(from, to));
        }
        return null;
    }

    private RESTResponse deleteMaintenance(Request request, Response response) {
        final String requestId = request.params(":id");
        boolean isDeleted = maintenanceDao.delete(requestId);
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            LOGGER.warning(format("Could not delete maintenance with id %s", requestId));
            response.status(BAD_REQUEST_STATUS_CODE);
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No maintenance was found with id '%s'", requestId))));
        }
    }

    private RESTResponse createMaintenance(Request request, Response response) {
        Maintenance maintenance = convertRequestToMaintenance(request);
        final Set<String> errors = maintenanceValidator.validate(request);
        if(errors.isEmpty()) {
            maintenanceDao.upsert(maintenance);
            return new RESTResponse();
        }
        response.status(BAD_REQUEST_STATUS_CODE);
        return new RESTResponse(errors);
    }

    public void init() {
        get(format("%s/future", PREFIX), this::getFutureMaintenance, JsonHelper.json());
        get(format("%s/past", PREFIX), this::getPastMaintenance, JsonHelper.json());
        get(format("%s/past/:from/:to", PREFIX), this::getPastPaged, JsonHelper.json());
        delete(format("%s/delete/:id", PREFIX), this::deleteMaintenance, JsonHelper.json());
        put(format("%s/save", PREFIX), this::createMaintenance, JsonHelper.json());
    }

    private Maintenance convertRequestToMaintenance(final Request request) {
        final String requestBody = request.body();
        return Objects.requireNonNull(gsonBeanHelper.getGsonBuilder())
                .fromJson(requestBody, Maintenance.class);
    }
}
