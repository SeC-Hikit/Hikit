package org.sc.manager

import org.sc.common.rest.LinkedMediaDto
import org.sc.common.rest.PoiDto
import org.sc.common.rest.UnLinkeMediaRequestDto
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PoiMapper
import org.sc.data.repository.PoiDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PoiManager @Autowired constructor(
    private val poiDtoMapper: PoiMapper,
    private val poiDAO: PoiDAO,
    private val linkedMediaMapper: LinkedMediaMapper)
{

    fun getPoiPaginated(page: Int, count: Int): List<PoiDto> {
        return poiDAO.get(page, count).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun getPoiByID(id: String): List<PoiDto> {
        return poiDAO.getById(id).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun doesPoiExist(id: String): Boolean = poiDAO.getById(id).isNotEmpty()


    fun getPoiByName(name: String, page: Int, count: Int): List<PoiDto> {
        val elements = poiDAO.getByName(name, page, count).map { poiDtoMapper.poiToPoiDto(it) }
        if (elements.isEmpty()) {
            return poiDAO.getByTags(name, page, count).map { poiDtoMapper.poiToPoiDto(it) }
        }
        return elements
    }

    fun getPoiByTrailId(code: String, page: Int, count: Int): List<PoiDto> {
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

    fun linkMedia(id: String, linkedMedia: LinkedMediaDto): List<PoiDto> {
        val linkMedia = linkedMediaMapper.map(linkedMedia)
        val mediaLinkingResult = poiDAO.linkMedia(id, linkMedia)
        return mediaLinkingResult.map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun unlinkMedia(id: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto) : List<PoiDto>{
        return poiDAO.unlinkMediaId(id, unLinkeMediaRequestDto.id).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun countPoi(): Long = poiDAO.countPOI()
}