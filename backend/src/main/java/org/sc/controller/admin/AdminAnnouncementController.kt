package org.sc.controller.admin

import io.swagger.v3.oas.annotations.Operation
import org.sc.common.rest.AnnouncementDto
import org.sc.common.rest.response.AnnouncementResponse
import org.sc.controller.admin.Constants.PREFIX_ANNOUNCEMENT
import org.sc.controller.response.AnnouncementResponseHelper
import org.sc.service.AnnouncementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(PREFIX_ANNOUNCEMENT)
class AdminAnnouncementController @Autowired constructor(
    private val announcementService: AnnouncementService,
    private val announcementResponseHelper: AnnouncementResponseHelper
) {

    @PutMapping
    @Operation(summary = "Create an announcement")
    fun create(@RequestBody request: AnnouncementDto): AnnouncementResponse {
        val retrieved = announcementService.create(request)
        return announcementResponseHelper.constructResponse(emptySet(),
            retrieved, retrieved.size.toLong(),0,1)
    }

    @Operation(summary = "Update the provided announcement")
    @PostMapping
    fun update(@RequestBody request: AnnouncementDto): AnnouncementResponse {
        val retrieved = announcementService.update(request)
        return announcementResponseHelper.constructResponse(emptySet(),
            retrieved, retrieved.size.toLong(),0,1)
    }

    @DeleteMapping
    @Operation(summary = "Delete the announcement")
    fun delete(id: String) = announcementService.delete(id)
}