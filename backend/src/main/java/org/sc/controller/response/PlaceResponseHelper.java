package org.sc.controller.response;

import org.sc.common.rest.PlaceDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.PlaceResponse;
import org.sc.controller.Constants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class PlaceResponseHelper {

    private final ControllerPagination controllerPagination;

    public PlaceResponseHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public PlaceResponse constructResponse(Set<String> errors,
                                              List<PlaceDto> dtos,
                                              long totalCount,
                                              int skip,
                                              int limit) {
        if (!errors.isEmpty()) {
            return new PlaceResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new PlaceResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
