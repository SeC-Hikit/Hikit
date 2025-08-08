package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.MunicipalityIntersectionResponse;
import org.sc.data.validator.GeneralValidator;
import org.sc.service.TrailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static org.sc.controller.admin.Constants.PREFIX_TRAIL;

@RestController
@RequestMapping(PREFIX_TRAIL)
public class AdminMunicipalityController {

    private final TrailService trailService;
    private final GeneralValidator generalValidator;

    @Autowired
    public AdminMunicipalityController(
            final TrailService trailService,
            final GeneralValidator generalValidator) {
        this.trailService = trailService;
        this.generalValidator = generalValidator;
    }

    @Operation(summary = "Find all existing trail municipalities intersections and distances for a given trail")
    @GetMapping("/intersect/{trailId}")
    public MunicipalityIntersectionResponse
    findTrailIntersection(@PathVariable String trailId) {
        final Set<String> validate = generalValidator.validateUpdateTrail(trailId);

        if (!validate.isEmpty()) return new MunicipalityIntersectionResponse(List.of());

        final List<MunicipalityToTrailDto> intersections =
                trailService.intersectMunicipalitiesCalculatingDistances(trailId);
        return new MunicipalityIntersectionResponse(intersections);
    }

}
