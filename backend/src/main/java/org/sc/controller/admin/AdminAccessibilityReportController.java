package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.controller.response.AccessibilityReportResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.AccessibilityReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.sc.controller.admin.Constants.PREFIX_REPORT;

@RestController
@RequestMapping(PREFIX_REPORT)
public class AdminAccessibilityReportController {

    private final AccessibilityReportResponseHelper accessibilityIssueResponseHelper;
    private final GeneralValidator generalValidator;
    private final AccessibilityReportManager accessibilityNotManager;

    @Autowired
    public AdminAccessibilityReportController(final AccessibilityReportManager accessibilityNotificationManager,
                                              final AccessibilityReportResponseHelper accessibilityIssueResponseHelper,
                                              final GeneralValidator generalValidator) {
        this.accessibilityNotManager = accessibilityNotificationManager;
        this.accessibilityIssueResponseHelper = accessibilityIssueResponseHelper;
        this.generalValidator = generalValidator;
    }

    @Operation(summary = "Creates a report")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AccessibilityReportResponse create(
            @RequestBody AccessibilityReportDto accReport) {
        final Set<String> errors = generalValidator.validate(accReport);
        if (!errors.isEmpty() || accReport.getId() != null) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(), accessibilityNotManager.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return accessibilityIssueResponseHelper
                .constructResponse(errors, accessibilityNotManager.save(accReport),
                        accessibilityNotManager.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Updates a report")
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AccessibilityReportResponse update(
            @RequestBody AccessibilityReportDto accReport) {
        final Set<String> errors = generalValidator.validate(accReport);
        final Set<String> updateError = generalValidator.validateReportAcc(accReport.getId());
        if (!updateError.isEmpty() || !errors.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(updateError, emptyList(), accessibilityNotManager.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return accessibilityIssueResponseHelper
                .constructResponse(updateError, accessibilityNotManager.save(accReport),
                        accessibilityNotManager.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }


    @Operation(summary = "Remove accessibility reports")
    @DeleteMapping("/{id}")
    public AccessibilityReportResponse deleteAccessibilityNotification(
            @PathVariable String id) {
        Set<String> errors = generalValidator.validateReportAcc(id);
        if (!errors.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(), accessibilityNotManager.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }

        final List<AccessibilityReportDto> isDeleted =
                accessibilityNotManager.delete(id);
        return accessibilityIssueResponseHelper
                .constructResponse(emptySet(), isDeleted, accessibilityNotManager.count(),
                        org.sc.controller.Constants.ZERO,
                        org.sc.controller.Constants.ONE);
    }

}
