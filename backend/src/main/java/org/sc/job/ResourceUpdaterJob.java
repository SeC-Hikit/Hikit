package org.sc.job;

import org.apache.logging.log4j.Logger;
import org.sc.configuration.AppProperties;
import org.sc.service.ResourceService;
import org.sc.util.FileManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.apache.logging.log4j.LogManager.getLogger;

@Component
public class ResourceUpdaterJob {
    private static final Logger LOGGER = getLogger(ResourceUpdaterJob.class);

    private static final String STARTING_COMPRESSION_JOB = "Going to run reosurce updater job (batch size: %s)...";
    private static final String DONE_COMPRESSION_JOB = "Done with resource updater job.";

    private final ResourceService resourceService;
    private final int imageBatchSize;

    @Autowired
    public ResourceUpdaterJob(final ResourceService resourceService,
                              final AppProperties appProperties) {
        this.resourceService = resourceService;
        this.imageBatchSize = appProperties.getJobImageBatchSize();
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void doRegenerateResources() {
        LOGGER.trace(format(STARTING_COMPRESSION_JOB, imageBatchSize));
        resourceService.execute();
        LOGGER.trace(DONE_COMPRESSION_JOB);

    }

}
