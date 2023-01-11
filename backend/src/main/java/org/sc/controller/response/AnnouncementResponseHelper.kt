package org.sc.controller.response

import org.sc.common.rest.AnnouncementDto
import org.sc.common.rest.Status
import org.sc.common.rest.response.AnnouncementResponse
import org.sc.controller.Constants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AnnouncementResponseHelper @Autowired constructor(private val controllerPagination : ControllerPagination) {

    fun constructResponse(
        errors: Set<String>,
        dtos: List<AnnouncementDto>,
        totalCount: Long,
        skip: Int,
        limit: Int
    ): AnnouncementResponse {
        return if (errors.isNotEmpty()) {
            AnnouncementResponse(
                Status.ERROR, errors, dtos, 1L,
                Constants.ONE.toLong(), limit.toLong(), totalCount
            )
        } else AnnouncementResponse(
            Status.OK, errors, dtos,
            controllerPagination.getCurrentPage(skip, limit),
            controllerPagination.getTotalPages(totalCount, limit), limit.toLong(), totalCount
        )
    }

}