package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.LinkedMediaDto;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.UnLinkeMediaRequestDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.data.validator.LinkedMediaValidator;
import org.sc.data.validator.MediaExistenceValidator;
import org.sc.data.validator.PlaceExistenceValidator;
import org.sc.data.validator.PlaceValidator;
import org.sc.manager.PlaceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(PlaceController.PREFIX)
public class PlaceController {
    public final static String PREFIX = "/place";

    private final PlaceValidator placeValidator;
    private final PlaceManager placeManager;
    private final LinkedMediaValidator linkedMediaValidator;
    private final MediaExistenceValidator mediaExistenceValidator;
    private final PlaceExistenceValidator placeExistenceValidator;

    @Autowired
    public PlaceController(PlaceValidator placeValidator,
                           PlaceManager placeManager,
                           LinkedMediaValidator linkedMediaValidator,
                           MediaExistenceValidator mediaExistenceValidator,
                           PlaceExistenceValidator placeExistenceValidator) {
        this.placeValidator = placeValidator;
        this.placeManager = placeManager;
        this.linkedMediaValidator = linkedMediaValidator;
        this.mediaExistenceValidator = mediaExistenceValidator;
        this.placeExistenceValidator = placeExistenceValidator;
    }

    @Operation(summary = "Retrieve place")
    @GetMapping
    public PlaceResponse get(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                             @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PlaceResponse(Status.OK,
                Collections.emptySet(),
                placeManager.getPaginated(page, count));
    }

    @Operation(summary = "Retrieve place by ID")
    @GetMapping("/{id}")
    public PlaceResponse get(@PathVariable String id) {
        return new PlaceResponse(Status.OK,
                Collections.emptySet(),
                placeManager.getById(id));
    }

    @Operation(summary = "Retrieve place by alternative names or tags")
    @GetMapping("/name/{name}")
    public PlaceResponse getLikeNameOrTags(@PathVariable String name,
                                           @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new PlaceResponse(Status.OK,
                Collections.emptySet(),
                placeManager.getLikeNameOrTags(name, page, count));
    }

    @Operation(summary = "Add media to place")
    @PutMapping("/media/{id}")
    public PlaceResponse addMedia(@PathVariable String id,
                                  @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = linkedMediaValidator.validate(linkedMediaRequest);
        errors.addAll(placeExistenceValidator.validate(id));
        errors.addAll(mediaExistenceValidator.validate(linkedMediaRequest.getId()));
        if (errors.isEmpty()) {
            final List<PlaceDto> linkedMediaResultDtos =
                    placeManager.linkMedia(id, linkedMediaRequest);
            return new PlaceResponse(Status.OK, Collections.emptySet(), linkedMediaResultDtos);
        }
        return new PlaceResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @Operation(summary = "Delete media from place")
    @DeleteMapping("/media/{id}")
    public PlaceResponse deleteMedia(@PathVariable String id,
                                     @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = placeExistenceValidator.validate(id);
        errors.addAll(mediaExistenceValidator.validate(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            final List<PlaceDto> linkedMediaResultDtos =
                    placeManager.unlinkMedia(id, unLinkeMediaRequestDto);
            return new PlaceResponse(Status.OK, Collections.emptySet(), linkedMediaResultDtos);
        }
        return new PlaceResponse(Status.ERROR, errors, Collections.emptyList());
    }

    @Operation(summary = "Add place")
    @PutMapping
    public PlaceResponse create(@RequestBody PlaceDto place) {
        Set<String> validationErrors = placeValidator.validate(place);
        if (!validationErrors.isEmpty()) {
            return new PlaceResponse(Status.ERROR, validationErrors, Collections.emptyList());
        }
        List<PlaceDto> placeDtoList = placeManager.create(place);
        return new PlaceResponse(Status.OK, Collections.emptySet(), placeDtoList);
    }

    @Operation(summary = "Delete place")
    @DeleteMapping("/{id}")
    public PlaceResponse delete(@PathVariable String id) {
        return new PlaceResponse(Status.OK, Collections.emptySet(), placeManager.deleteById(id));
    }

    @Operation(summary = "Update place")
    @PostMapping
    public PlaceResponse update(@RequestBody PlaceDto place) {
        Set<String> validationErrors = placeValidator.validate(place);
        if (!validationErrors.isEmpty()) {
            return new PlaceResponse(Status.ERROR, validationErrors, Collections.emptyList());
        }
        List<PlaceDto> placeDtoList = placeManager.update(place);
        return new PlaceResponse(Status.OK, Collections.emptySet(), placeDtoList);
    }
}
