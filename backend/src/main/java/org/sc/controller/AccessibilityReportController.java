package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.common.rest.CountDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.common.rest.response.CountResponse;
import org.sc.controller.response.AccessibilityReportResponseHelper;
import org.sc.data.validator.GeneralValidator;
import org.sc.manager.AccessibilityReportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Collections.emptySet;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.controller.Constants.ONE;
import static org.sc.controller.Constants.ZERO;

@RestController
@RequestMapping(AccessibilityReportController.PREFIX)
public class AccessibilityReportController {

    public final static String PREFIX = "/report";

    private final AccessibilityReportResponseHelper accessibilityIssueResponseHelper;
    private final AccessibilityReportManager accessibilityNotManager;

    @Autowired
    public AccessibilityReportController(final AccessibilityReportManager accessibilityNotificationManager,
                                         final AccessibilityReportResponseHelper accessibilityIssueResponseHelper) {
        this.accessibilityNotManager = accessibilityNotificationManager;
        this.accessibilityIssueResponseHelper = accessibilityIssueResponseHelper;
    }

    @Operation(summary = "Count all accessibility reports for realm")
    @GetMapping("/count/{realm}")
    public CountResponse getCount(final String realm) {
        final long count = accessibilityNotManager.count(realm);
        return new CountResponse(Status.OK, emptySet(), new CountDto(count));
    }

    @Operation(summary = "Get report by id")
    @GetMapping("/{id}")
    public AccessibilityReportResponse getById(final String id) {
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessibilityNotManager.byId(id),
                ONE, ZERO, ONE);
    }

    @Operation(summary = "Get reports by trail ID")
    @GetMapping("/trail/{id}")
    public AccessibilityReportResponse getByByTrailId(final String id,
                                                      @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
                                                      @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        final List<AccessibilityReportDto> upgradedByTrailId = accessibilityNotManager.getByTrailId(id, skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                upgradedByTrailId, accessibilityNotManager.countByTrailId(id)
                , skip, limit);
    }

    @Operation(summary = "Retrieve upgraded reports")
    @GetMapping("/upgraded/{realm}")
    public AccessibilityReportResponse getUpgraded(
            final String realm,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessibilityNotManager.getUpgradedByRealm(realm, skip, limit),
                accessibilityNotManager.countUpgraded(realm), skip, limit);
    }

    @Operation(summary = "Retrieve not-upgraded reports")
    @GetMapping("/not-upgraded/{realm}")
    public AccessibilityReportResponse getUnapgraded(
            final String realm,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessibilityNotManager.getUnapgradedByRealm(realm, skip, limit),
                accessibilityNotManager.countUnapgraded(realm), skip, limit);
    }

}
