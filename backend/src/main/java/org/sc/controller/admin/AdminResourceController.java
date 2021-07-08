package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.ResourceGeneratorResponse;
import org.sc.controller.response.PlaceResponseHelper;
import org.sc.data.model.Trail;
import org.sc.data.repository.TrailDAO;
import org.sc.manager.TrailFileManager;
import org.sc.processor.TrailSimplifierLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.sc.controller.admin.Constants.PREFIX_RESOURCE;
import static org.sc.data.repository.TrailDAO.NO_FILTERING;

@RestController
@RequestMapping(PREFIX_RESOURCE)
public class AdminResourceController {

    private static final AtomicBoolean isMigrationRunning = new AtomicBoolean();
    private final TrailFileManager trailFileManager;
    private final TrailDAO trailDAO;
    private final PlaceResponseHelper placeResponseHelper;

    @Autowired
    public AdminResourceController(final TrailDAO trailDAO,
                                   final TrailFileManager trailFileManager,
                                   final PlaceResponseHelper placeResponseHelper) {
        this.trailDAO = trailDAO;
        this.trailFileManager = trailFileManager;
        this.placeResponseHelper = placeResponseHelper;
    }

    @Operation(summary = "Generate resources")
    @PostMapping(path = "/generate",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResourceGeneratorResponse generate() {
        if (isMigrationRunning.compareAndSet(false, true)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                List<Trail> trails = trailDAO.getTrails(0, 0, TrailSimplifierLevel.FULL, NO_FILTERING);
                trails.forEach(trailFileManager::writeTrailToOfficialGpx);

                isMigrationRunning.set(false);
            });
        }

        return new ResourceGeneratorResponse(Status.OK);
    }
}
