package org.sc.manager

import org.sc.common.rest.TrailRawDto
import org.sc.data.mapper.TrailRawMapper
import org.sc.data.repository.TrailRawDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailRawManager @Autowired constructor(
    private val trailRawDAO: TrailRawDAO,
    private val trailRawMapper: TrailRawMapper
) {
    fun get(skip: Int, limit: Int): List<TrailRawDto> =
        trailRawDAO.get(skip, limit).map { trailRawMapper.map(it) }

    fun getById(id: String): List<TrailRawDto> =
        trailRawDAO.getById(id).map { trailRawMapper.map(it) }

    fun deleteById(id: String): List<TrailRawDto> =
        trailRawDAO.deleteById(id).map { trailRawMapper.map(it) }

    fun count(): Long =
        trailRawDAO.count()
}