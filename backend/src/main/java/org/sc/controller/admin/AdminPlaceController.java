package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.LinkedMediaDto;
import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.UnLinkeMediaRequestDto;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.controller.Constants;
import org.sc.controller.response.PlaceResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.PlaceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.controller.admin.Constants.PREFIX_PLACE;

@RestController
@RequestMapping(PREFIX_PLACE)
public class AdminPlaceController {

    protected final PlaceManager placeManager;
    protected final PlaceResponseHelper placeResponseHelper;
    protected final GeneralValidator generalValidator;


    @Autowired
    public AdminPlaceController(final PlaceManager placeManager,
                                final PlaceResponseHelper placeResponseHelper,
                                final GeneralValidator generalValidator) {
        this.placeManager = placeManager;
        this.placeResponseHelper = placeResponseHelper;
        this.generalValidator = generalValidator;
    }

    @Operation(summary = "Add media to place")
    @PutMapping("/media/{id}")
    public PlaceResponse addMedia(@PathVariable String id,
                                  @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = generalValidator.validate(linkedMediaRequest);
        errors.addAll(generalValidator.validateMediaExistence(linkedMediaRequest.getId()));
        errors.addAll(generalValidator.validateUpdatePlace(id));
        if (errors.isEmpty()) {
            final List<PlaceDto> linkedMediaResultDtos =
                    placeManager.linkMedia(id, linkedMediaRequest);
            return placeResponseHelper.constructResponse(emptySet(),
                    linkedMediaResultDtos,
                    placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return placeResponseHelper.constructResponse(errors,
                emptyList(),
                placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Delete media from place")
    @PostMapping("/media/{id}")
    public PlaceResponse deleteMedia(@PathVariable String id,
                                     @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = generalValidator.validateUpdatePlace(id);
        errors.addAll(generalValidator.validateMediaExistence(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            final List<PlaceDto> linkedMediaResultDtos =
                    placeManager.unlinkMedia(id, unLinkeMediaRequestDto);
            return placeResponseHelper.constructResponse(emptySet(),
                    linkedMediaResultDtos,
                    placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return placeResponseHelper.constructResponse(errors,
                emptyList(),
                placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Add place")
    @PutMapping
    public PlaceResponse create(@RequestBody PlaceDto place) {
        Set<String> errors = generalValidator.validate(place);
        if (!errors.isEmpty()) {
            return placeResponseHelper.constructResponse(errors,
                    emptyList(),
                    placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        List<PlaceDto> placeDtoList = placeManager.create(place);
        return placeResponseHelper.constructResponse(emptySet(),
                placeDtoList,
                placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Delete place")
    @DeleteMapping("/{id}")
    public PlaceResponse delete(@PathVariable String id) {
        final Set<String> errors = generalValidator.validateUpdatePlace(id);
        if (!errors.isEmpty()) {
            return placeResponseHelper.constructResponse(errors,
                    emptyList(),
                    placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        final List<PlaceDto> content = placeManager.deleteById(id);
        return placeResponseHelper.constructResponse(emptySet(),
                content, placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Update place")
    @PostMapping
    public PlaceResponse update(@RequestBody PlaceDto place) {
        Set<String> errors = generalValidator.validate(place);
        errors.addAll(generalValidator.validateUpdatePlace(place.getId()));
        if (!errors.isEmpty()) {
            return placeResponseHelper.constructResponse(errors,
                    emptyList(),
                    placeManager.count(), org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        final List<PlaceDto> placeDtoList = placeManager.update(place);
        return placeResponseHelper.constructResponse(emptySet(),
                placeDtoList, placeManager.count(), org.sc.controller.Constants.ZERO, Constants.ONE);
    }


}
