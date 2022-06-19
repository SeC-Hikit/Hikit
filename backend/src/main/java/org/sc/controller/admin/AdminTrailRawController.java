package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.response.TrailRawResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.data.validator.auth.AuthRealmValidator;
import org.sc.manager.TrailRawManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Set;

import static org.sc.controller.Constants.ONE;
import static org.sc.controller.Constants.ZERO;
import static org.sc.controller.admin.Constants.PREFIX_RAW;

@RestController
@RequestMapping(PREFIX_RAW)
public class AdminTrailRawController {

    private final TrailRawManager trailRawManager;
    private final TrailRawResponseHelper trailRawResponseHelper;
    private final GeneralValidator generalValidator;

    @Autowired
    public AdminTrailRawController(final TrailRawManager trailRawManager,
                                   final TrailRawResponseHelper trailRawResponseHelper,
                                   final GeneralValidator generalValidator) {
        this.trailRawManager = trailRawManager;
        this.trailRawResponseHelper = trailRawResponseHelper;
        this.generalValidator = generalValidator;
    }

    @Operation(summary = "Delete a single raw trail")
    @DeleteMapping("/{id}")
    public TrailRawResponse deleteById(final @PathVariable String id) {
        final Set<String> errors = generalValidator.validateDeleteRawTrail(id);
        if(errors.isEmpty()) {
            return trailRawResponseHelper
                    .constructResponse(Collections.emptySet(),
                            trailRawManager.deleteById(id),
                            ONE, ZERO, ONE);
        }
        return trailRawResponseHelper.constructResponse(errors, Collections.emptyList(), ZERO, ZERO, ONE);
    }
}
