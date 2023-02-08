package org.sc.controller.response;

import org.hikit.common.response.ControllerPagination;
import org.sc.common.rest.Status;
import org.sc.common.rest.TrailRawDto;
import org.sc.common.rest.response.TrailRawResponse;
import org.sc.controller.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TrailRawResponseHelper {

    final ControllerPagination controllerPagination;

    @Autowired
    public TrailRawResponseHelper(final ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public TrailRawResponse constructResponse(Set<String> errors,
                                               List<TrailRawDto> dtos,
                                               long totalCount,
                                               int skip,
                                               int limit) {
        if (!errors.isEmpty()) {
            return new TrailRawResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new TrailRawResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }
}
