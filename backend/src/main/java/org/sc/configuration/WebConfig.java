package org.sc.configuration;

import org.sc.controller.MediaController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    @Autowired
    public WebConfig(final AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler(MediaController.PREFIX + "/file/**")
                .addResourceLocations("file:" + appProperties.getTrailStorage() + File.separator)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());
    }
}
