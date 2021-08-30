package org.sc.controller.response;

import org.sc.common.rest.AccessibilityNotificationDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.AccessibilityResponse;
import org.sc.controller.Constants;
import org.sc.controller.ControllerPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AccessibilityIssueResponseHelper {

    final ControllerPagination controllerPagination;

    @Autowired
    public AccessibilityIssueResponseHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public AccessibilityResponse constructResponse(final Set<String> errors,
                                                   final List<AccessibilityNotificationDto> dtos,
                                                   final long totalCount,
                                                   final int skip,
                                                   final int limit) {
        if (!errors.isEmpty()) {
            return new AccessibilityResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new AccessibilityResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
