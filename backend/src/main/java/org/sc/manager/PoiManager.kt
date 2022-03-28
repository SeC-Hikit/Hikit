package org.sc.manager

import org.sc.common.rest.LinkedMediaDto
import org.sc.common.rest.PoiDto
import org.sc.common.rest.UnLinkeMediaRequestDto
import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PoiMapper
import org.sc.data.model.RecordDetails
import org.sc.data.repository.PoiDAO
import org.sc.manager.regeneration.RegenerationActionType
import org.sc.manager.regeneration.RegenerationEntryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PoiManager @Autowired constructor(
        private val poiDtoMapper: PoiMapper,
        private val poiDAO: PoiDAO,
        private val linkedMediaMapper: LinkedMediaMapper,
        private val resourceManager: ResourceManager,
        private val authFacade: AuthFacade) {

    fun getPoiPaginated(page: Int, count: Int, realm: String): List<PoiDto> {
        return poiDAO.get(page, count, realm).map { poiDtoMapper.poiToPoiDto(it) }
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
        val delete = poiDAO.delete(id)
        val deletedPoi = delete.first()
        // TODO: move this to Service
        deletedPoi.trailIds.forEach {
            resourceManager.addEntry(it, RegenerationEntryType.POI,
                    deletedPoi.id, authFacade.authHelper.username,
                    RegenerationActionType.DELETE)
        }
        return poiByID
    }

    fun upsert(poiDto: PoiDto): List<PoiDto> {
        val fromDto = poiDtoMapper.map(poiDto)
        val authHelper = authFacade.authHelper

        fromDto.keyVal = fromDto.keyVal.distinctBy { it.key.lowercase() }
        fromDto.microType = fromDto.microType.distinct()
        fromDto.trailIds = fromDto.trailIds.distinct()

        fromDto.recordDetails = RecordDetails(Date(),
                authHelper.username,
                authHelper.instance,
                authHelper.realm)
        val upsertPoi = poiDAO.upsert(fromDto)

        // TODO: move this to Service
        val createdPoi = upsertPoi.first()
        createdPoi.trailIds.forEach {
            resourceManager.addEntry(it, RegenerationEntryType.POI,
                    createdPoi.id, authFacade.authHelper.username,
                    RegenerationActionType.CREATE)
        }

        return listOf(poiDto)
    }

    fun update(poiDto: PoiDto): List<PoiDto> {
        val fromDto = poiDtoMapper.map(poiDto)
        val update = poiDAO.update(fromDto)
        val updatedPoi = update.first()

        // TODO: move this to Service
        updatedPoi.trailIds.forEach {
            resourceManager.addEntry(it, RegenerationEntryType.POI,
                    updatedPoi.id, authFacade.authHelper.username,
                    RegenerationActionType.UPDATE)
        }

        return listOf(poiDto)
    }

    fun linkMedia(id: String, linkedMedia: LinkedMediaDto): List<PoiDto> {
        val linkMedia = linkedMediaMapper.map(linkedMedia)
        val mediaLinkingResult = poiDAO.linkMedia(id, linkMedia)
        return mediaLinkingResult.map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun unlinkMedia(id: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto): List<PoiDto> {
        return poiDAO.unlinkMediaId(id, unLinkeMediaRequestDto.id).map { poiDtoMapper.poiToPoiDto(it) }
    }

    fun deleteTrailReference(trailId: String) = poiDAO.unlinkTrailId(trailId)


    fun count(): Long = poiDAO.countPOI()
    fun countByRealm(realm: String): Long = poiDAO.countPOIByRealm(realm)
}