package org.sc.service

import org.sc.common.rest.AnnouncementDto
import org.sc.manager.AnnouncementManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AnnouncementService @Autowired constructor(private val announcementManager: AnnouncementManager) {

    fun get(id: String) : List<AnnouncementDto> =
        announcementManager.get(id)

    fun get(skip: Int, limit: Int, realm: String) : List<AnnouncementDto> =
        announcementManager.get(skip, limit, realm)

    fun delete(id: String) =
        announcementManager.delete(id)

    fun update(announcement: AnnouncementDto) : List<AnnouncementDto> =
        announcementManager.update(announcement)

    fun create(announcement: AnnouncementDto) : List<AnnouncementDto> =
        announcementManager.create(announcement)
}