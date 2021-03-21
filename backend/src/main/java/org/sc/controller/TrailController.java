package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.data.validator.LinkedMediaValidator;
import org.sc.data.validator.MediaExistenceValidator;
import org.sc.data.validator.PlaceRefValidator;
import org.sc.data.validator.TrailImportValidator;
import org.sc.data.validator.trail.TrailExistenceValidator;
import org.sc.manager.TrailImporterManager;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    private final TrailExistenceValidator trailExistenceValidator;
    private final LinkedMediaValidator linkedMediaValidator;
    private final MediaExistenceValidator mediaExistanceValidator;
    private final PlaceRefValidator placeRefValidator;
    private final ControllerPagination controllerPagination;
    private final TrailImporterManager trailImporterManager;
    private final TrailImportValidator trailValidator;

    @Autowired
    public TrailController(final TrailManager trailManager,
                           final LinkedMediaValidator linkedMediaValidator,
                           final TrailExistenceValidator trailExistenceValidator,
                           final MediaExistenceValidator mediaExistanceValidator,
                           final PlaceRefValidator placeRefValidator,
                           final ControllerPagination controllerPagination,
                           final TrailImporterManager trailImporterManager,
                           final TrailImportValidator trailValidator) {
        this.trailManager = trailManager;
        this.linkedMediaValidator = linkedMediaValidator;
        this.trailExistenceValidator = trailExistenceValidator;
        this.mediaExistanceValidator = mediaExistanceValidator;
        this.placeRefValidator = placeRefValidator;
        this.controllerPagination = controllerPagination;
        this.trailImporterManager = trailImporterManager;
        this.trailValidator = trailValidator;
    }

    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailManager.count();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return constructTrailResponse(Collections.emptySet(), trailManager.get(light, skip, limit),
                trailManager.count(), skip, limit);
    }

    @GetMapping("/{id}")
    public TrailResponse getById(@PathVariable String id,
                                 @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return constructTrailResponse(Collections.emptySet(), trailManager.getById(id, light),
                trailManager.count(),
                Constants.ONE, Constants.ONE);
    }

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

    @PostMapping("/place/{id}")
    public TrailResponse addPlaceToTrail(@PathVariable String id,
                                         @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = trailExistenceValidator.validate(id);
        errors.addAll(placeRefValidator.validate(placeRefDto));
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

    @DeleteMapping("/place/{id}")
    public TrailResponse removePlaceFromTrail(@PathVariable String id,
                                              @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = trailExistenceValidator.validate(id);
        errors.addAll(placeRefValidator.validate(placeRefDto));
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

    @PostMapping("/media/{id}")
    public TrailResponse addMediaToTrail(@PathVariable String id,
                                         @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = linkedMediaValidator.validate(linkedMediaRequest);
        errors.addAll(trailExistenceValidator.validate(id));
        errors.addAll(mediaExistanceValidator.validate(linkedMediaRequest.getId()));
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

    @DeleteMapping("/media/{id}")
    public TrailResponse removeMediaFromTrail(@PathVariable String id,
                                              @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = trailExistenceValidator.validate(id);
        errors.addAll(mediaExistanceValidator.validate(unLinkeMediaRequestDto.getId()));
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

    @DeleteMapping("/{id}")
    public TrailResponse deleteById(@PathVariable String id,
                                    @RequestParam(required = false, defaultValue = "false") boolean isPurged) {
        final List<TrailDto> deleted = trailManager.delete(id, isPurged);
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

    @PutMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailResponse importTrail(@RequestBody TrailImportDto request) {
        final Set<String> errors = trailValidator.validate(request);
        if (errors.isEmpty()) {
            List<TrailDto> savedTrail = trailImporterManager.save(request);
            return constructTrailResponse(emptySet(), savedTrail, trailManager.count(),
                    Constants.ZERO, Constants.ONE);
        }
        return constructTrailResponse(errors, emptyList(), trailManager.count(),
                Constants.ZERO, Constants.ONE);
    }

    @PostMapping
    public TrailResponse update(@RequestBody TrailDto trailDto) {
        throw new NotImplementedException();
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
