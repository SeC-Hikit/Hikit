package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.Main;
import org.sc.common.rest.BatchStatus;
import org.sc.common.rest.GenerateRequestDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.ResourceGeneratorResponse;
import org.sc.configuration.AppProperties;
import org.sc.controller.response.PlaceResponseHelper;
import org.sc.data.model.Trail;
import org.sc.data.repository.TrailDAO;
import org.sc.manager.TrailFileManager;
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
import static org.sc.data.repository.TrailDAO.NO_FILTERING;

@RestController
@RequestMapping(PREFIX_RESOURCE)
public class AdminResourceController {

    final static Logger LOGGER = LoggerFactory.getLogger(AdminResourceController.class);

    private static final AtomicBoolean isMigrationRunning = new AtomicBoolean();
    public static final String COULD_NOT_ERROR = "Could not re-generate resources for Trail with Id '%s'";
    private final TrailFileManager trailFileManager;
    private final AppProperties appProperties;
    private final TrailDAO trailDAO;

    @Autowired
    public AdminResourceController(final TrailDAO trailDAO,
                                   final TrailFileManager trailFileManager,
                                   final AppProperties appProperties) {
        this.trailDAO = trailDAO;
        this.trailFileManager = trailFileManager;
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

                final List<Trail> trails = trailDAO.getTrails(0, Integer.MAX_VALUE, TrailSimplifierLevel.FULL,
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
                    final List<Trail> singletonList = trailDAO.getTrailById(id, TrailSimplifierLevel.LOW);
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
                final List<Trail> trails = trailDAO.getTrails(0, Integer.MAX_VALUE, TrailSimplifierLevel.FULL,
                        appProperties.getInstanceRealm());

                doExport(trails);

                isMigrationRunning.set(false);
            });
        }

        return new ResourceGeneratorResponse(BatchStatus.OK);
    }

    private void doExport(List<Trail> trails) {
        trails.forEach(t -> LOGGER.info(format("Going to re-generate file for trail with id '%s'", t.getId())));
        trails.forEach(trailFileManager::writeTrailToOfficialGpx);
    }
}
