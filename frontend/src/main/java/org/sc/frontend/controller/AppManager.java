package org.sc.frontend.controller;

import com.google.inject.Inject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.sc.common.config.ConfigurationProperties;
import org.sc.common.rest.controller.CoordinatesWithAltitude;
import org.sc.common.rest.controller.FileDownloadRestResponse;
import org.sc.common.rest.controller.Trail;
import org.sc.common.rest.controller.TrailRestResponse;
import org.sc.common.rest.controller.helper.GsonBeanHelper;
import org.sc.frontend.configuration.AppProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AppManager {

    private final AppProperties appProperties;
    private final GsonBeanHelper gsonBeanHelper;

    @Inject
    public AppManager(AppProperties appProperties, GsonBeanHelper gsonBeanHelper) {
        this.appProperties = appProperties;
        this.gsonBeanHelper = gsonBeanHelper;
    }

    public String getTrailsPreview() throws IOException {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail/preview")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public List<CoordinatesWithAltitude> getTrailPreviewPoints(String id) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail/" + id)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (isNotBlank(responseBody)) {
            TrailRestResponse trailRestResponse = gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, TrailRestResponse.class);
            return trailRestResponse.getTrails().stream().findFirst().get().getCoordinates();
        }
        return null;
    }

    public TrailRestResponse getTrailsLowCoordinates() throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail")
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (isNotBlank(responseBody)) {
            final TrailRestResponse trailRestResponse = gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, TrailRestResponse.class);
            trailRestResponse.getTrails().stream().map(trail ->
                    Trail.TrailBuilder.aTrail()
                            .withCode(trail.getCode())
                            .withDate(trail.getDate())
                            .withClassification(trail.getClassification())
                            .withCountry(trail.getCountry())
                            .withDescription(trail.getDescription())
                            .withTrailMetadata(trail.getStatsMetadata())
                            .withStartPos(trail.getStartPos())
                            .withFinalPos(trail.getFinalPos())
                            .withMaintainingSection(trail.getMaintainingSection())
                            .withName(trail.getName())
                            .withCoordinates(getLowestCoordinates(trail.getCoordinates()))
                            .build());
        }
        return null;
    }

    public TrailRestResponse getTrail(String code) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail/" + code)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (isNotBlank(responseBody)) {
            return gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, TrailRestResponse.class);
        }
        return null;
    }

    public byte[] getTrailDownloadableLink(String code) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail/download/" + code)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (isNotBlank(responseBody)) {
            FileDownloadRestResponse fileDownloadRestResponse = gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, FileDownloadRestResponse.class);
            byte[] bytes = Files.readAllBytes(Paths.get(fileDownloadRestResponse.getPath()));
            return bytes;
        }
        return null;
    }

    private List<CoordinatesWithAltitude> getLowestCoordinates(List<CoordinatesWithAltitude> coordinatesWithAltitudes) {
        return IntStream.range(0, coordinatesWithAltitudes.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(coordinatesWithAltitudes::get)
                .collect(Collectors.toList());
    }

    private String getBasicUrl() {
        return appProperties.getBackendAddress() + "/" + ConfigurationProperties.API_PREFIX;
    }


}
