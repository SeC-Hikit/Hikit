package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.response.TrailResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.TrailImporterManager;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailController.PREFIX)
public class TrailController {

    public final static String PREFIX = "/trail";

    protected final TrailManager trailManager;
    protected final GeneralValidator generalValidator;
    protected final TrailResponseHelper trailResponseHelper;
    protected final TrailImporterManager trailManagementManager;
    protected final AuthFacade authenticationProvider;

    @Autowired
    public TrailController(final TrailManager trailManager,
                           final GeneralValidator generalValidator,
                           final TrailResponseHelper trailResponseHelper,
                           final TrailImporterManager trailManagementManager,
                           final AuthFacade authFacade) {
        this.trailManager = trailManager;
        this.generalValidator = generalValidator;
        this.trailResponseHelper = trailResponseHelper;
        this.trailManagementManager = trailManagementManager;
        this.authenticationProvider = authFacade;
    }


    @Operation(summary = "Retrieve trail")
    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = "false") Boolean light,
            @RequestParam(required = false, defaultValue = "*") String realm) {
        return trailResponseHelper
                .constructResponse(Collections.emptySet(), trailManager.get(light, skip, limit, realm),
                        trailManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve trail by ID")
    @GetMapping("/{id}")
    public TrailResponse getById(@PathVariable String id,
                                 @RequestParam(required = false, defaultValue = "false") Boolean light, String level) {
        return trailResponseHelper
                .constructResponse(Collections.emptySet(), trailManager.getById(id, light, level),
                        trailManager.count(),
                        Constants.ONE, Constants.ONE);
    }

    @Operation(summary = "Retrieve trail by place ID")
    @GetMapping("/place/{id}")
    public TrailResponse getByPlaceId(@PathVariable String id,
                                      @RequestParam(required = false, defaultValue = "false") Boolean light,
                                      @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                      @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        List<TrailDto> byPlaceRefId = trailManager.getByPlaceRefId(id, light, skip, limit);
        return trailResponseHelper.constructResponse(Collections.emptySet(), byPlaceRefId,
                trailManager.count(),
                skip, limit);
    }

    @Operation(summary = "Count all trails in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailManager.count();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

}
