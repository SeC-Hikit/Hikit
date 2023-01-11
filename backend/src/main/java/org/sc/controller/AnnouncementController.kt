package org.sc.controller

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.response.AnnouncementResponse
import org.sc.controller.admin.Constants
import org.sc.controller.response.AnnouncementResponseHelper
import org.sc.service.AnnouncementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Constants.PREFIX_ANNOUNCEMENT)
class AnnouncementController @Autowired constructor(
    private val announcementService: AnnouncementService,
    private val announcementResponseHelper: AnnouncementResponseHelper
) {
    companion object {
        const val PREFIX = "/announcement"
    }

    @GetMapping
    @Operation(summary = "Get announcement by id")
    fun get(id: String): AnnouncementResponse {
        val retrieved = announcementService.get(id)
        return announcementResponseHelper.constructResponse(
            emptySet(),
            retrieved, retrieved.size.toLong(), 0, 1
        )
    }

    @Operation(summary = "Get announcements")
    @GetMapping
    fun get(skip: Int, limit: Int, realm: String): AnnouncementResponse {
        val retrieved = announcementService.get(skip, limit, realm)
        return announcementResponseHelper.constructResponse(
            emptySet(),
            retrieved, retrieved.size.toLong(), skip, limit
        )
    }
}