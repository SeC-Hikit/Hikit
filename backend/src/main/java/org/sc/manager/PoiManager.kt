package org.sc.manager

import org.sc.common.rest.PoiDto
import org.sc.data.PoiMapper
import org.sc.data.entity.Poi
import org.sc.data.repository.PoiRepository
import org.springframework.beans.factory.annotation.Autowired

class PoiManager @Autowired constructor(
    private val poiMapper : PoiMapper,
    private val poiRepository: PoiRepository){


    fun upsertPoi(poiDto: PoiDto): PoiDto {
        val fromDto = poiMapper.fromDto(poiDto)
        return poiMapper.toDto(poiRepository.save(fromDto))
    }
}