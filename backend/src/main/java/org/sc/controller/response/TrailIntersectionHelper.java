package org.sc.controller.response;

import org.sc.common.rest.Status;
import org.sc.common.rest.TrailIntersectionDto;
import org.sc.common.rest.response.TrailIntersectionResponse;
import org.sc.controller.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TrailIntersectionHelper {

    private final ControllerPagination controllerPagination;

    @Autowired
    public TrailIntersectionHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public TrailIntersectionResponse constructResponse(final Set<String> errors,
                                                             final List<TrailIntersectionDto> dtos,
                                                             final long totalCount,
                                                             final int skip,
                                                             final int limit) {
        if (!errors.isEmpty()) {
            return new TrailIntersectionResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailIntersectionResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
