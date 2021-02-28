package org.sc.manager

import org.sc.common.rest.LinkedMediaDto
import org.sc.common.rest.LinkedMediaResultDto
import org.sc.common.rest.PoiDto
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PoiMapper
import org.sc.data.repository.PoiDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PoiManager @Autowired constructor(
    private val poiDtoMapper: PoiMapper,
    private val poiDAO: PoiDAO,
    private val linkedMediaMapper: LinkedMediaMapper
) {

    fun getPoiPaginated(page: Int, count: Int): List<PoiDto> {
        return poiDAO.get(page, count).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun getPoiByID(id: String): List<PoiDto> {
        return poiDAO.getById(id).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun getPoiByName(name: String, page: Int, count: Int): List<PoiDto> {
        val elements = poiDAO.getByName(name, page, count).map { poiDtoMapper.poiToPoiDto(it) }
        if (elements.isEmpty()) {
            return poiDAO.getByTags(name, page, count).map { poiDtoMapper.poiToPoiDto(it) }
        }
        return elements
    }

    fun getPoiByTrailCode(code: String, page: Int, count: Int): List<PoiDto> {
        return poiDAO.getByCode(code, page, count).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun getPoiByMacro(macro: String, page: Int, count: Int): List<PoiDto> {
        return poiDAO.getByMacro(macro, page, count).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun getPoiByPointDistance(
        longitude: Double,
        latitude: Double,
        meters: Double,
        page: Int,
        count: Int
    ): List<PoiDto> {
        return poiDAO.getByPosition(longitude, latitude, meters, page, count).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun deleteById(id: String): List<PoiDto> {
        val poiByID = getPoiByID(id)
        poiDAO.delete(id)
        return poiByID
    }

    fun upsertPoi(poiDto: PoiDto): List<PoiDto> {
        val fromDto = poiDtoMapper.poiDtoToPoi(poiDto)
        poiDAO.upsert(fromDto)
        return listOf(poiDto)
    }

    fun linkMedia(id: String, linkedMedia: LinkedMediaDto): List<LinkedMediaResultDto> {
        val linkMedia = linkedMediaMapper.map(linkedMedia)
        val result = poiDAO.linkMedia(id, linkMedia)
        return result.map { LinkedMediaResultDto(id, linkedMediaMapper.map(it)) }
    }
}