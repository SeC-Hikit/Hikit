package org.sc.controller.response;

import org.hikit.common.response.ControllerPagination;
import org.sc.common.rest.PoiDto;
import org.sc.common.rest.Status;
import org.sc.common.rest.response.PoiResponse;
import org.sc.controller.Constants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class PoiResponseHelper {

    final ControllerPagination controllerPagination;

    public PoiResponseHelper(ControllerPagination controllerPagination) {
        this.controllerPagination = controllerPagination;
    }

    public PoiResponse constructResponse(Set<String> errors,
                                          List<PoiDto> dtos,
                                          long totalCount,
                                          int skip,
                                          int limit) {
        if (!errors.isEmpty()) {
            return new PoiResponse(Status.ERROR, errors, dtos, 1L,
                    Constants.ONE, limit, totalCount);
        }
        return new PoiResponse(Status.OK, errors, dtos,
                controllerPagination.getCurrentPage(skip, limit),
                controllerPagination.getTotalPages(totalCount, limit), limit, totalCount);
    }

}

