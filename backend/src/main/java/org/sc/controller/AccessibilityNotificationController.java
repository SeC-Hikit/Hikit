package org.sc.controller;

import org.sc.common.rest.controller.AccessibilityNotification;
import org.sc.common.rest.controller.AccessibilityResponse;
import org.sc.common.rest.controller.RESTResponse;
import org.sc.common.rest.controller.Status;
import org.sc.data.AccessibilityNotificationDAO;
import org.sc.importer.AccessibilityCreationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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
    public RESTResponse getSolved() {
        return new AccessibilityResponse(accessibilityDAO.getSolved());
    }

    @GetMapping("/unsolved")
    public RESTResponse getNotSolved() {
        return new AccessibilityResponse(accessibilityDAO.getNotSolved());
    }

    @GetMapping("/code/{code}")
    public RESTResponse getByTrailCode(@PathVariable String code) {
        return new AccessibilityResponse(accessibilityDAO.getByCode(code));
    }

    @DeleteMapping("/delete/{code}")
    public RESTResponse deleteAccessibilityNotification(@PathVariable String code) {
        boolean isDeleted = accessibilityDAO.delete(code);
        if (isDeleted) {
            return new RESTResponse(Status.OK, Collections.emptySet());
        } else {
            return new RESTResponse(Status.ERROR,
                    new HashSet<>(Collections.singletonList(
                            format("No accessibility notification was found with id '%s'", code))));
        }
    }

    @GetMapping("/solved/{from}/{to}")
    public RESTResponse getSolvedPaged(@PathVariable int from, @PathVariable int to) {
        if(from <= to){
          return new AccessibilityResponse(accessibilityDAO.getSolved(from, to));
        }
        return null;
    }

    @PutMapping(path = "/save",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public RESTResponse createAccessibilityNotification(@RequestBody AccessibilityNotification accessibilityNotification) {
        final Set<String> errors = accessibilityValidator.validate(accessibilityNotification);
        if(errors.isEmpty()) {
            accessibilityDAO.upsert(accessibilityNotification);
            return new RESTResponse();
        }
        return new RESTResponse(errors);
    }

}
