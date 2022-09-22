package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.TrailResponse;
import org.sc.controller.response.TrailResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.service.TrailImporterService;
import org.sc.manager.TrailManager;
import org.sc.service.TrailService;
import org.sc.service.TrailUpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.controller.Constants.ONE;
import static org.sc.controller.Constants.ZERO;
import static org.sc.controller.admin.Constants.PREFIX_TRAIL;

@RestController
@RequestMapping(PREFIX_TRAIL)
public class AdminTrailController {

    private final TrailService trailService;
    private final TrailManager trailManager;
    private final GeneralValidator generalValidator;
    private final TrailImporterService trailImporterService;
    private final TrailUpgradeService trailUpgradeService;
    private final TrailResponseHelper trailResponseHelper;

    @Autowired
    public AdminTrailController(
            final TrailService trailService,
            final TrailManager trailManager,
            final GeneralValidator generalValidator,
            final TrailResponseHelper trailResponseHelper,
            final TrailImporterService trailImporterService,
            final TrailUpgradeService trailUpgradeService) {
        this.trailService = trailService;
        this.trailManager = trailManager;
        this.generalValidator = generalValidator;
        this.trailResponseHelper = trailResponseHelper;
        this.trailImporterService = trailImporterService;
        this.trailUpgradeService = trailUpgradeService;
    }

    @Operation(summary = "Add place to trail")
    @PostMapping("/place/{id}")
    public TrailResponse addPlaceToTrail(@PathVariable String id,
                                         @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = generalValidator.validateUpdateTrail(id);
        errors.addAll(generalValidator.validate(placeRefDto));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedPlaceResultDtos =
                    trailService.linkTrailToPlace(id, placeRefDto);
            return trailResponseHelper.constructResponse(errors, linkedPlaceResultDtos,
                    trailManager.count(),
                    ONE, ONE);
        }
        return trailResponseHelper.constructResponse(errors,
                emptyList(),
                trailManager.count(),
                ONE, ONE);
    }

    @Operation(summary = "Remove place from trail")
    @DeleteMapping("/place/{id}")
    public TrailResponse removePlaceFromTrail(@PathVariable String id,
                                              @RequestBody PlaceRefDto placeRefDto) {
        Set<String> errors = generalValidator.validateUpdateTrail(id);
        errors.addAll(generalValidator.validate(placeRefDto));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedPlaceResultDtos =
                    trailService.unlinkPlace(id, placeRefDto);
            return trailResponseHelper.constructResponse(errors, linkedPlaceResultDtos,
                    trailManager.count(),
                    ONE, ONE);
        }
        return trailResponseHelper.constructResponse(errors, emptyList(),
                trailManager.count(),
                ONE, ONE);
    }

    @Operation(summary = "Add media to trail")
    @PostMapping("/media/{id}")
    public TrailResponse addMediaToTrail(@PathVariable String id,
                                         @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = generalValidator.validate(linkedMediaRequest);
        errors.addAll(generalValidator.validateUpdateTrail(id));
        errors.addAll(generalValidator.validateMediaExistence(linkedMediaRequest.getId()));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedMediaResultDtos =
                    trailManager.linkMedia(id, linkedMediaRequest);
            return trailResponseHelper.constructResponse(errors, linkedMediaResultDtos,
                    trailManager.count(),
                    ONE, ONE);
        }
        return trailResponseHelper.constructResponse(errors, emptyList(),
                trailManager.count(),
                ZERO, ONE);
    }

    @Operation(summary = "Remove media from trail")
    @DeleteMapping("/media/{id}")
    public TrailResponse removeMediaFromTrail(@PathVariable String id,
                                              @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = generalValidator.validateUpdateTrail(id);
        errors.addAll(generalValidator.validateMediaExistence(unLinkeMediaRequestDto.getId()));
        if (errors.isEmpty()) {
            final List<TrailDto> linkedMediaResultDtos =
                    trailManager.unlinkMedia(id, unLinkeMediaRequestDto);
            return trailResponseHelper.constructResponse(errors, linkedMediaResultDtos,
                    trailManager.count(),
                    ONE, ONE);
        }
        return trailResponseHelper.constructResponse(errors, emptyList(),
                trailManager.count(),
                ZERO, ONE);
    }

    @Operation(summary = "Remove trail by ID")
    @DeleteMapping("/{id}")
    public TrailResponse deleteById(@PathVariable String id) {
        final Set<String> errors = generalValidator.validateUpdateTrail(id);
        if (errors.isEmpty()) {
            final List<TrailDto> deleted = trailService.deleteById(id);
            return trailResponseHelper.constructResponse(emptySet(), deleted,
                    trailManager.count(),
                    ONE, ONE);
        }
        return trailResponseHelper.constructResponse(emptySet(), emptyList(),
                trailManager.count(),
                ZERO, ONE);
    }

    @Operation(summary = "Creates a new trail")
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TrailResponse importTrail(@RequestBody TrailImportDto request) {
        final Set<String> errors = generalValidator.validate(request);
        if (!errors.isEmpty()) {
            return trailResponseHelper.constructResponse(errors, emptyList(), trailManager.count(),
                    ZERO, ONE);
        }
        final List<TrailDto> savedTrail = trailImporterService.save(request);
        return trailResponseHelper.constructResponse(emptySet(), savedTrail, trailManager.count(),
                ZERO, ONE);
    }

    @Operation(summary = "Upgrade an existing trail possibly modifying its connections or relations")
    @PutMapping("/upgrade/{id}")
    public TrailResponse upgradeTrail(@RequestBody TrailUpgradeDto trailDto) {
        final Set<String> errors = generalValidator.validate(trailDto);
        errors.addAll(generalValidator.validateUpdateTrail(trailDto.getId()));

        if (errors.isEmpty()) {
            List<TrailDto> updatedTrail = trailUpgradeService.upgradeTrail(trailDto);
            return trailResponseHelper.constructResponse(emptySet(), updatedTrail,
                    updatedTrail.size(), ZERO, ONE);
        }

        return trailResponseHelper.constructResponse(errors, emptyList(),
                ZERO, ZERO, ONE);
    }


    @Operation(summary = "Update an existing trail without modifying its connections or relations")
    @PutMapping("/update")
    public TrailResponse updateTrail(@RequestBody TrailDto trailDto) {
        final Set<String> errors = generalValidator.validate(trailDto);
        errors.addAll(generalValidator.validateUpdateTrail(trailDto.getId()));

        if (errors.isEmpty()) {
            List<TrailDto> updatedTrail = trailImporterService.updateTrail(trailDto);
            return trailResponseHelper.constructResponse(emptySet(), updatedTrail,
                    updatedTrail.size(), ZERO, ONE);
        }

        return trailResponseHelper.constructResponse(errors, emptyList(),
                ZERO, ZERO, ONE);
    }

    @Operation(summary = "Changes a trail to PUBLIC/DRAFT status")
    @PostMapping("/status")
    public TrailResponse updateTrailStatus(@RequestBody TrailDto trailDto) {
        final Set<String> errors = generalValidator.validate(trailDto);
        errors.addAll(generalValidator.validateUpdateTrail(trailDto.getId()));

        if (errors.isEmpty()) {
            List<TrailDto> updatedTrail = trailService.switchToStatus(trailDto);
            return trailResponseHelper.constructResponse(emptySet(), updatedTrail,
                    updatedTrail.size(), ZERO, ONE);
        }

        return trailResponseHelper.constructResponse(errors, emptyList(),
                ZERO, ZERO, ONE);
    }

}
