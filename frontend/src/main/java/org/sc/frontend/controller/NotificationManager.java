package org.sc.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.sc.common.config.ConfigurationProperties;
import org.sc.common.rest.controller.AccessibilityResponse;
import org.sc.common.rest.controller.helper.ObjectMapperWrapper;
import org.sc.frontend.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class NotificationManager {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapperWrapper;

    @Autowired
    public NotificationManager(final AppProperties appProperties, final ObjectMapper objectMapperWrapper) {
        this.appProperties = appProperties;
        this.objectMapperWrapper = objectMapperWrapper;
    }

    public AccessibilityResponse getNotificationsForTrail(String code) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/accessibility/code/" + code)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, AccessibilityResponse.class);
        }
        return null;
    }

    public AccessibilityResponse getNotificationUnsolved() throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/accessibility/unsolved")
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, AccessibilityResponse.class);
        }
        return null;
    }

    public AccessibilityResponse getNotificationSolved(int from, int to) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/accessibility/solved/" + from + "/" + to)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (StringUtils.hasText(responseBody)) {
            return objectMapperWrapper
                    .readValue(responseBody, AccessibilityResponse.class);
        }
        return null;
    }

    private String getBasicUrl() {
        return appProperties.getBackendAddress() + "/" + ConfigurationProperties.API_PREFIX;
    }
}
