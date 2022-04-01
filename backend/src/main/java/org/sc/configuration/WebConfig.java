package org.sc.configuration;

import org.apache.logging.log4j.Logger;
import org.sc.controller.MediaController;
import org.sc.controller.TrailController;
import org.sc.controller.admin.AdminTrailImporterController;
import org.sc.manager.MediaManager;
import org.sc.manager.TrailFileManager;
import org.sc.util.FileManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import java.io.File;

import static org.apache.logging.log4j.LogManager.getLogger;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = getLogger(WebConfig.class);

    private final FileManagementUtil fileManagementUtil;
    private final AppProperties appProperties;

    @Autowired
    public WebConfig(final FileManagementUtil fileManagementUtil,
                     final AppProperties appProperties) {
        this.fileManagementUtil = fileManagementUtil;
        this.appProperties = appProperties;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        final String mediaEndpoint = MediaController.PREFIX + "/" + MediaManager.MEDIA_MID + "/**";
        final String mediaStoragePath = fileManagementUtil.getMediaStoragePath();
        registry.addResourceHandler(mediaEndpoint)
                .addResourceLocations("file:" + mediaStoragePath)
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        LOGGER.info(String.format("Media endpoint: %s -> %s", mediaEndpoint, mediaStoragePath));

        final String gpxEndpoint = TrailController.PREFIX + "/" + TrailFileManager.GPX_TRAIL_MID + "/**";
        final String trailGpxStoragePath = fileManagementUtil.getTrailGpxStoragePath();

        registry.addResourceHandler(gpxEndpoint)
                .addResourceLocations("file:" + trailGpxStoragePath)
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        LOGGER.info(String.format("GPX endpoint: %s -> %s", gpxEndpoint, trailGpxStoragePath));

        final String kmlEndpoint = TrailController.PREFIX + "/" + TrailFileManager.KML_TRAIL_MID + "/**";
        final String trailKmlStoragePath = fileManagementUtil.getTrailKmlStoragePath();

        registry.addResourceHandler(kmlEndpoint)
                .addResourceLocations("file:" + trailKmlStoragePath)
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        LOGGER.info(String.format("KML endpoint: %s -> %s", kmlEndpoint, trailKmlStoragePath));

        final String pdfEndpoint = TrailController.PREFIX + "/" + TrailFileManager.PDF_TRAIL_MID + "/**";
        final String trailPdfStoragePath = fileManagementUtil.getTrailPdfStoragePath();

        registry.addResourceHandler(pdfEndpoint)
                .addResourceLocations("file:" + trailPdfStoragePath)
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        LOGGER.info(String.format("PDF endpoint: %s -> %s", pdfEndpoint, trailPdfStoragePath));
    }
}
