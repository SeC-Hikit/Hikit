package org.sc.frontend.controller;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.sc.common.config.ConfigurationProperties;
import org.sc.common.rest.controller.AccessibilityResponse;
import org.sc.common.rest.controller.helper.GsonBeanHelper;
import org.sc.frontend.configuration.AppProperties;

import javax.inject.Inject;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class NotificationManager {

    private final AppProperties appProperties;
    private final GsonBeanHelper gsonBeanHelper;

    @Inject
    public NotificationManager(final AppProperties appProperties, final GsonBeanHelper gsonBeanHelper) {
        this.appProperties = appProperties;
        this.gsonBeanHelper = gsonBeanHelper;
    }

    public AccessibilityResponse getNotificationsForTrail(String code) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getBasicUrl() + "/accessibility/code/" + code)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (isNotBlank(responseBody)) {
            return gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, AccessibilityResponse.class);
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
        if (isNotBlank(responseBody)) {
            return gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, AccessibilityResponse.class);
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
        if (isNotBlank(responseBody)) {
            return gsonBeanHelper.getGsonBuilder()
                    .fromJson(responseBody, AccessibilityResponse.class);
        }
        return null;
    }

    private String getBasicUrl() {
        return appProperties.getBackendAddress() + "/" + ConfigurationProperties.API_PREFIX;
    }
}
