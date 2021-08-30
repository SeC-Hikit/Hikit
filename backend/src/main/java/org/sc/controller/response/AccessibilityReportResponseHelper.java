package org.sc.controller.response;

import org.sc.common.rest.AccessibilityReportDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.AccessibilityReportResponse;
import org.sc.controller.Constants;
import org.sc.controller.ControllerPagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AccessibilityReportResponseHelper {

    final ControllerPagination controllerPagination;

    @Autowired
    public AccessibilityReportResponseHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public AccessibilityReportResponse constructResponse(final Set<String> errors,
                                                         final List<AccessibilityReportDto> dtos,
                                                         final long totalCount,
                                                         final int skip,
                                                         final int limit) {
        if (!errors.isEmpty()) {
            return new AccessibilityReportResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new AccessibilityReportResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
