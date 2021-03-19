package org.sc.configuration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.apache.logging.log4j.Logger;
import org.sc.data.model.Place;
import org.sc.data.repository.TrailDatasetVersionDao;
import org.sc.util.FileManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.File;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class StartupChecker {

    private static final Logger LOGGER = getLogger(StartupChecker.class);

    @Autowired
    DataSource dataSource;
    @Autowired
    TrailDatasetVersionDao trailDatasetVersionDao;
    @Autowired
    AppProperties appProperties;
    @Autowired
    FileManagementUtil fileManagementUtil;

    @PostConstruct
    public void init() {
        configureDir(appProperties.getTempStorage(), "Could not create temp folder");
        configureDir(appProperties.getStorage(), "Could not create storage folder");
        configureDir(fileManagementUtil.getMediaStoragePath(), "Could not create media folder");
        configureDir(fileManagementUtil.getTrailStoragePath(), "Could not create trail folder");
        configureDir(fileManagementUtil.getRawTrailStoragePath(), "Could not create raw trail folder");
        configureDir(fileManagementUtil.getTrailGpxStoragePath(), "Could not create trail/gpx folder");
        configureDir(fileManagementUtil.getTrailKmlStoragePath(), "Could not create trail/kml folder");
        configureDir(fileManagementUtil.getTrailPdfStoragePath(), "Could not create trail/pdf folder");

        try {
            trailDatasetVersionDao.getLast();

            configureIndexes();


        } catch (Exception mongoSocketOpenException) {
            LOGGER.error("Could not establish a correct configuration. Is the database available and running?");
        }
    }

    private void configureIndexes() {
        LOGGER.info("Checking DB indexes");
        MongoDatabase db = dataSource.getDB();
        String index = db.getCollection(Place.COLLECTION_NAME).createIndex(Indexes.geo2dsphere(Place.POINTS));
        LOGGER.info("Index name" + index);
    }

    private void configureDir(final String path,
                              final String errorPath) {
        final File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IllegalStateException(errorPath);
            }
        }
    }
}
