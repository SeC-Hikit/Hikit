package org.sc.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.sc.common.rest.*;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.common.rest.response.AccessibilityUnresolvedResponse;
import org.sc.common.rest.response.CountResponse;
import org.sc.data.validator.AccessibilityValidator;
import org.sc.manager.AccessibilityNotificationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(AccessibilityNotificationController.PREFIX)
public class AccessibilityNotificationController {

    public final static String PREFIX = "/accessibility";

    private final AccessibilityValidator accessibilityValidator;
    private final AccessibilityNotificationManager accessibilityNotManager;

    @Autowired
    public AccessibilityNotificationController(final AccessibilityNotificationManager accessibilityNotificationManager,
                                               final AccessibilityValidator accessibilityValidator) {
        this.accessibilityNotManager = accessibilityNotificationManager;
        this.accessibilityValidator = accessibilityValidator;
    }

    @Operation(summary = "Count all accessibility notifications in DB")
    @GetMapping("/count")
    public CountResponse getCount() {
        final long count = accessibilityNotManager.countAccessibilityNotification();
        return new CountResponse(Status.OK, Collections.emptySet(), new CountDto(count));
    }

    @Operation(summary = "Retrieve solved notifications")
    @GetMapping("/solved")
    public AccessibilityResponse getSolved(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new AccessibilityResponse(Status.OK,
                Collections.emptySet(),
                accessibilityNotManager.getSolved(page, count));
    }

    @Operation(summary = "Retrieve solved notifications by trail ID")
    @GetMapping("/solved/{code}")
    public AccessibilityResponse getSolvedByTrailId(
            @PathVariable String code) {
        return new AccessibilityResponse(Status.OK,
                Collections.emptySet(),
                accessibilityNotManager.getResolvedById(code));
    }

    @Operation(summary = "Retrieve unresolved notifications")
    @GetMapping("/unresolved")
    public AccessibilityUnresolvedResponse getNotSolved(
            @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
            @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new AccessibilityUnresolvedResponse(Status.OK,
                Collections.emptySet(),
                accessibilityNotManager.getUnresolved(page, count));
    }

    @Operation(summary = "Retrieve unresolved notifications by trail ID")
    @GetMapping("/unresolved/{id}")
    public AccessibilityUnresolvedResponse getNotSolvedByTrailId(
            @PathVariable String id) {
        return new AccessibilityUnresolvedResponse(Status.OK, Collections.emptySet(),
                accessibilityNotManager.getUnresolvedById(id));
    }

    @Operation(summary = "Resolve accessibility notification")
    @PostMapping("/resolve")
    public AccessibilityResponse resolveNotification(
            @RequestBody AccessibilityNotificationResolutionDto accessibilityRes) {
        final List<AccessibilityNotificationDto> resolved =
                accessibilityNotManager.resolve(accessibilityRes);
        if (resolved.isEmpty()) {
            return new AccessibilityResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No accessibility notification was found with id '%s'", accessibilityRes.getId()))),
                    Collections.emptyList());
        }
        return new AccessibilityResponse(Status.OK, Collections.emptySet(), resolved);
    }

    @Operation(summary = "Add accessibility notification")
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AccessibilityUnresolvedResponse createAccessibilityNotification(
            @RequestBody AccessibilityNotificationCreationDto accessibilityNotificationCreation) {
        final Set<String> errors = accessibilityValidator.validate(accessibilityNotificationCreation);
        if (!errors.isEmpty()) {
            return new AccessibilityUnresolvedResponse(Status.ERROR,
                    errors, Collections.emptyList());
        }
        final List<AccessibilityUnresolvedDto> upserted =
                accessibilityNotManager.upsert(accessibilityNotificationCreation);
        if (!upserted.isEmpty()) {
            return new AccessibilityUnresolvedResponse(Status.OK, Collections.emptySet(), upserted);
        }
        throw new IllegalStateException();
    }

    @Operation(summary = "Remove accessibility notification")
    @DeleteMapping("/{id}")
    public AccessibilityResponse deleteAccessibilityNotification(
            @PathVariable String id) {
        final List<AccessibilityNotificationDto> isDeleted =
                accessibilityNotManager.delete(id);
        if (isDeleted.isEmpty()) {
            return new AccessibilityResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No accessibility notification was found with id '%s'", id))),
                    Collections.emptyList());
        }
        return new AccessibilityResponse(Status.OK, Collections.emptySet(), isDeleted);
    }
}
