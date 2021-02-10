package org.sc.configuration;

import org.apache.logging.log4j.Logger;
import org.sc.data.repository.TrailDatasetVersionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.File;

import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class StartupChecker {

    private static final Logger LOGGER = getLogger(StartupChecker.class);

    @Autowired
    TrailDatasetVersionDao trailDatasetVersionDao;
    @Autowired
    AppProperties appProperties;

    @PostConstruct
    public void init() {
        final File uploadDir = new File(appProperties.getTempStorage());
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdir()) {
                throw new IllegalStateException("Could not create temp folder");
            }
        }

        try {
            trailDatasetVersionDao.getLast();
        } catch (Exception mongoSocketOpenException) {
            LOGGER.error("Could not establish a correct configuration. Is the database available and running?");
        }
    }
}
