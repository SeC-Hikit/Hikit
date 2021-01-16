package org.sc.controller;

import org.sc.common.rest.*;
import org.sc.configuration.AppProperties;
import org.sc.data.repository.AccessibilityNotificationDAO;
import org.sc.data.validator.AccessibilityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ;
import static org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ;

@RestController
@RequestMapping(AccessibilityNotificationController.PREFIX)
public class AccessibilityNotificationController {

    public final static String PREFIX = "/accessibility";

    private final AccessibilityValidator accessibilityValidator;
    private final AccessibilityNotificationDAO accessibilityDAO;

    @Autowired
    public AccessibilityNotificationController(final AccessibilityNotificationDAO accessibilityDao,
                                               final AccessibilityValidator accessibilityValidator,
                                               final AppProperties appProperties) {
        this.accessibilityDAO = accessibilityDao;
        this.accessibilityValidator = accessibilityValidator;
    }

    @GetMapping("/solved")
    public AccessibilityResponse getSolved(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                           @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new AccessibilityResponse(accessibilityDAO.getSolved(page, count));
    }

    @GetMapping("/solved/{code}")
    public AccessibilityResponse getSolved(@PathVariable String code) {
        return new AccessibilityResponse(accessibilityDAO.getResolvedByCode(code));
    }

    @GetMapping("/unresolved")
    public AccessibilityUnresolvedResponse getNotSolved(@RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) int page,
                                                        @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) int count) {
        return new AccessibilityUnresolvedResponse(accessibilityDAO.getNotSolved(page, count));
    }

    @GetMapping("/unresolved/{code}")
    public AccessibilityUnresolvedResponse getNotSolvedByTrailCode(@PathVariable String code) {
        return new AccessibilityUnresolvedResponse(accessibilityDAO.getUnresolvedByCode(code));
    }

    @PostMapping("/resolve")
    public RESTResponse resolveNotification(@RequestBody AccessibilityNotificationResolution accessibilityRes) {
        boolean hasBeenResolved = accessibilityDAO.resolve(accessibilityRes);
        if (hasBeenResolved) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        }
        return new RESTResponse(Status.ERROR,
                new HashSet<>(Collections.singletonList(
                        format("No accessibility notification was found with id '%s'", accessibilityRes.get_id()))));

    }

    @DeleteMapping("/{objectId}")
    public RESTResponse deleteAccessibilityNotification(@PathVariable String objectId) {
        boolean isDeleted = accessibilityDAO.delete(objectId);
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No accessibility notification was found with id '%s'", objectId))));
        }
    }

    @PutMapping(path = "",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public RESTResponse createAccessibilityNotification(@RequestBody AccessibilityNotificationCreation accessibilityNotificationCreation) {
        final Set<String> errors = accessibilityValidator.validate(accessibilityNotificationCreation);
        if (!errors.isEmpty()) {
            return new RESTResponse(errors);
        }
        if (accessibilityDAO.upsert(accessibilityNotificationCreation)) {
            return new RESTResponse();
        }
        throw new IllegalStateException();
    }

}
