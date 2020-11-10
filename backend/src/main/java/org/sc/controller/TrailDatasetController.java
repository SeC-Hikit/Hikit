package org.sc.controller;

import com.google.inject.Inject;
import org.sc.common.rest.controller.JsonHelper;
import org.sc.common.rest.controller.PublicController;
import org.sc.data.TrailDatasetVersion;
import org.sc.data.TrailDatasetVersionDao;
import spark.Request;
import spark.Response;

import static java.lang.String.format;
import static org.sc.common.config.ConfigurationProperties.API_PREFIX;
import static spark.Spark.get;

public class TrailDatasetController implements PublicController {

    private final static String PREFIX = API_PREFIX + "/dataset";
    private TrailDatasetVersionDao trailDatasetVersionDao;

    @Inject
    public TrailDatasetController(TrailDatasetVersionDao trailDatasetVersionDao) {
        this.trailDatasetVersionDao = trailDatasetVersionDao;
    }

    private TrailDatasetVersion getTrailDatasetV(Request request, Response response) {
        return trailDatasetVersionDao.getLast();
    }

    public void init() {
        get(format("%s", PREFIX), this::getTrailDatasetV, JsonHelper.json());
    }

}
