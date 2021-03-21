package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.common.rest.response.CountResponse;
import org.sc.data.validator.AccessibilityValidator;
import org.sc.manager.AccessibilityNotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.*;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(AccessibilityNotificationController.PREFIX)
public class AccessibilityNotificationController {

    public final static String PREFIX = "/accessibility";

    private final AccessibilityValidator accessibilityValidator;
    private final AccessibilityNotificationManager accessibilityNotManager;
    private final ControllerPagination controllerPagination;

    @Autowired
    public AccessibilityNotificationController(final AccessibilityNotificationManager accessibilityNotificationManager,
                                               final AccessibilityValidator accessibilityValidator,
                                               final ControllerPagination controllerPagination) {
        this.accessibilityNotManager = accessibilityNotificationManager;
        this.accessibilityValidator = accessibilityValidator;
        this.controllerPagination = controllerPagination;
    }

    @Operation(summary = "Count all accessibility notifications in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = accessibilityNotManager.count();
        return new CountResponse(Status.OK, emptySet(), new CountDto(count));
    }

    @Operation(summary = "Retrieve solved notifications")
    @GetMapping("/solved")
    public AccessibilityResponse getSolved(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(),
                accessibilityNotManager.getSolved(skip, limit),
                accessibilityNotManager.count(), skip, limit);
    }

    @Operation(summary = "Retrieve solved notifications by trail ID")
    @GetMapping("/solved/{trailId}")
    public AccessibilityResponse getSolvedByTrailId(
            @PathVariable String trailId,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        List<AccessibilityNotificationDto> resolvedById = accessibilityNotManager.getResolvedById(trailId, skip, limit);
        return constructResponse(emptySet(), resolvedById,
                accessibilityNotManager.countSolvedForTrailId(trailId), skip, limit);
    }

    @Operation(summary = "Retrieve unresolved notifications")
    @GetMapping("/unresolved")
    public AccessibilityResponse getNotSolved(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(),
                accessibilityNotManager.getUnresolved(skip, limit),
                accessibilityNotManager.countNotSolved(), skip, limit);
    }

    @Operation(summary = "Retrieve unresolved notifications by trail ID")
    @GetMapping("/unresolved/{trailId}")
    public AccessibilityResponse getNotSolvedByTrailId(
            @PathVariable String trailId,
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int skip,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int limit) {
        return constructResponse(emptySet(),
                accessibilityNotManager.getUnresolvedById(trailId, skip, limit),
                accessibilityNotManager.countNotSolvedForTrailId(trailId), skip, limit);
    }

    @Operation(summary = "Resolve accessibility notification")
    @PostMapping("/resolve")
    public AccessibilityResponse resolveNotification(
            @RequestBody AccessibilityNotificationResolutionDto accessibilityRes) {
        final List<AccessibilityNotificationDto> resolved =
                accessibilityNotManager.resolve(accessibilityRes);
        if (resolved.isEmpty()) {
            constructResponse(
                    singleton(format("No accessibility notification was found with id '%s'",
                            accessibilityRes.getId())), emptyList(), accessibilityNotManager.count(),
                    Constants.ZERO, Constants.ONE);
        }
        return constructResponse(emptySet(), resolved, accessibilityNotManager.count(),
                Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Add accessibility notification")
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AccessibilityResponse createAccessibilityNotification(
            @RequestBody AccessibilityNotificationCreationDto accessibilityNotificationCreation) {
        final Set<String> errors = accessibilityValidator.validate(accessibilityNotificationCreation);
        if (!errors.isEmpty()) {
            return constructResponse(errors, emptyList(), accessibilityNotManager.count(),
                    Constants.ZERO, Constants.ONE);
        }
        return constructResponse(errors, accessibilityNotManager.upsert(accessibilityNotificationCreation),
                accessibilityNotManager.count(),
                Constants.ZERO, Constants.ONE);
    }

    @Operation(summary = "Remove accessibility notification")
    @DeleteMapping("/{id}")
    public AccessibilityResponse deleteAccessibilityNotification(
            @PathVariable String id) {
        final List<AccessibilityNotificationDto> isDeleted =
                accessibilityNotManager.delete(id);
        return constructResponse(emptySet(), isDeleted, accessibilityNotManager.count(),
                Constants.ZERO,
                Constants.ONE);
    }

    private AccessibilityResponse constructResponse(Set<String> errors,
                                                    List<AccessibilityNotificationDto> dtos,
                                                    long totalCount,
                                                    int skip,
                                                    int limit) {
        if (!errors.isEmpty()) {
            return new AccessibilityResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new AccessibilityResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
