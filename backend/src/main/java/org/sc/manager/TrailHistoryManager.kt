package org.sc.manager

import org.sc.common.rest.TrailDto
import org.sc.data.mapper.TrailMapper
import org.sc.data.repository.TrailHistoryDAO
import org.springframework.stereotype.Component

@Component
class TrailHistoryManager constructor(private val trailHistoryDAO: TrailHistoryDAO,
                                      private val trailMapper: TrailMapper) {
    fun addToHistory(trail: TrailDto) {
        val trailEntity = trailMapper.map(trail)
        trailHistoryDAO.create(trailEntity)
    }
}