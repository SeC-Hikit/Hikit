package org.sc.controller;

import org.sc.common.rest.controller.*;
import org.sc.data.AccessibilityNotificationDAO;
import org.sc.importer.AccessibilityCreationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

@RestController
@RequestMapping(AccessibilityNotificationController.PREFIX)
public class AccessibilityNotificationController {

    public final static String PREFIX =  "/accessibility";

    private final AccessibilityCreationValidator accessibilityValidator;
    private final AccessibilityNotificationDAO accessibilityDAO;


    @Autowired
    public AccessibilityNotificationController(final AccessibilityNotificationDAO accessibilityDao,
                                               final AccessibilityCreationValidator accessibilityValidator) {
        this.accessibilityDAO = accessibilityDao;
        this.accessibilityValidator = accessibilityValidator;
    }

    @GetMapping("/solved")
    public AccessibilityResponse getSolved() {
        return new AccessibilityResponse(accessibilityDAO.getSolved());
    }

    @GetMapping("/solved/{code}")
    public AccessibilityResponse getSolved(@PathVariable String code) {
        return new AccessibilityResponse(accessibilityDAO.getResolvedByCode(code));
    }

    @GetMapping("/unresolved")
    public AccessibilityUnresolvedResponse getNotSolved() {
        return new AccessibilityUnresolvedResponse(accessibilityDAO.getNotSolved());
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

    @PutMapping(path = "/save",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public RESTResponse createAccessibilityNotification(@RequestBody AccessibilityNotificationCreation accessibilityNotificationCreation) {
        final Set<String> errors = accessibilityValidator.validate(accessibilityNotificationCreation);
        if(!errors.isEmpty()) {
            return new RESTResponse(errors);
        }
        if(accessibilityDAO.upsert(accessibilityNotificationCreation)) {
            return new RESTResponse();
        }
        throw new IllegalStateException();
    }

}
