package org.sc.controller.response;

import org.hikit.common.response.ControllerPagination;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailDto;
import org.sc.common.rest.response.TrailResponse;
import org.sc.controller.Constants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TrailResponseHelper {

    private final ControllerPagination controllerPagination;

    public TrailResponseHelper(final ControllerPagination controllerPagination){
        this.controllerPagination = controllerPagination;
    }

    public TrailResponse constructResponse(Set<String> errors,
                                           List<TrailDto> trailDtos,
                                           long totalCount,
                                           int skip,
                                           int limit) {
        if (!errors.isEmpty()) {
            return new TrailResponse(Status.ERROR, errors, trailDtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailResponse(Status.OK, errors, trailDtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
