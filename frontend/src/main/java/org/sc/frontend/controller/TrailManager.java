package org.sc.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.sc.common.config.ConfigurationProperties;
import org.sc.common.rest.controller.*;
import org.sc.frontend.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TrailManager {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapperWrapper;

    @Autowired
    public TrailManager(AppProperties appProperties, ObjectMapper objectMapperWrapper) {
        this.appProperties = appProperties;
        this.objectMapperWrapper = objectMapperWrapper;
    }

    public TrailPreviewRestResponse getTrailsPreview() throws IOException {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/preview")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, TrailPreviewRestResponse.class);

        }
        return null;
    }

    public List<CoordinatesWithAltitude> getTrailPreviewPoints(String id) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail/" + id)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            TrailRestResponse trailRestResponse = objectMapperWrapper
                    .readValue(responseBody, TrailRestResponse.class);
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
        if (StringUtils.hasText(responseBody)) {
            final TrailRestResponse trailRestResponse = objectMapperWrapper
                    .readValue(responseBody, TrailRestResponse.class);
            final List<Trail> lowTrails = trailRestResponse.getTrails().stream().map(trail ->
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
                            .build()).collect(Collectors.toList());
            return new TrailRestResponse(lowTrails);
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
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, TrailRestResponse.class);
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
        if (StringUtils.hasText(responseBody)) {
            FileDownloadRestResponse fileDownloadRestResponse = objectMapperWrapper
                    .readValue(responseBody, FileDownloadRestResponse.class);
            return Files.readAllBytes(Paths.get(fileDownloadRestResponse.getPath()));
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
