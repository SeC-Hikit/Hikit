package org.sc.frontend.configuration;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.sc.common.config.ConfigurationProperties;
import org.sc.frontend.controller.MaintenanceController;
import org.sc.frontend.controller.NotificationController;
import org.sc.frontend.controller.TrailController;
import spark.Spark;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static spark.Spark.port;

public class ConfigurationManager {

    private final Logger LOG = getLogger(ConfigurationManager.class.getName());

    private final MaintenanceController maintenanceController;
    private final NotificationController notificationController;
    private final AppProperties appProperties;
    private final TrailController trailController;

    @Inject
    public ConfigurationManager(final TrailController trailController,
                                final MaintenanceController maintenanceController,
                                final NotificationController notificationController,
                                final AppProperties appProperties) {
        this.trailController = trailController;
        this.maintenanceController = maintenanceController;
        this.notificationController = notificationController;
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
        trailController.init();
        notificationController.init();
        maintenanceController.init();
    }



}
