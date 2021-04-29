package org.sc.controller.response;

import org.sc.common.rest.MediaDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.MediaResponse;
import org.sc.controller.Constants;
import org.sc.controller.ControllerPagination;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class MediaResponseHelper {

    final ControllerPagination controllerPagination;

    public MediaResponseHelper(ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public MediaResponse constructResponse(Set<String> errors,
                                           List<MediaDto> dtos,
                                           long totalCount,
                                           int skip,
                                           int limit) {
        if (!errors.isEmpty()) {
            return new MediaResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new MediaResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
