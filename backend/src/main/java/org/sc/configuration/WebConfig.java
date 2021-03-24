package org.sc.configuration;

import org.sc.controller.MediaController;
import org.sc.controller.TrailController;
import org.sc.manager.MediaManager;
import org.sc.manager.TrailFileManager;
import org.sc.util.FileManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FileManagementUtil fileManagementUtil;
    private final AppProperties appProperties;

    @Autowired
    public WebConfig(final FileManagementUtil fileManagementUtil, AppProperties appProperties) {
        this.fileManagementUtil = fileManagementUtil;
        this.appProperties = appProperties;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {

        registry.addResourceHandler(MediaController.PREFIX + "/" + MediaManager.MEDIA_MID + "/**")
                .addResourceLocations("file:" + fileManagementUtil.getMediaStoragePath())
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        registry.addResourceHandler(TrailController.PREFIX + "/" + TrailFileManager.GPX_TRAIL_MID + "/**")
                .addResourceLocations("file:" + fileManagementUtil.getTrailGpxStoragePath())
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        registry.addResourceHandler(TrailController.PREFIX + "/" + TrailFileManager.KML_TRAIL_MID + "/**")
                .addResourceLocations("file:" + fileManagementUtil.getTrailKmlStoragePath())
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

        registry.addResourceHandler(TrailController.PREFIX + "/" + TrailFileManager.PDF_TRAIL_MID + "/**")
                .addResourceLocations("file:" + fileManagementUtil.getTrailPdfStoragePath())
                .setCachePeriod(appProperties.getResourcesCachePeriod())
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());

    }
}
