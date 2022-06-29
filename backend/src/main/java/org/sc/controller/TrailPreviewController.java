package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.response.TrailMappingResponse;
import org.sc.common.rest.response.TrailPreviewResponse;
import org.sc.controller.response.ControllerPagination;
import org.sc.controller.response.TrailPreviewResponseHelper;
import org.sc.manager.TrailPreviewManager;
import org.sc.processor.TrailSimplifierLevel;
import org.sc.service.TrailPreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(TrailPreviewController.PREFIX)
public class TrailPreviewController {

    public final static String PREFIX = "/preview";

    private final TrailPreviewManager trailManager;
    private final TrailPreviewResponseHelper trailPreviewResponseHelper;
    private final TrailPreviewService trailPreviewService;
    private final ControllerPagination controllerPagination;

    @Autowired
    public TrailPreviewController(final TrailPreviewManager trailManager,
                                  final TrailPreviewResponseHelper trailPreviewResponseHelper,
                                  final TrailPreviewService trailPreviewService,
                                  final ControllerPagination controllerPagination) {
        this.trailManager = trailManager;
        this.trailPreviewResponseHelper = trailPreviewResponseHelper;
        this.trailPreviewService = trailPreviewService;
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
                .constructResponse(emptySet(), trailManager.getPreviews(skip, limit, realm, isDraftTrailVisible),
                        trailManager.countPreviewByRealm(realm, isDraftTrailVisible), skip, limit);
    }

    @Operation(summary = "Retrieve trail ID/Code mapping")
    @GetMapping("/map")
    public TrailMappingResponse getTrailMapping(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                                @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm,
                                                @RequestParam(required = false, defaultValue = "false") boolean isDraftTrailVisible) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailPreviewResponseHelper
                .constructMappingResponse(emptySet(), trailManager.getMappings(skip, limit, realm, isDraftTrailVisible),
                        trailManager.countPreviewByRealm(realm, isDraftTrailVisible), skip, limit);
    }

    @Operation(summary = "Retrieve RAW trail previews")
    @GetMapping("/raw")
    public TrailPreviewResponse getRawTrailPreviews(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return trailPreviewResponseHelper
                .constructResponse(emptySet(), trailManager.getRawPreviews(skip, limit, realm),
                        trailManager.countRaw(realm), skip, limit);
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
                        trailManager.countFindingByCode(realm, code, isDraftTrailVisible), skip, limit);
    }

    @Operation(summary = "Retrieve trails by location name or trail name")
    @GetMapping("/find/name/{name}")
    public TrailPreviewResponse findByLocationOrTrailNames(@PathVariable String name,
                                                    @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm,
                                                    @RequestParam(defaultValue = "LOW") TrailSimplifierLevel level,
                                                    @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                    @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
                                                    @RequestParam(defaultValue = "false") boolean isDraftTrailVisible) {
        return trailPreviewResponseHelper
                .constructResponse(Collections.emptySet(),
                        trailPreviewService.searchByLocationNameOrName(
                                name, realm, isDraftTrailVisible, skip, limit),
                        trailManager.countFindingByNameOrLocationName(name, realm, isDraftTrailVisible),
                        Constants.ONE, Constants.ONE);
    }

}
