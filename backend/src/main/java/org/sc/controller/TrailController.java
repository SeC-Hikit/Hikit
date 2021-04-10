package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.geo.SquareDto;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.data.validator.*;
import org.sc.data.validator.trail.TrailExistenceValidator;
import org.sc.manager.TrailImporterManager;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(TrailController.PREFIX)
public class TrailController {

    public final static String PREFIX = "/trail";

    private final TrailManager trailManager;
    private final GeneralValidator generalValidator;
    private final ControllerPagination controllerPagination;
    private final TrailImporterManager trailImporterManager;

    @Autowired
    public TrailController(final TrailManager trailManager,
                           final GeneralValidator generalValidator,
                           final ControllerPagination controllerPagination,
                           final TrailImporterManager trailImporterManager) {
        this.trailManager = trailManager;
        this.generalValidator = generalValidator;
        this.controllerPagination = controllerPagination;
        this.trailImporterManager = trailImporterManager;
    }

    @Operation(summary = "Count all trails in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailManager.count();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @Operation(summary = "Retrieve trail")
    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return constructTrailResponse(Collections.emptySet(), trailManager.get(light, skip, limit),
                trailManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve trail by ID")
    @GetMapping("/{id}")
    public TrailResponse getById(@PathVariable String id,
                                 @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return constructTrailResponse(Collections.emptySet(), trailManager.getById(id, light),
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
        return constructTrailResponse(Collections.emptySet(), byPlaceRefId,
                trailManager.count(),
                skip, limit);
    }

    @Operation(summary = "Add place to trail")
    @PostMapping("/place/{id}")
    public TrailResponse addPlaceToTrail(@PathVariable String id,
                                         @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = generalValidator.validateTrailExistence(id);
        errors.addAll(generalValidator.validate(placeRefDto));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedPlaceResultDtos =
                    trailManager.linkPlace(id, placeRefDto);
            return constructTrailResponse(errors, linkedPlaceResultDtos,
                    trailManager.count(),
                    Constants.ONE, Constants.ONE);
        }
        return constructTrailResponse(errors,
                Collections.emptyList(),
                trailManager.count(),
                Constants.ONE, Constants.ONE);
    }

    @Operation(summary = "Remove place from trail")
    @DeleteMapping("/place/{id}")
    public TrailResponse removePlaceFromTrail(@PathVariable String id,
                                              @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = generalValidator.validateTrailExistence(id);
        errors.addAll(generalValidator.validate(placeRefDto));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedPlaceResultDtos =
                    trailManager.unlinkPlace(id, placeRefDto);
            return constructTrailResponse(errors, linkedPlaceResultDtos,
                    trailManager.count(),
                    Constants.ONE, Constants.ONE);
        }
        return constructTrailResponse(errors, Collections.emptyList(),
                trailManager.count(),
                Constants.ONE, Constants.ONE);
    }

    @Operation(summary = "Add media to trail")
    @PostMapping("/media/{id}")
    public TrailResponse addMediaToTrail(@PathVariable String id,
                                         @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = generalValidator.validate(linkedMediaRequest);
        errors.addAll(generalValidator.validateTrailExistence(id));
        errors.addAll(generalValidator.validateMediaExistence(linkedMediaRequest.getId()));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedMediaResultDtos =
                    trailManager.linkMedia(id, linkedMediaRequest);
            return constructTrailResponse(errors, linkedMediaResultDtos,
                    trailManager.count(),
                    Constants.ONE, Constants.ONE);
        }
        return constructTrailResponse(errors, Collections.emptyList(),
                trailManager.count(),
                Constants.ONE, Constants.ONE);
    }

    @Operation(summary = "Remove media from trail")
    @DeleteMapping("/media/{id}")
    public TrailResponse removeMediaFromTrail(@PathVariable String id,
                                              @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = generalValidator.validateTrailExistence(id);
        errors.addAll(generalValidator.validateMediaExistence(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedMediaResultDtos =
                    trailManager.unlinkMedia(id, unLinkeMediaRequestDto);
            return constructTrailResponse(errors, linkedMediaResultDtos,
                    trailManager.count(),
                    Constants.ONE, Constants.ONE);
        }
        return constructTrailResponse(errors, Collections.emptyList(),
                trailManager.count(),
                Constants.ONE, Constants.ONE);
    }

    @Operation(summary = "Remove trail by ID")
    @DeleteMapping("/{id}")
    public TrailResponse deleteById(@PathVariable String id) {
        final List<TrailDto> deleted = trailManager.delete(id);
        if (!deleted.isEmpty()) {
            return constructTrailResponse(Collections.emptySet(), deleted,
                    trailManager.count(),
                    Constants.ONE, Constants.ONE);
        } else {
            return constructTrailResponse(Collections.singleton(
                    format("No trail deleted with id '%s'", id)), deleted,
                    trailManager.count(), Constants.ONE,
                    Constants.ONE);
        }
    }

    @Operation(summary = "Creates a new trail")
    @PutMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailResponse importTrail(@RequestBody TrailImportDto request) {
        final Set<String> errors = generalValidator.validate(request);
        if (errors.isEmpty()) {
            List<TrailDto> savedTrail = trailImporterManager.save(request);
            return constructTrailResponse(emptySet(), savedTrail, trailManager.count(),
                    Constants.ZERO, Constants.ONE);
        }
        return constructTrailResponse(errors, emptyList(), trailManager.count(),
                Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Update an existing trail without modifying its connections or relations")
    @PostMapping
    public TrailResponse updateTrail(@RequestBody TrailDto trailDto) {

        final Set<String> errors = generalValidator.validate(trailDto);

        if (errors.isEmpty()) {
            List<TrailDto> updatedTrail = trailImporterManager.updateTrail(trailDto);
            return constructTrailResponse(emptySet(), updatedTrail,
                    updatedTrail.size(), Constants.ZERO, Constants.ONE);
        }

        return constructTrailResponse(errors, emptyList(),
                Constants.ZERO, Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Add geo-located trails within a defined polygon")
    @PostMapping("/geolocate")
    public TrailResponse geoLocateTrail(@RequestBody SquareDto squareDto) {

        final Set<String> errors = generalValidator.validate(squareDto);

        if (errors.isEmpty()) {
            List<TrailDto> updatedTrail = trailManager.findTrailsWithinRectangle(squareDto);
            return constructTrailResponse(emptySet(), updatedTrail,
                    updatedTrail.size(), Constants.ZERO, Constants.ONE);
        }

        return constructTrailResponse(errors, emptyList(),
                Constants.ZERO, Constants.ZERO, Constants.ONE);
    }

    private TrailResponse constructTrailResponse(Set<String> errors,
                                                 List<TrailDto> trailDtos,
                                                 long totalCount,
                                                 int skip,
                                                 int limit) {
        if (!errors.isEmpty()) {
            return new TrailResponse(Status.ERROR, errors, trailDtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailResponse(Status.OK, errors, trailDtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }


}
