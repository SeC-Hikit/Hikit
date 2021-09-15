package org.sc.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.controller.response.AccessibilityReportResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.AccessibilityReportManager;
import org.sc.service.AccessibilityReportService;
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
    private final AccessibilityReportService reportService;

    @Autowired
    public AdminAccessibilityReportController(final AccessibilityReportService accService,
                                              final AccessibilityReportResponseHelper accessibilityIssueResponseHelper,
                                              final GeneralValidator generalValidator) {
        this.reportService = accService;
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
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(), reportService.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return accessibilityIssueResponseHelper
                .constructResponse(errors, reportService.create(accReport),
                        reportService.count(),
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
            return accessibilityIssueResponseHelper.constructResponse(updateError, emptyList(), reportService.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return accessibilityIssueResponseHelper
                .constructResponse(updateError, reportService.update(accReport),
                        reportService.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Remove accessibility reports")
    @DeleteMapping("/{id}")
    public AccessibilityReportResponse deleteAccessibilityNotificationReport(
            @PathVariable String id) {
        final Set<String> errors = generalValidator.validateReportAcc(id);
        if (!errors.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(), reportService.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }

        final List<AccessibilityReportDto> isDeleted =
                reportService.delete(id);
        return accessibilityIssueResponseHelper
                .constructResponse(emptySet(), isDeleted, reportService.count(),
                        org.sc.controller.Constants.ZERO,
                        org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Upgrade a report")
    @PutMapping("/upgrade/{id}")
    public AccessibilityReportResponse upgradeReport(
            @PathVariable String id) {
        final Set<String> errors = generalValidator.validateReportAcc(id);
        if (!errors.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(), reportService.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }

        final List<AccessibilityReportDto> upgraded =
                reportService.upgrade(id);
        return accessibilityIssueResponseHelper
                .constructResponse(emptySet(), upgraded, reportService.count(),
                        org.sc.controller.Constants.ZERO,
                        org.sc.controller.Constants.ONE);
    }

}
