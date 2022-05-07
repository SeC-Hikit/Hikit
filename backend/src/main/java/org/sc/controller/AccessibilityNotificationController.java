package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.common.rest.response.CountResponse;
import org.sc.controller.response.AccessibilityIssueResponseHelper;
import org.sc.manager.AccessibilityNotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.*;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;
import static org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN;

@RestController
@RequestMapping(AccessibilityNotificationController.PREFIX)
public class AccessibilityNotificationController {

    public final static String PREFIX = "/accessibility";

    private final AccessibilityIssueResponseHelper accessibilityIssueResponseHelper;
    private final AccessibilityNotificationManager accessibilityNotManager;
    private final ControllerPagination controllerPagination;

    @Autowired
    public AccessibilityNotificationController(final AccessibilityNotificationManager accessibilityNotificationManager,
                                               final AccessibilityIssueResponseHelper accessibilityIssueResponseHelper,
                                               final ControllerPagination controllerPagination) {
        this.accessibilityNotManager = accessibilityNotificationManager;
        this.accessibilityIssueResponseHelper = accessibilityIssueResponseHelper;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Count all accessibility notifications in DB")
    @GetMapping("/count")
    public CountResponse getCount(
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        final long count = accessibilityNotManager.count(realm);
        return new CountResponse(Status.OK, emptySet(), new CountDto(count));
    }

    @Operation(summary = "Retrieve notification by id")
    @GetMapping("/{id}")
    public AccessibilityResponse getById(
            @PathVariable String id) {
        List<AccessibilityNotificationDto> dtos = accessibilityNotManager.byId(id);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                dtos, dtos.size(), 0, 1);
    }

    @Operation(summary = "Retrieve solved notifications")
    @GetMapping("/solved")
    public AccessibilityResponse getSolved(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessibilityNotManager.getSolved(skip, limit, realm),
                accessibilityNotManager.count(realm), skip, limit);
    }

    @Operation(summary = "Retrieve solved notifications by trail ID")
    @GetMapping("/solved/{trailId}")
    public AccessibilityResponse getSolvedByTrailId(
            @PathVariable String trailId,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        final List<AccessibilityNotificationDto> resolvedById =
                accessibilityNotManager.getResolvedByTrailId(trailId, skip, limit, realm);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(), resolvedById,
                accessibilityNotManager.countSolvedForTrailId(realm), skip, limit);
    }

    @Operation(summary = "Retrieve unresolved notifications")
    @GetMapping("/unresolved")
    public AccessibilityResponse getNotSolved(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit,
            @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) String realm) {
        controllerPagination.checkSkipLim(skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessibilityNotManager.getUnresolved(skip, limit, realm),
                accessibilityNotManager.countNotSolved(realm), skip, limit);
    }

    @Operation(summary = "Retrieve unresolved notifications by trail ID")
    @GetMapping("/unresolved/{trailId}")
    public AccessibilityResponse getNotSolvedByTrailId(
            @PathVariable String trailId,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        controllerPagination.checkSkipLim(skip, limit);
        return accessibilityIssueResponseHelper.constructResponse(emptySet(),
                accessibilityNotManager.getUnresolvedByTrailId(trailId, skip, limit),
                accessibilityNotManager.countNotSolvedForTrailId(trailId), skip, limit);
    }
}
