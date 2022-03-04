package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.LinkedMediaDto;
import org.sc.common.rest.PoiDto;
import org.sc.common.rest.UnLinkeMediaRequestDto;
import org.sc.common.rest.response.PoiResponse;
import org.sc.controller.response.PoiResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.PoiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.controller.Constants.*;
import static org.sc.controller.admin.Constants.PREFIX_POI;

@RestController
@RequestMapping(PREFIX_POI)
public class AdminPoiController {

    private final PoiManager poiManager;
    private final GeneralValidator generalValidator;
    private final PoiResponseHelper poiResponseHelper;

    @Autowired
    public AdminPoiController(final PoiManager poiManager,
                         final GeneralValidator generalValidator,
                         final PoiResponseHelper poiResponseHelper) {
        this.poiManager = poiManager;
        this.generalValidator = generalValidator;
        this.poiResponseHelper = poiResponseHelper;
    }

    @Operation(summary = "Create a POI")
    @PostMapping
    public PoiResponse create(@RequestBody PoiDto poiDto) {
        final Set<String> errors = generalValidator.validate(poiDto);
        if (errors.isEmpty()) {
            return poiResponseHelper.constructResponse(emptySet(), poiManager.upsert(poiDto),
                    poiManager.count(), ZERO, ONE);
        }
        return poiResponseHelper.constructResponse(errors, emptyList(),
                poiManager.count(), ZERO, ONE);
    }

    @Operation(summary = "Update a POI")
    @PutMapping
    public PoiResponse update(@RequestBody PoiDto poiDto) {
        final Set<String> errors = generalValidator.validate(poiDto);
        errors.addAll(generalValidator.validateUpdatePoi(poiDto.getId()));
        if (errors.isEmpty()) {
            return poiResponseHelper.constructResponse(emptySet(), poiManager.upsert(poiDto),
                    poiManager.count(), ZERO, ONE);
        }
        return poiResponseHelper.constructResponse(errors, emptyList(),
                poiManager.count(), ZERO, ONE);
    }

    @Operation(summary = "Add media to POI")
    @PostMapping("/media/{id}")
    public PoiResponse addMediaToPoi(@PathVariable String id,
                                     @RequestBody LinkedMediaDto linkedMediaRequest) {
        final Set<String> errors = generalValidator.validatePoiExistence(id);
        errors.addAll(generalValidator.validate(linkedMediaRequest));
        errors.addAll(generalValidator.validateUpdatePoi(id));
        if (errors.isEmpty()) {
            final List<PoiDto> poiDtos =
                    poiManager.linkMedia(id, linkedMediaRequest);
            return poiResponseHelper.constructResponse(emptySet(), poiDtos,
                    poiManager.count(), ZERO, ONE);
        }
        return poiResponseHelper.constructResponse(errors, emptyList(),
                poiManager.count(), ZERO, ONE);
    }

    @Operation(summary = "Remove media from POI")
    @DeleteMapping("/media/{id}")
    public PoiResponse removeMediaFromPoi(@PathVariable String id,
                                          @RequestBody UnLinkeMediaRequestDto unLinkeMediaRequestDto) {
        final Set<String> errors = generalValidator.validatePoiExistence(id);
        errors.addAll(generalValidator.validateMediaExistence(unLinkeMediaRequestDto.getId()));
        errors.addAll(generalValidator.validateUpdatePoi(id));
        if (errors.isEmpty()) {
            return poiResponseHelper.constructResponse(emptySet(), poiManager.unlinkMedia(id, unLinkeMediaRequestDto),
                    poiManager.count(), ZERO, ONE);
        }
        return poiResponseHelper.constructResponse(errors, emptyList(),
                poiManager.count(), ZERO, ONE);
    }

    @Operation(summary = "Delete POI")
    @DeleteMapping("/{id}")
    public PoiResponse deletePoi(@PathVariable String id) {
        final Set<String> errors = generalValidator.validateUpdatePoi(id);
        if(errors.isEmpty()) {
            final List<PoiDto> deleted = poiManager.deleteById(id);
            return poiResponseHelper.constructResponse(emptySet(), deleted,
                    poiManager.count(), ZERO, ONE);
        }
        return poiResponseHelper.constructResponse(errors, emptyList(),
                poiManager.count(), ZERO, ONE);
    }
}
