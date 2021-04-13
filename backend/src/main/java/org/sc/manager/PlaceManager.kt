package org.sc.manager

import org.sc.common.rest.LinkedMediaDto
import org.sc.common.rest.PlaceDto
import org.sc.common.rest.TrailCoordinatesDto
import org.sc.common.rest.UnLinkeMediaRequestDto
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PlaceMapper
import org.sc.data.model.TrailCoordinates
import org.sc.data.repository.PlaceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceManager @Autowired constructor(
        private val placeDao: PlaceDAO,
        private val placeMapper: PlaceMapper,
        private val trailManager: TrailManager,
        private val linkedMediaMapper: LinkedMediaMapper
) {

    fun getPaginated(skip: Int, limit: Int): List<PlaceDto> =
            placeDao.get(skip, limit).map { placeMapper.map(it) }


    fun getLikeNameOrTags(name: String, skip: Int, limit: Int): List<PlaceDto> =
            placeDao.getLikeName(name, skip, limit).map { placeMapper.map(it) }

    fun getNearPoint(longitude: Double, latitude: Double, distance: Double,
                     skip: Int, limit: Int): List<PlaceDto> =
            placeDao.getNear(longitude, latitude, distance, skip, limit).map { placeMapper.map(it) }

    fun doesItExist(id: String) = getById(id).isNotEmpty()

    fun getById(id: String): List<PlaceDto> =
            placeDao.getById(id).map { placeMapper.map(it) }


    fun create(place: PlaceDto): List<PlaceDto> {
        return placeDao.create(placeMapper.map(place)).map { placeMapper.map(it) }
    }

    fun deleteById(placeId: String): List<PlaceDto> {
        trailManager.removePlaceRefFromTrails(placeId)
        return placeDao.delete(placeId).map { placeMapper.map(it) }
    }

    fun update(place: PlaceDto): List<PlaceDto> = placeDao.update(placeMapper.map(place)).map { placeMapper.map(it) }

    fun doesPlaceExist(id: String): Boolean =
            getById(id).isNotEmpty()

    fun linkMedia(placeId: String, linkedMediaRequest: LinkedMediaDto): List<PlaceDto> =
            placeDao.addMediaToPlace(placeId, linkedMediaMapper.map(linkedMediaRequest)).map { placeMapper.map(it) }


    fun unlinkMedia(placeId: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto): List<PlaceDto> =
            placeDao.removeMediaFromPlace(placeId, unLinkeMediaRequestDto.id).map { placeMapper.map(it) }

    fun removeTrailFromPlaces(placeId: String, trailId: String,
                              trailCoordinates: TrailCoordinates) {
        placeDao.removeTrailFromPlace(placeId, trailId, trailCoordinates)
    }

    fun linkTrailToPlace(placeId: String,
                         trailId: String,
                         trailCoordinates: TrailCoordinatesDto) {
        placeDao.addTrailIdToPlace(placeId, trailId, trailCoordinates)
    }

    fun count(): Long = placeDao.count()


}