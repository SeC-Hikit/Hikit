package org.sc.controller.response;

import org.hikit.common.response.ControllerPagination;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailMappingDto;
import org.sc.common.rest.TrailPreviewDto;
import org.sc.common.rest.response.TrailMappingResponse;
import org.sc.common.rest.response.TrailPreviewResponse;
import org.sc.controller.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TrailPreviewResponseHelper {

    private final ControllerPagination controllerPagination;

    @Autowired
    public TrailPreviewResponseHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public TrailPreviewResponse constructResponse(Set<String> errors,
                                                  List<TrailPreviewDto> dtos,
                                                  long totalCount,
                                                  int skip,
                                                  int limit) {
        if (!errors.isEmpty()) {
            return new TrailPreviewResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailPreviewResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }

    public TrailMappingResponse constructMappingResponse(Set<String> errors,
                                                         List<TrailMappingDto> dtos,
                                                         long totalCount,
                                                         int skip,
                                                         int limit) {
        return new TrailMappingResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
