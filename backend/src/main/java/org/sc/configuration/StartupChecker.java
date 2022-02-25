package org.sc.configuration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.apache.logging.log4j.Logger;
import org.sc.configuration.tenant.InstanceRegister;
import org.sc.data.model.Place;
import org.sc.data.model.Trail;
import org.sc.data.repository.TrailDatasetVersionDao;
import org.sc.util.FileManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.File;
import java.util.Arrays;

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
    @Autowired
    InstanceRegister instanceRegister;

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

        instanceRegister.register(
                appProperties.getInstanceId(),
                appProperties.getInstanceRealm(),
                appProperties.getInstanceHostname(),
                appProperties.getPort());

        try {
            trailDatasetVersionDao.getLast();
            configureIndexes();

        } catch (Exception mongoSocketOpenException) {
            LOGGER.error("Could not establish a correct configuration. Is the database available and running?");
        }
    }


    private void configureIndexes() {
        LOGGER.info("Ensuring DB indexes existence");

        final MongoDatabase db = dataSource.getDB();
        final String pointGeoIndex = db.getCollection(Place.COLLECTION_NAME)
                .createIndex(Indexes.geo2dsphere(Place.POINTS));
        final String trailGeoIndex = db.getCollection(Trail.COLLECTION_NAME)
                .createIndex(Indexes.geo2dsphere(Trail.GEO_LINE));

        Arrays.asList(
                Arrays.asList(pointGeoIndex, Place.POINTS),
                Arrays.asList(trailGeoIndex, Trail.GEO_LINE))
                .forEach(
                        (indexArr) -> LOGGER.info("Ensured pointGeoIndex name " + indexArr.get(0) +
                                " for collection: `" + indexArr.get(1) + "`")
                );
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
