package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.common.rest.response.CountResponse;
import org.sc.common.rest.response.TrailResponse;
import org.sc.data.validator.LinkedMediaValidator;
import org.sc.data.validator.MediaExistenceValidator;
import org.sc.data.validator.PlaceRefValidator;
import org.sc.data.validator.trail.TrailExistenceValidator;
import org.sc.manager.TrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
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

    @Autowired
    public TrailController(final TrailManager trailManager,
                           final LinkedMediaValidator linkedMediaValidator,
                           final TrailExistenceValidator trailExistenceValidator,
                           final MediaExistenceValidator mediaExistanceValidator,
                           final PlaceRefValidator placeRefValidator) {
        this.trailManager = trailManager;
        this.linkedMediaValidator = linkedMediaValidator;
        this.trailExistenceValidator = trailExistenceValidator;
        this.mediaExistanceValidator = mediaExistanceValidator;
        this.placeRefValidator = placeRefValidator;
    }

    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = trailManager.countTrail();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @GetMapping
    public TrailResponse get(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count,
            @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(Status.OK, Collections.emptySet(), trailManager.get(light, page, count));
    }

    @GetMapping("/{id}")
    public TrailResponse getById(@PathVariable String id,
                                 @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(Status.OK, Collections.emptySet(), trailManager.getById(id, light));
    }

    @GetMapping("/{code}")
    public TrailResponse getByCode(@PathVariable String code,
                                   @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(Status.OK, Collections.emptySet(), trailManager.getByCode(code, light));
    }

    @GetMapping("/place/{code}")
    public TrailResponse getByPlaceId(@PathVariable String code,
                                      @RequestParam(required = false, defaultValue = "false") Boolean light) {
        return new TrailResponse(Status.OK, Collections.emptySet(), trailManager.getByPlaceRefId(code, light));
    }

    @PostMapping("/place/{id}")
    public TrailResponse addPlaceToTrail(@PathVariable String id,
                                         @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = trailExistenceValidator.validate(id);
        errors.addAll(placeRefValidator.validate(placeRefDto));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedPlaceResultDtos =
                    trailManager.linkPlace(id, placeRefDto);
            return new TrailResponse(Status.OK, Collections.emptySet(), linkedPlaceResultDtos);
        }
        return new TrailResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @DeleteMapping("/place/{id}")
    public TrailResponse removePlaceFromTrail(@PathVariable String id,
                                              @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = trailExistenceValidator.validate(id);
        errors.addAll(placeRefValidator.validate(placeRefDto));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedPlaceResultDtos =
                    trailManager.unlinkPlace(id, placeRefDto);
            return new TrailResponse(Status.OK, Collections.emptySet(), linkedPlaceResultDtos);
        }
        return new TrailResponse(Status.ERROR, errors, Collections.emptyList());
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
            return new TrailResponse(Status.OK, Collections.emptySet(), linkedMediaResultDtos);
        }
        return new TrailResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @DeleteMapping("/media/{code}")
    public TrailResponse removeMediaFromTrail(@PathVariable String code,
                                              @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = trailExistenceValidator.validate(code);
        errors.addAll(mediaExistanceValidator.validate(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedMediaResultDtos =
                    trailManager.unlinkMedia(code, unLinkeMediaRequestDto);
            return new TrailResponse(Status.OK, Collections.emptySet(), linkedMediaResultDtos);
        }
        return new TrailResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @DeleteMapping("/{code}")
    public TrailResponse deleteByCode(@PathVariable String code,
                                      @RequestParam(required = false, defaultValue = "false") boolean isPurged) {
        final List<TrailDto> deleted = trailManager.delete(code, isPurged);
        if (!deleted.isEmpty()) {
            return new TrailResponse(Status.OK, Collections.emptySet(), deleted);
        } else {
            return new TrailResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No trail deleted with code '%s'", code))), Collections.emptyList());
        }
    }
}
