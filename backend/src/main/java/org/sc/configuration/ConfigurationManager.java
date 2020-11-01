package org.sc.configuration;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.sc.controller.AccessibilityNotificationController;
import org.sc.controller.MaintenanceController;
import org.sc.controller.TrailController;
import org.sc.controller.TrailDatasetController;
import spark.Spark;

import java.io.File;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.configuration.ConfigurationProperties.LOCAL_IP_ADDRESS;
import static spark.Spark.after;
import static spark.Spark.port;

public class ConfigurationManager {

    private final Logger LOG = getLogger(ConfigurationManager.class.getName());

    public static final String TMP_FOLDER = "tmp";

    private final DataSource dataSource;
    private final AppProperties appProperties;
    public static final File UPLOAD_DIR = new File(TMP_FOLDER);

    /**
     * Controllers
     */
    private final TrailController trailController;
    private final MaintenanceController maintenanceController;
    private final AccessibilityNotificationController accessibilityNotificationController;
    private final TrailDatasetController trailDatasetController;

    @Inject
    public ConfigurationManager(final TrailController trailController,
                                final MaintenanceController maintenanceController,
                                final AccessibilityNotificationController accessibilityNotificationController,
                                final TrailDatasetController trailDatasetController,
                                final DataSource dataSource,
                                final AppProperties appProperties) {
        this.trailController = trailController;
        this.maintenanceController = maintenanceController;
        this.accessibilityNotificationController = accessibilityNotificationController;
        this.trailDatasetController = trailDatasetController;
        this.dataSource = dataSource;
        this.appProperties = appProperties;
        webServerSetup();
        UPLOAD_DIR.mkdir();
    }

    private void webServerSetup() {
        Spark.ipAddress(LOCAL_IP_ADDRESS);
        Spark.staticFiles.location(appProperties.getPathToGpxDirectory());
        Spark.staticFiles.externalLocation(TMP_FOLDER);
        port(appProperties.getWebPort());
    }

    private void dbSetup() {
        testConnectionWithDB();
        // TODO: setup the indexes;
    }


    public void init() {
        dbSetup();
        startControllers();
        LOG.info(format("Configuration completed. Listening on port %s", port()));
    }

    private void testConnectionWithDB() {
        dataSource.getClient().listDatabases();
    }

    private void startControllers() {
        after((request, response) -> response.type("application/json"));
        trailController.init();
        maintenanceController.init();
        accessibilityNotificationController.init();
        trailDatasetController.init();
    }



}
