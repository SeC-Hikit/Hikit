package org.sc.manager

import org.sc.common.rest.PoiDto
import org.sc.data.dto.PoiDtoMapper
import org.sc.data.repository.PoiDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PoiManager @Autowired constructor(
    private val poiDtoMapper : PoiDtoMapper,
    private val poiDAO: PoiDAO){

    fun getPoiPaginated(page: Int, count: Int) : List<PoiDto>  {
        return poiDAO.get(page, count).map { poiDtoMapper.toDto(it) }
    }

    fun getPoiByID(id: String) : List<PoiDto>  {
        return poiDAO.getById(id).map { poiDtoMapper.toDto(it) }
    }

    fun getPoiByName(name: String, page: Int, count: Int) : List<PoiDto>  {
        val elements = poiDAO.getByName(name, page, count).map { poiDtoMapper.toDto(it) }
        if(elements.isEmpty()) {
           return poiDAO.getByTags(name, page, count).map { poiDtoMapper.toDto(it) }
        }
        return elements
    }

    fun getPoiByTrailCode(code: String, page: Int, count: Int) : List<PoiDto>  {
        return poiDAO.getByCode(code, page, count).map { poiDtoMapper.toDto(it) }
    }

    fun getPoiByMacro(macro: String, page: Int, count: Int) : List<PoiDto>  {
        return poiDAO.getByMacro(macro, page, count).map { poiDtoMapper.toDto(it) }
    }

    fun getPoiByPointDistance(longitude: Double, latitude: Double, meters: Double, page: Int, count: Int) : List<PoiDto>  {
        return poiDAO.getByPosition(longitude, latitude, meters, page, count).map { poiDtoMapper.toDto(it) }
    }

    fun deleteById(id: String): Boolean {
        return poiDAO.delete(id)
    }

    fun upsertPoi(poiDto: PoiDto) {
        val fromDto = poiDtoMapper.toEntity(poiDto)
        return poiDAO.upsert(fromDto)
    }
}