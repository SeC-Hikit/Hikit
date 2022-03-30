package org.sc.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

import static org.sc.configuration.AppProperties.APP_NAME;
import static org.sc.configuration.AppProperties.DISPLAYED_VERSION;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = APP_NAME,
        version= DISPLAYED_VERSION,
        description = "S&C is an open API that manages high level geo and meta data related to trails. " +
                "It supports accessibility-notifications, maintenance planning, generic/specific POIs " +
                "and other features all linked with trails."))
public class OpenAPI { }
