package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.BatchStatus;
import org.sc.common.rest.GenerateRequestDto;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.response.ResourceGeneratorResponse;
import org.sc.configuration.AppProperties;
import org.sc.data.model.Trail;
import org.sc.data.repository.TrailDAO;
import org.sc.manager.TrailFileManager;
import org.sc.manager.TrailImporterService;
import org.sc.manager.TrailManager;
import org.sc.processor.TrailSimplifierLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static org.sc.controller.admin.Constants.PREFIX_RESOURCE;

@RestController
@RequestMapping(PREFIX_RESOURCE)
public class AdminResourceController {

    final static Logger LOGGER = LoggerFactory.getLogger(AdminResourceController.class);

    public static final String COULD_NOT_ERROR = "Could not re-generate resources for Trail with Id '%s'";
    public static final String REGENERATE_FOR_TRAIL_ID_MESSAGE = "Going to re-generate file for trail with id '%s'";
    public static final String DONE_REGENERATING_FOR_TRAIL_ID = "Done regenerating files for trail with id '%s'";


    private static final AtomicBoolean isMigrationRunning = new AtomicBoolean();

    private final TrailImporterService trailImporterService;
    private final TrailManager trailManager;
    private final AppProperties appProperties;

    @Autowired
    public AdminResourceController(final TrailManager trailManager,
                                   final TrailImporterService trailImporterService,
                                   final AppProperties appProperties) {
        this.trailManager = trailManager;
        this.trailImporterService = trailImporterService;
        this.appProperties = appProperties;
    }

    @Operation(summary = "Generate all resources belonging to this instance")
    @GetMapping(path = "/regenerate",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResourceGeneratorResponse generateResource() {
        if (isMigrationRunning.get()) {
            return new ResourceGeneratorResponse(BatchStatus.BUSY);
        }
        if (isMigrationRunning.compareAndSet(false, true)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                final List<TrailDto> trails = trailManager.get(0,
                        Integer.MAX_VALUE, TrailSimplifierLevel.FULL,
                        appProperties.getInstanceRealm());
                doExport(trails);
                isMigrationRunning.set(false);
            });
        }

        return new ResourceGeneratorResponse(BatchStatus.OK);
    }


    @Operation(summary = "Generate the target resources belonging to this instance")
    @PostMapping(path = "/regenerate",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResourceGeneratorResponse generateResource(GenerateRequestDto generateRequestDto) {
        if (isMigrationRunning.get()) {
            return new ResourceGeneratorResponse(BatchStatus.BUSY);
        }
        if (isMigrationRunning.compareAndSet(false, true)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                generateRequestDto.getIds().forEach(id -> {
                    if (id == null) return;
                    final List<TrailDto> singletonList = trailManager.getById(id, TrailSimplifierLevel.LOW);
                    if (singletonList.isEmpty()) {
                        LOGGER.warn(String.format(COULD_NOT_ERROR +
                                ", as it does not exist", id));
                        return;
                    }
                    if (!singletonList.stream().findFirst().get()
                            .getFileDetails().getRealm().equals(appProperties.getInstanceRealm())) {
                        LOGGER.warn(String.format(COULD_NOT_ERROR +
                                ", as it does not belong to the same realm", id));
                        return;
                    }
                    doExport(singletonList);
                });
                isMigrationRunning.set(false);
            });
        }

        return new ResourceGeneratorResponse(BatchStatus.OK);
    }

    private void doExport(List<TrailDto> trails) {
        trails.forEach(t -> {
            final String trailId = t.getId();
            LOGGER.info(format(REGENERATE_FOR_TRAIL_ID_MESSAGE, trailId));
            trailImporterService.updateResourcesForTrail(t);
            LOGGER.info(format(DONE_REGENERATING_FOR_TRAIL_ID, trailId));
        });
    }
}
