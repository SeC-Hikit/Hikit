package org.sc.frontend.controller;

import com.google.inject.Inject;
import org.sc.common.rest.controller.CoordinatesWithAltitude;
import org.sc.common.rest.controller.JsonHelper;
import org.sc.common.rest.controller.PublicController;
import org.sc.common.rest.controller.TrailRestResponse;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static spark.Spark.get;

public class TrailController implements PublicController {

    public static final String PREFIX = "app";
    private final TrailManager trailManager;

    @Inject
    public TrailController(final TrailManager trailManager) {
        this.trailManager = trailManager;
    }

    private String getTrailsPreview(Request request, Response response) throws IOException {
        return trailManager.getTrailsPreview();
    }

    private List<CoordinatesWithAltitude> getTrailCoordinatesByCode(Request request, Response response) throws IOException {
        String code = request.params(":code");
        return trailManager.getTrailPreviewPoints(code);
    }

    private TrailRestResponse getTrailsCoordinateLow(Request request, Response response) throws IOException {
        return trailManager.getTrailsLowCoordinates();
    }

    private TrailRestResponse getTrailByCode(Request request, Response response) throws IOException {
        String code = request.params(":code");
        return trailManager.getTrail(code);
    }

    private HttpServletResponse getDownloadableFile(Request request, Response response) throws IOException {
        String code = request.params(":code");
        byte[] trailGpxFile = trailManager.getTrailDownloadableLink(code);
        HttpServletResponse raw = response.raw();
        raw.getOutputStream().write(trailGpxFile);
        raw.getOutputStream().flush();
        raw.getOutputStream().close();
        return response.raw();
    }

    public void init() {
        get(format("%s/preview/all", PREFIX), this::getTrailsPreview, JsonHelper.json());
        get(format("%s/preview/trail/:code", PREFIX), this::getTrailCoordinatesByCode, JsonHelper.json());
        get(format("%s/trail", PREFIX), this::getTrailsCoordinateLow, JsonHelper.json());
        get(format("%s/trail/:code", PREFIX), this::getTrailByCode, JsonHelper.json());
        get(format("%s/download/:code", PREFIX), this::getDownloadableFile);
    }

}
