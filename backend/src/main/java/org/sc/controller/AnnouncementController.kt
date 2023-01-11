package org.sc.controller

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.response.AnnouncementResponse
import org.sc.configuration.AppBoundaries.MAX_DOCS_ON_READ
import org.sc.configuration.AppBoundaries.MIN_DOCS_ON_READ
import org.sc.controller.AnnouncementController.Companion.PREFIX
import org.sc.controller.admin.Constants
import org.sc.controller.response.AnnouncementResponseHelper
import org.sc.data.repository.MongoUtils.NO_FILTERING_TOKEN
import org.sc.service.AnnouncementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(PREFIX)
class AnnouncementController @Autowired constructor(
    private val announcementService: AnnouncementService,
    private val announcementResponseHelper: AnnouncementResponseHelper
) {
    companion object {
        const val PREFIX = "/announcement"
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get announcement by id")
    fun get(@PathVariable id: String): AnnouncementResponse {
        val retrieved = announcementService.get(id)
        return announcementResponseHelper.constructResponse(
            emptySet(),
            retrieved, retrieved.size.toLong(), 0, 1
        )
    }

    @GetMapping
    @Operation(summary = "Get announcements")
    fun get(
        @RequestParam(required = false, defaultValue = MIN_DOCS_ON_READ) skip: Int,
        @RequestParam(required = false, defaultValue = MAX_DOCS_ON_READ) limit: Int,
        @RequestParam(required = false, defaultValue = NO_FILTERING_TOKEN) realm: String
    ): AnnouncementResponse {
        val retrieved = announcementService.get(skip, limit, realm)
        return announcementResponseHelper.constructResponse(
            emptySet(),
            retrieved, retrieved.size.toLong(), skip, limit
        )
    }
}