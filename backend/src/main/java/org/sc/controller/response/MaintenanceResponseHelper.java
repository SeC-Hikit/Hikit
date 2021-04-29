package org.sc.controller.response;

import org.sc.common.rest.MaintenanceDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.MaintenanceResponse;
import org.sc.controller.Constants;
import org.sc.controller.ControllerPagination;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class MaintenanceResponseHelper {
    final ControllerPagination controllerPagination;

    public MaintenanceResponseHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public MaintenanceResponse constructResponse(Set<String> errors,
                                                 List<MaintenanceDto> dtos,
                                                 long totalCount,
                                                 int skip,
                                                 int limit) {
        if (!errors.isEmpty()) {
            return new MaintenanceResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new MaintenanceResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
