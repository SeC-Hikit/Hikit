package org.sc.manager

import org.sc.common.rest.TrailRawDto
import org.sc.data.mapper.TrailRawMapper
import org.sc.data.repository.TrailRawDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailRawManager @Autowired constructor(
        private val trailRawDAO: TrailRawDAO,
        private val trailRawMapper: TrailRawMapper,
        private val fileManager: TrailFileManager
) {
    fun get(skip: Int, limit: Int, realm: String): List<TrailRawDto> =
            trailRawDAO.get(skip, limit, realm).map { trailRawMapper.map(it) }

    fun getById(id: String): List<TrailRawDto> =
            trailRawDAO.getById(id).map { trailRawMapper.map(it) }

    fun deleteById(id: String): List<TrailRawDto> {
        val map = trailRawDAO.deleteById(id).map { trailRawMapper.map(it) }
        map.forEach { fileManager.deleteRawTrail(it.fileDetails.filename) }
        return map;
    }

    fun count(realm: String): Long =
            trailRawDAO.count(realm)
}