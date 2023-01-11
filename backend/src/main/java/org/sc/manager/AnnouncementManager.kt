package org.sc.manager

import org.sc.common.rest.AnnouncementDto
import org.sc.data.mapper.AnnouncementMapper
import org.sc.data.repository.AnnouncementDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AnnouncementManager @Autowired constructor(
    private val announcementDao: AnnouncementDao,
    private val announcementMapper: AnnouncementMapper
) {
    fun get(id: String) : List<AnnouncementDto> =
        announcementDao.get(id).map { announcementMapper.map(it) }

    fun get(skip: Int, limit: Int, realm: String) : List<AnnouncementDto> =
        announcementDao.get(skip, limit, realm).map { announcementMapper.map(it) }

    fun delete(id: String) =
        announcementDao.delete(id)

    fun update(announcement: AnnouncementDto) : List<AnnouncementDto> =
        announcementDao.update(announcementMapper.map(announcement)).map { announcementMapper.map(it) }

    fun create(announcement: AnnouncementDto) : List<AnnouncementDto> =
        announcementDao.create(announcementMapper.map(announcement)).map { announcementMapper.map(it) }

}