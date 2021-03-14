package org.sc.manager

import org.sc.common.rest.PlaceDto
import org.sc.data.mapper.PlaceMapper
import org.sc.data.repository.PlaceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceManager @Autowired constructor(
    private val placeDao: PlaceDAO,
    private val placeMapper: PlaceMapper,
    private val trailManager: TrailManager
) {

    fun getPaginated(page: Int, count: Int): List<PlaceDto> {
        return placeDao.get(page, count).map { placeMapper.map(it) }
    }

    fun getLikeNameOrTags(name: String, page: Int, count: Int): List<PlaceDto> {
        return placeDao.getLikeName(name, page, count).map { placeMapper.map(it) }
    }

    fun doesItExist(id: String) = getById(id).isNotEmpty()

    fun getById(id: String): List<PlaceDto> {
        return placeDao.getById(id).map { placeMapper.map(it) }
    }

    fun create(place: PlaceDto): List<PlaceDto> {
        return placeDao.create(placeMapper.map(place)).map { placeMapper.map(it) }
    }

    fun deleteById(placeId: String): List<PlaceDto> {
        trailManager.removePlaceRefFromTrails(placeId)
        return placeDao.delete(placeId).map { placeMapper.map(it) }
    }

    fun update(place: PlaceDto): List<PlaceDto> {
        return placeDao.update(placeMapper.map(place)).map { placeMapper.map(it) }
    }
}