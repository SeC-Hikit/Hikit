package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.TrailResponse;
import org.sc.configuration.auth.AuthFacade;
import org.sc.controller.ControllerPagination;
import org.sc.controller.TrailController;
import org.sc.data.validator.GeneralValidator;
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
import static org.sc.controller.admin.Constants.PREFIX_TRAIL;

@RestController
@RequestMapping(PREFIX_TRAIL)
public class AdminTrailController extends TrailController {

    @Autowired
    public AdminTrailController(final TrailManager trailManager,
                                final GeneralValidator generalValidator,
                                final ControllerPagination controllerPagination,
                                final TrailImporterManager trailImporterManager,
                                final AuthFacade authFacade) {
        super(trailManager, generalValidator, controllerPagination, trailImporterManager, authFacade );
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
                    org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
        }
        return constructTrailResponse(errors,
                Collections.emptyList(),
                trailManager.count(),
                org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
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
                    org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
        }
        return constructTrailResponse(errors, Collections.emptyList(),
                trailManager.count(),
                org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
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
                    org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
        }
        return constructTrailResponse(errors, Collections.emptyList(),
                trailManager.count(),
                org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
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
                    org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
        }
        return constructTrailResponse(errors, Collections.emptyList(),
                trailManager.count(),
                org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Remove trail by ID")
    @DeleteMapping("/{id}")
    public TrailResponse deleteById(@PathVariable String id) {
        final List<TrailDto> deleted = trailManager.delete(id);
        if (!deleted.isEmpty()) {
            return constructTrailResponse(Collections.emptySet(), deleted,
                    trailManager.count(),
                    org.sc.controller.Constants.ONE, org.sc.controller.Constants.ONE);
        } else {
            return constructTrailResponse(Collections.singleton(
                    format("No trail deleted with id '%s'", id)), deleted,
                    trailManager.count(), org.sc.controller.Constants.ONE,
                    org.sc.controller.Constants.ONE);
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
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return constructTrailResponse(errors, emptyList(), trailManager.count(),
                org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Update an existing trail without modifying its connections or relations")
    @PostMapping
    public TrailResponse updateTrail(@RequestBody TrailDto trailDto) {

        final Set<String> errors = generalValidator.validate(trailDto);

        if (errors.isEmpty()) {
            List<TrailDto> updatedTrail = trailImporterManager.updateTrail(trailDto);
            return constructTrailResponse(emptySet(), updatedTrail,
                    updatedTrail.size(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }

        return constructTrailResponse(errors, emptyList(),
                org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }


}
