package org.sc.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import org.jetbrains.annotations.NotNull;
import org.sc.common.config.ConfigurationProperties;
import org.sc.common.rest.controller.*;
import org.sc.frontend.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TrailManager {

    private static final String TMP_FOLDER = "tmp";
    private static final File UPLOAD_DIR = new File(TMP_FOLDER);

    private final AppProperties appProperties;
    private final ObjectMapper objectMapperWrapper;

    @Autowired
    public TrailManager(AppProperties appProperties, ObjectMapper objectMapperWrapper) {
        this.appProperties = appProperties;
        this.objectMapperWrapper = objectMapperWrapper;
    }

    public TrailPreviewResponse getTrailsPreview() throws IOException {
        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/preview")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, TrailPreviewResponse.class);

        }
        return null;
    }

    public List<TrailCoordinates> getTrailPreviewPoints(String id) throws IOException {
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
            final List<Trail> lowTrails = trailRestResponse.getTrails().stream().map(this::getLowerResolutionTrail).collect(Collectors.toList());
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
            TrailRestResponse trailRestResponse = objectMapperWrapper
                    .readValue(responseBody, TrailRestResponse.class);
            final List<Trail> lowTrails = trailRestResponse.getTrails().stream().map(this::getLowerResolutionTrail).collect(Collectors.toList());
            return new TrailRestResponse(lowTrails);
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
            FileDownloadResponse fileDownloadRestResponse = objectMapperWrapper
                    .readValue(responseBody, FileDownloadResponse.class);
            return Files.readAllBytes(Paths.get(fileDownloadRestResponse.getPath()));
        }
        return null;
    }

    public TrailPreparationModel getTrailPreparationFromGpx(MultipartFile gpxFile) throws IOException {
        final Path tempFile = Files.createTempFile(UPLOAD_DIR.toPath(), "", "");
        try (final InputStream input = gpxFile.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        final OkHttpClient client = new OkHttpClient();
        final RequestBody multipart = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("gpxFile",
                        gpxFile.getOriginalFilename(),
                        RequestBody.create(MediaType.parse("gpx=application/gpx+xml"),
                                Files.readAllBytes(tempFile)))
                .build();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/trail/read")
                .post(multipart)
                .build();
        final Response response = client.newCall(request).execute();
        final String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, TrailPreparationModel.class);
        }
        return null;
    }

    @NotNull
    private Trail getLowerResolutionTrail(Trail trail) {
        return Trail.TrailBuilder.aTrail()
                .withCode(trail.getCode())
                .withDate(trail.getLastUpdate())
                .withClassification(trail.getClassification())
                .withCountry(trail.getCountry())
                .withDescription(trail.getDescription())
                .withTrailMetadata(trail.getStatsMetadata())
                .withStartPos(trail.getStartPos())
                .withFinalPos(trail.getFinalPos())
                .withMaintainingSection(trail.getMaintainingSection())
                .withName(trail.getName())
                .withCoordinates(getHalfCoordinates(trail.getCoordinates()))
                .build();
    }

    private List<TrailCoordinates> getHalfCoordinates(final List<TrailCoordinates> coordinatesWithAltitudes) {
        return IntStream.range(0, coordinatesWithAltitudes.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(coordinatesWithAltitudes::get)
                .collect(Collectors.toList());
    }

    private String getBasicUrl() {
        return appProperties.getBackendAddress() + "/" + ConfigurationProperties.API_PREFIX;
    }

}
