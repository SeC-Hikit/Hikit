package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.hikit.common.response.ControllerPagination;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.SortOrderDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.common.rest.response.CountResponse;
import org.hikit.common.response.ControllerPagination;
import org.sc.controller.response.AccessibilityReportResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.service.AccessibilityReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.util.Collections.*;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.controller.Constants.ONE;
import static org.sc.controller.Constants.ZERO;

@RestController
@RequestMapping(AccessibilityReportController.PREFIX)
public class AccessibilityReportController {

    public final static String PREFIX = "/report";

    private final AccessibilityReportResponseHelper accessibilityIssueResponseHelper;
    private final GeneralValidator generalValidator;
    private final AccessibilityReportService accessService;
    private final ControllerPagination controllerPagination;

    @Autowired
    public AccessibilityReportController(final AccessibilityReportService accessibilityNotificationManager,
                                         final AccessibilityReportResponseHelper accessibilityIssueResponseHelper,
                                         final GeneralValidator generalValidator,
                                         final ControllerPagination controllerPagination) {
        this.accessService = accessibilityNotificationManager;
        this.accessibilityIssueResponseHelper = accessibilityIssueResponseHelper;
        this.generalValidator = generalValidator;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Count all accessibility reports for realm")
    @GetMapping("/count/{realm}")
    public CountResponse getCount(
            @PathVariable final String realm) {
        final long count = accessService.count(realm);
        return new CountResponse(Status.OK, emptySet(), new CountDto(count));
    }

    @Operation(summary = "Get report by id")
    @GetMapping("/{id}")
    public AccessibilityReportResponse getById(
            @PathVariable final String id) {
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessService.byId(id),
                ONE, ZERO, ONE);
    }

    @Operation(summary = "Creates a report")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AccessibilityReportResponse create(
            @RequestBody AccessibilityReportDto accReport) {
        final Set<String> errors = generalValidator.validate(accReport);
        if (!errors.isEmpty() || accReport.getId() != null) {
            return accessibilityIssueResponseHelper.constructResponse(errors, emptyList(), accessService.count(),
                    org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
        }
        return accessibilityIssueResponseHelper
                .constructResponse(errors, accessService.create(accReport),
                        accessService.count(),
                        org.sc.controller.Constants.ZERO, org.sc.controller.Constants.ONE);
    }

    @Operation(summary = "Get reports by trail ID")
    @GetMapping("/trail/{id}")
    public AccessibilityReportResponse getByTrailId(
            @PathVariable final String id,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        final List<AccessibilityReportDto> upgradedByTrailId = accessService.getByTrailId(id, skip, limit);
        controllerPagination.checkSkipLim(skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                upgradedByTrailId, accessService.countByTrailId(id), skip, limit);
    }

    @Operation(summary = "Retrieve upgraded reports")
    @GetMapping("/upgraded/{realm}")
    public AccessibilityReportResponse getUpgraded(
            @PathVariable final String realm,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessService.getUpgradedByRealm(realm, skip, limit),
                accessService.countUpgraded(realm), skip, limit);
    }

    @Operation(summary = "Validate newly created reports")
    @PutMapping("/validate/{validationId}")
    public AccessibilityReportResponse validate(
            @PathVariable String validationId) {
        List<AccessibilityReportDto> values = accessService.validate(validationId);
        if(values.isEmpty()) {
            return accessibilityIssueResponseHelper.constructResponse(
                    singleton("No report found to validate"),
                    values, 0, 0, 1);

        }
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                values, values.size(), 0, values.size());
    }

    @Operation(summary = "Retrieve not-upgraded reports")
    @GetMapping("/not-upgraded/{realm}")
    public AccessibilityReportResponse getUnapgraded(
            @PathVariable final String realm,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessService.getUnapgradedByRealm(realm, skip, limit),
                accessService.countUnapgraded(realm), skip, limit);
    }


}
