package org.sc.manager

import org.sc.adapter.AltitudeServiceAdapter
import org.sc.common.rest.*
import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PlaceMapper
import org.sc.data.model.*
import org.sc.data.repository.PlaceDAO
import org.sc.manager.regeneration.RegenerationActionType
import org.sc.manager.regeneration.RegenerationEntryType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PlaceManager @Autowired constructor(
        private val placeDao: PlaceDAO,
        private val placeMapper: PlaceMapper,
        private val trailManager: TrailManager,
        private val linkedMediaMapper: LinkedMediaMapper,
        private val altitudeServiceAdapter: AltitudeServiceAdapter,
        private val resourceManager: ResourceManager,
        private val authFacade: AuthFacade
) {

    fun getPaginated(skip: Int, limit: Int, realm: String, isDynamic: Boolean): List<PlaceDto> =
            placeDao.get(skip, limit, realm, isDynamic).map { placeMapper.map(it) }

    fun getOldestPaginated(skip: Int, limit: Int, isDynamic: Boolean, realm: String = "*"): List<PlaceDto> =
            placeDao.getOldest(skip, limit, realm, isDynamic).map { placeMapper.map(it) }

    fun getLikeNameOrTags(name: String, skip: Int, limit: Int, realm: String): List<PlaceDto> =
            placeDao.getLikeName(name, skip, limit, realm).map { placeMapper.map(it) }

    fun getNearPoint(longitude: Double, latitude: Double, distance: Double,
                     skip: Int, limit: Int): List<PlaceDto> =
            placeDao.getNear(longitude, latitude, distance, skip, limit).map { placeMapper.map(it) }

    fun getById(id: String): List<PlaceDto> =
            placeDao.getById(id).map { placeMapper.map(it) }

    fun create(place: PlaceDto): List<PlaceDto> {
        val mapCreation = placeMapper.mapCreation(place)
        mapCreation.recordDetails = RecordDetails(
                Date(),
                authFacade.authHelper.username,
                authFacade.authHelper.instance,
                authFacade.authHelper.realm
        )
        mapCreation.coordinates = ensureCorrectElevation(mapCreation)

        // TODO: move to service
        val createdPlace = placeDao.create(mapCreation).first()
        createdPlace.crossingTrailIds.forEach {
            resourceManager.addEntry(it, RegenerationEntryType.PLACE,
                    createdPlace.id, authFacade.authHelper.username,
                    RegenerationActionType.CREATE)
        }
        return listOf(placeMapper.map(createdPlace))
    }

    fun deleteById(placeId: String): List<PlaceDto> {
        trailManager.removePlaceRefFromTrails(placeId)
        val deletablePlace = placeDao.delete(placeId)
        if(deletablePlace.isEmpty()) return emptyList()
        val deletedPlace = deletablePlace.first()
        deletedPlace.crossingTrailIds.forEach {
            resourceManager.addEntry(it, RegenerationEntryType.PLACE,
                    deletedPlace.id, authFacade.authHelper.username,
                    RegenerationActionType.DELETE)
        }
        return listOf(placeMapper.map(deletedPlace))
    }

    fun update(place: PlaceDto): List<PlaceDto> {
        val update = placeDao.updateNameAndTags(placeMapper.map(place)).first()
        update.crossingTrailIds.forEach {
            resourceManager.addEntry(it, RegenerationEntryType.PLACE,
                    update.id, authFacade.authHelper.username,
                    RegenerationActionType.UPDATE)
        }
        return listOf(placeMapper.map(update))
    }

    fun doesPlaceExist(id: String): Boolean =
            getById(id).isNotEmpty()

    fun linkMedia(placeId: String, linkedMediaRequest: LinkedMediaDto): List<PlaceDto> =
            placeDao.addMediaToPlace(placeId, linkedMediaMapper.map(linkedMediaRequest)).map { placeMapper.map(it) }

    fun unlinkMedia(placeId: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto): List<PlaceDto> =
            placeDao.removeMediaFromPlace(placeId, unLinkeMediaRequestDto.id).map { placeMapper.map(it) }


    fun unlinkTrailFromPlace(placeId: String, trailId: String, coordinates: Coordinates) {
        placeDao.removeTrailFromPlace(placeId,
                trailId, coordinates)
                .map { placeMapper.map(it) }
    }

    fun unlinkTrailFromPlace(linkedPlaceDto: LinkedPlaceDto): List<PlaceDto> =
            placeDao.removeTrailFromPlace(linkedPlaceDto.placeId,
                    linkedPlaceDto.trailId,
                    linkedPlaceDto.coordinatesDto)
                    .map { placeMapper.map(it) }

    fun linkTrailToPlace(linkedPlaceDto: LinkedPlaceDto): List<PlaceDto> =
            placeDao.linkTrailToPlace(linkedPlaceDto.placeId,
                    linkedPlaceDto.trailId, linkedPlaceDto.coordinatesDto).map { placeMapper.map(it) }

    fun deleteTrailReference(trailId: String, locationRefs: List<PlaceRefDto>) {
        locationRefs.forEach {
            placeDao.removeTrailFromPlace(it.placeId, trailId, it.coordinates)
        }
    }

    fun mergePlacePositions(placeId: String, otherPlaceId: String) {
        val place = placeDao.getById(placeId).first()
        val otherPlace = placeDao.getById(otherPlaceId).first()
        val points = otherPlace.points.coordinates2D
        placeDao.updatePlacePoints(place.id, points)
    }

    fun addNotExistingTrailsIdToPlaceId(id: String, crossingTrailIds: List<String>) =
        placeDao.addTrailsIdToPlace(id, crossingTrailIds)


    fun findNearestMatchByCoordinatesExcludingById(
            id: String, coordinates: List<CoordinatesDto>,
            distance: Double, instanceRealm: String) : List<Place> {
        val latitudeMax = coordinates.maxOf { it.latitude }
        val latitudeMin = coordinates.minOf { it.latitude }
        val longitudeMax = coordinates.maxOf { it.longitude }
        val longitudeMin = coordinates.minOf { it.longitude }
        val middleLatitude = (latitudeMax + latitudeMin) / 2
        val middleLongitude = (longitudeMax + longitudeMin) / 2
        return placeDao.getNotDynamicsNearExcludingById(middleLongitude, middleLatitude,
                distance, id, instanceRealm)
    }

    private fun ensureCorrectElevation(mapCreation: Place) = mapCreation.coordinates.map {
        CoordinatesWithAltitude(
                it.latitude, it.longitude,
                altitudeServiceAdapter
                        .getElevationsByLongLat(it.latitude, it.longitude)
                        .first()
        )
    }

    fun count(): Long = placeDao.count()
    fun countByRealm(realm: String, isDynamic: Boolean = false): Long = placeDao.count(realm, isDynamic)
    fun countByNameOrTags(name: String, realm: String) = placeDao.count(name, realm)

}