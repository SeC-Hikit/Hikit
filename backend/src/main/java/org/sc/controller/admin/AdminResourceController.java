package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.BatchStatus;
import org.sc.common.rest.GenerateRequestDto;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.response.ResourceGeneratorResponse;
import org.sc.configuration.AppProperties;
import org.sc.manager.PlaceManager;
import org.sc.manager.TrailFileManager;
import org.sc.manager.TrailManager;
import org.sc.processor.TrailSimplifierLevel;
import org.sc.service.ResourceService;
import org.sc.service.TrailImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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

    private final ResourceService resourceService;
    private final TrailImporterService trailImporterService;
    private final TrailManager trailManager;
    private final PlaceManager placeManager;
    private final AppProperties appProperties;
    private final TrailFileManager trailFileManager;

    @Autowired
    public AdminResourceController(final TrailManager trailManager,
                                   final ResourceService resourceService,
                                   final TrailImporterService trailImporterService,
                                   final PlaceManager placeManager,
                                   final AppProperties appProperties,
                                   final TrailFileManager trailFileManager) {
        this.trailManager = trailManager;
        this.resourceService = resourceService;
        this.trailImporterService = trailImporterService;
        this.placeManager = placeManager;
        this.appProperties = appProperties;
        this.trailFileManager = trailFileManager;
    }

    @Operation(summary = "Get the status")
    @PostMapping(path = "/status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResourceGeneratorResponse getGenerationResourceStatus() {
        if (resourceService.isJobRunning().get()) {
            return new ResourceGeneratorResponse(BatchStatus.BUSY);
        }
        return new ResourceGeneratorResponse(BatchStatus.OK);
    }

    @Operation(summary = "Generate all resources belonging to this instance")
    @PostMapping(path = "/regenerate/all",
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
                        appProperties.getInstanceRealm(), true);
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
        trails.forEach(trail -> {
            final String trailId = trail.getId();
            LOGGER.info(format(REGENERATE_FOR_TRAIL_ID_MESSAGE, trailId));
            final List<PlaceDto> targetPlaces = trail.getLocations().stream()
                    .map((it) -> placeManager.getById(it.getPlaceId())).flatMap(Collection::stream)
                    .collect(Collectors.toList());
            trailImporterService.updateResourcesForTrail(
                    trail,
                    targetPlaces,
                    trailFileManager.getFilename(trail));
            LOGGER.info(format(DONE_REGENERATING_FOR_TRAIL_ID, trailId));
        });
    }
}
