package org.sc.frontend.configuration;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.sc.common.config.ConfigurationProperties;
import org.sc.frontend.controller.AppController;
import spark.Spark;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static spark.Spark.port;

public class ConfigurationManager {

    private final Logger LOG = getLogger(ConfigurationManager.class.getName());

    private final AppProperties appProperties;
    private final AppController appController;

    @Inject
    public ConfigurationManager(final AppController appController,
                                final AppProperties appProperties) {
        this.appController = appController;
        this.appProperties = appProperties;
        webServerSetup();
    }

    private void webServerSetup() {
        Spark.ipAddress(ConfigurationProperties.LOCAL_IP_ADDRESS);
        Spark.staticFiles.location("/public");
        port(appProperties.getWebPort());
    }

    public void init() {
        startControllers();
        LOG.info(format("Configuration completed. Listening on port %s", port()));
    }

    private void startControllers() {
        appController.init();
    }



}
