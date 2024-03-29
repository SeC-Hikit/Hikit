package org.sc.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.common.rest.AccessibilityNotificationResolutionDto;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.controller.response.AccessibilityIssueResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.AccessibilityNotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.*;
import static org.sc.controller.Constants.ONE;
import static org.sc.controller.Constants.ZERO;
import static org.sc.controller.admin.Constants.PREFIX_ACCESSIBILITY;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(PREFIX_ACCESSIBILITY)
public class AdminAccessibilityIssueController {
    private final GeneralValidator generalValidator;
    private final AccessibilityIssueResponseHelper accessibilityIssueResponseHelper;
    private final AccessibilityNotificationManager accessibilityNotManager;

    @Autowired
    public AdminAccessibilityIssueController(final AccessibilityNotificationManager accessibilityNotificationManager,
                                             final GeneralValidator generalValidator,
                                             final AccessibilityIssueResponseHelper accessibilityIssueResponseHelper) {
        this.accessibilityNotManager = accessibilityNotificationManager;
        this.generalValidator = generalValidator;
        this.accessibilityIssueResponseHelper = accessibilityIssueResponseHelper;
    }

    @Operation(summary = "Resolve accessibility notification")
    @PostMapping("/resolve")

    public AccessibilityResponse resolveNotification(
            @RequestBody AccessibilityNotificationResolutionDto accessibilityRes,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        final List<AccessibilityNotificationDto> resolved =
                accessibilityNotManager.resolve(accessibilityRes);
        if (resolved.isEmpty()) {
            accessibilityIssueResponseHelper.constructResponse(
                    singleton(format("No accessibility notification was found with id '%s'",
                            accessibilityRes.getId())), emptyList(),
                    accessibilityNotManager.count(realm),
                    ZERO, ONE);
        }
        return accessibilityIssueResponseHelper.constructResponse(emptySet(), resolved,
                accessibilityNotManager.countSolved(realm),
                ZERO, ONE);
    }

    @Operation(summary = "Add accessibility notification")
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AccessibilityResponse create(
            @RequestBody AccessibilityNotificationDto accessibilityNotificationCreation) {
        final Set<String> errors = generalValidator.validate(accessibilityNotificationCreation);
        if (!errors.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(),
                    accessibilityNotManager.count(NO_FILTERING_TOKEN),
                    ZERO, ONE);
        }
        List<AccessibilityNotificationDto> created = accessibilityNotManager.create(accessibilityNotificationCreation);
        return accessibilityIssueResponseHelper
                .constructResponse(errors, created,
                        accessibilityNotManager.count(NO_FILTERING_TOKEN),
                        ZERO, ONE);
    }

    @Operation(summary = "Remove accessibility notification")
    @DeleteMapping("/{id}")
    public AccessibilityResponse deleteAccessibilityNotification(
            @PathVariable String id) {
        Set<String> errors = generalValidator.validateAcc(id);
        if(!errors.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(),
                    accessibilityNotManager.count(NO_FILTERING_TOKEN),
                    ZERO, ONE);
        }
        final List<AccessibilityNotificationDto> isDeleted =
                accessibilityNotManager.delete(id);
        return accessibilityIssueResponseHelper
                .constructResponse(emptySet(), isDeleted, accessibilityNotManager.count(NO_FILTERING_TOKEN),
                        ZERO,
                        ONE);
    }
}
