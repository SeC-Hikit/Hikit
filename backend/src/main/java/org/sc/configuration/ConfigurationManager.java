package org.sc.configuration;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.sc.controller.ImporterController;
import spark.Spark;

import javax.inject.Named;
import java.io.File;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.sc.configuration.ConfigurationProperties.LOCAL_IP_ADDRESS;
import static spark.Spark.port;

public class ConfigurationManager {

    public static final String TMP_FOLDER = "tmp";

    private final Logger LOG = getLogger(ConfigurationManager.class.getName());

    private final DataSource dataSource;

    private static final String PORT_PROPERTY = "web-port";
    public static final File UPLOAD_DIR = new File(TMP_FOLDER);

    /**
     * Controllers
     */
    private final ImporterController trailController;


    @Inject
    public ConfigurationManager(final @Named(PORT_PROPERTY) String port,
                                final ImporterController trailController,
                                final DataSource dataSource) {
        this.trailController = trailController;
        this.dataSource = dataSource;
        webServerSetup(port);
        UPLOAD_DIR.mkdir();

    }

    private void webServerSetup(final String port) {
        Spark.ipAddress(LOCAL_IP_ADDRESS);
        Spark.staticFiles.location("/public"); // Static files
        Spark.staticFiles.externalLocation(TMP_FOLDER); // Static files
        port(Integer.parseInt(port));
    }


    public void init() {
        startControllers();
        testConnectionWithDB();
        LOG.info(format("Configuration completed. Listening on port %s", port()));
    }

    private void testConnectionWithDB() {
        dataSource.getClient().listDatabases();
    }

    private void startControllers() {
        trailController.init();
    }

}
