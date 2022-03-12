package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.response.TrailMappingResponse;
import org.sc.common.rest.response.TrailPreviewResponse;
import org.sc.controller.response.TrailPreviewResponseHelper;
import org.sc.manager.TrailPreviewManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoConstants.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(TrailPreviewController.PREFIX)
public class TrailPreviewController {

    public final static String PREFIX = "/preview";

    private final TrailPreviewManager trailManager;
    private final TrailPreviewResponseHelper trailPreviewResponseHelper;
    private final ControllerPagination controllerPagination;

    @Autowired
    public TrailPreviewController(final TrailPreviewManager trailManager,
                                  final TrailPreviewResponseHelper trailPreviewResponseHelper,
                                  final ControllerPagination controllerPagination) {
        this.trailManager = trailManager;
        this.trailPreviewResponseHelper = trailPreviewResponseHelper;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Retrieve trail previews")
    @GetMapping
    public TrailPreviewResponse getTrailPreviews(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                 @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                                 @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm,
                                                 @RequestParam(defaultValue = "false") boolean isDraftTrailVisible) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailPreviewResponseHelper
                .constructResponse(emptySet(), trailManager.getPreviews(skip, limit, realm),
                        trailManager.countPreviewByRealm(realm), skip, limit);
    }

    @Operation(summary = "Retrieve trail ID/Code mapping")
    @GetMapping("/map")
    public TrailMappingResponse getTrailMapping(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                                @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailPreviewResponseHelper
                .constructMappingResponse(emptySet(), trailManager.getMappings(skip, limit, realm),
                        trailManager.countPreviewByRealm(realm), skip, limit);
    }

    @Operation(summary = "Retrieve RAW trail previews")
    @GetMapping("/raw")
    public TrailPreviewResponse getRawTrailPreviews(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailPreviewResponseHelper
                .constructResponse(emptySet(), trailManager.getRawPreviews(skip, limit),
                        trailManager.countRaw(), skip, limit);
    }

    @Operation(summary = "Retrieve preview by ID")
    @GetMapping("/{id}")
    public TrailPreviewResponse getPreviewById(@PathVariable String id) {
        return trailPreviewResponseHelper
                .constructResponse(emptySet(), trailManager.getPreviewById(id),
                        trailManager.countPreview(), Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Find trail preview by trail code")
    @GetMapping("/find/code/{code}")
    public TrailPreviewResponse findByTrailCode(@PathVariable String code,
                                                @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                                @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm,
                                                @RequestParam(defaultValue = "false") boolean isDraftTrailVisible) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailPreviewResponseHelper
                .constructResponse(emptySet(), trailManager.findPreviewsByCode(code, skip, limit, realm, isDraftTrailVisible),
                        trailManager.countFindingByCode(code, isDraftTrailVisible), skip, limit);
    }
}
