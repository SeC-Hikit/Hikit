package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.PoiResponse;
import org.sc.data.validator.LinkedMediaValidator;
import org.sc.data.validator.MediaExistenceValidator;
import org.sc.data.validator.poi.PoiExistenceValidator;
import org.sc.data.validator.poi.PoiValidator;
import org.sc.manager.PoiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Collections.*;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(POIController.PREFIX)
public class POIController {

    public final static String PREFIX = "/poi";
    private final static Logger LOGGER = Logger.getLogger(POIController.class.getName());

    private final PoiManager poiManager;
    private final PoiValidator poiValidator;
    private final PoiExistenceValidator poiExistenceValidator;
    private final LinkedMediaValidator linkedMediaValidator;
    private final MediaExistenceValidator mediaExistanceValidator;
    private final ControllerPagination controllerPagination;

    @Autowired
    public POIController(final PoiManager poiManager,
                         final PoiValidator poiValidator,
                         final PoiExistenceValidator poiExistenceValidator,
                         final LinkedMediaValidator linkedMediaValidator,
                         final MediaExistenceValidator mediaExistanceValidator,
                         final ControllerPagination controllerPagination) {
        this.poiManager = poiManager;
        this.poiValidator = poiValidator;
        this.poiExistenceValidator = poiExistenceValidator;
        this.linkedMediaValidator = linkedMediaValidator;
        this.mediaExistanceValidator = mediaExistanceValidator;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Count all POIs in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = poiManager.count();
        return new CountResponse(Status.OK, emptySet(), new CountDto(count));
    }

    @Operation(summary = "Retrieve POI")
    @GetMapping
    public PoiResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), poiManager.getPoiPaginated(skip, limit),
                poiManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve POI by ID")
    @GetMapping("/{id}")
    public PoiResponse get(@PathVariable String id) {
        return constructResponse(emptySet(), poiManager.getPoiByID(id),
                poiManager.count(), Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Retrieve POI by trail code")
    @GetMapping("/code/{code}")
    public PoiResponse getByTrail(@PathVariable String code,
                                  @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), poiManager.getPoiByTrailId(code, skip, limit),
                poiManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve POI by macro-type")
    @GetMapping("/type/{type}")
    public PoiResponse getByMacro(@PathVariable String type,
                                  @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                  @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), poiManager.getPoiByMacro(type, skip, limit),
                poiManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve POI by name or tag")
    @GetMapping("/name/{name}")
    public PoiResponse getByNameOrTags(@PathVariable String name,
                                       @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                       @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(), poiManager.getPoiByName(name, skip, limit),
                poiManager.count(), skip, limit);
    }

    @Operation(summary = "Update POI in DB (or create POI, if not present)")
    @PutMapping
    public PoiResponse upsertPoi(@RequestBody PoiDto poiDto) {
        final Set<String> errors = poiValidator.validate(poiDto);
        if (errors.isEmpty()) {
            return constructResponse(emptySet(), poiManager.upsertPoi(poiDto),
                    poiManager.count(), Constants.ZERO, Constants.ONE);
        }
        return constructResponse(errors, emptyList(),
                poiManager.count(), Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Add media to POI")
    @PutMapping("/media/{id}")
    public PoiResponse addMediaToPoi(@PathVariable String id,
                                     @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = poiExistenceValidator.validate(id);
        errors.addAll(linkedMediaValidator.validate(linkedMediaRequest));
        if (errors.isEmpty()) {
            final List<PoiDto> poiDtos =
                    poiManager.linkMedia(id, linkedMediaRequest);
            return constructResponse(emptySet(), poiDtos,
                    poiManager.count(), Constants.ZERO, Constants.ONE);
        }
        return constructResponse(errors, emptyList(),
                poiManager.count(), Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Remove media from POI")
    @DeleteMapping("/media/{id}")
    public PoiResponse removeMediaFromPoi(@PathVariable String id,
                                          @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = poiExistenceValidator.validate(id);
        errors.addAll(mediaExistanceValidator.validate(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            return constructResponse(emptySet(), poiManager.unlinkMedia(id, unLinkeMediaRequestDto),
                    poiManager.count(), Constants.ZERO, Constants.ONE);
        }
        return constructResponse(errors, emptyList(),
                poiManager.count(), Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Delete POI")
    @DeleteMapping("/{id}")
    public PoiResponse deletePoi(@PathVariable String id) {
        List<PoiDto> deleted = poiManager.deleteById(id);
        return constructResponse(emptySet(), deleted,
                poiManager.count(), Constants.ZERO, Constants.ONE);
    }

    private PoiResponse constructResponse(Set<String> errors,
                                          List<PoiDto> dtos,
                                          long totalCount,
                                          int skip,
                                          int limit) {
        if (!errors.isEmpty()) {
            return new PoiResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new PoiResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }


}
