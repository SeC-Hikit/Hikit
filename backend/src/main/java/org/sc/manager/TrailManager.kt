package org.sc.manager

import org.sc.data.mapper.*
import org.sc.common.rest.*
import org.sc.common.rest.geo.GeoLineDto
import org.sc.common.rest.geo.RectangleDto
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.repository.AccessibilityNotificationDAO
import org.sc.data.repository.MaintenanceDAO
import org.sc.data.repository.TrailDAO
import org.sc.data.model.*
import org.sc.data.repository.PlaceDAO
import org.sc.processor.GeoCalculator
import org.sc.service.AltitudeServiceAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class TrailManager @Autowired constructor(
    private val trailDAO: TrailDAO,
    private val maintenanceDAO: MaintenanceDAO,
    private val accessibilityNotificationDAO: AccessibilityNotificationDAO,
    private val placeDAO: PlaceDAO,
    private val trailFileHelper: TrailFileManager,
    private val trailMapper: TrailMapper,
    private val linkedMediaMapper: LinkedMediaMapper,
    private val placeRefMapper: PlaceRefMapper,
    private val coordinatesMapper: CoordinatesMapper,
    private val trailIntersectionMapper: TrailIntersectionMapper,
    private val altitudeService: AltitudeServiceAdapter
) {

    private val logger = Logger.getLogger(TrailManager::class.java.name)

    fun get(
        isLight: Boolean,
        page: Int,
        count: Int,
        realm: String
    ): List<TrailDto> = trailDAO.getTrails(isLight, page, count, realm)
        .map { trailMapper.map(it) }

    fun getById(id: String, isLight: Boolean, level: String): List<TrailDto> =
        trailDAO.getTrailById(id, isLight, level).map { trailMapper.map(it) }

    fun getByPlaceRefId(code: String, isLight: Boolean, page: Int, limit: Int): List<TrailDto> =
        trailDAO.getTrailByPlaceId(code, isLight, page, limit).map { trailMapper.map(it) }

    fun delete(id: String, level: String): List<TrailDto> {
        val deletedMaintenance = maintenanceDAO.deleteByTrailId(id)
        val deletedAccessibilityNotification = accessibilityNotificationDAO.deleteByTrailId(id)
        val deletedTrailInMem = trailDAO.delete(id, level)
        deletedTrailInMem.forEach { trail ->
            trail.locations.forEach {
                placeDAO.removeTrailFromPlace(it.placeId, trail.id, it.coordinates)
            }
        }
        logger.info("Purge deleting trail $id. Maintenance deleted: $deletedMaintenance, " +
                "deleted notifications: $deletedAccessibilityNotification" + ",")
        return deletedTrailInMem.map { trailMapper.map(it) }
    }

    fun saveWithGeo(trail: Trail): List<TrailDto> {
        // TODO #60 create a PDF and KML document too
        trailFileHelper.writeTrailToOfficialGpx(trail)
        return trailDAO.upsert(trail).map { trailMapper.map(it) }
    }

    fun update(trail: Trail): List<TrailDto> {
        return trailDAO.upsert(trail).map { trailMapper.map(it) }
    }

    fun linkMedia(id: String, linkedMediaRequest: LinkedMediaDto, level: String): List<TrailDto> {
        val linkMedia = linkedMediaMapper.map(linkedMediaRequest)
        val result = trailDAO.linkMedia(id, linkMedia, level)
        return result.map { trailMapper.map(it) }
    }

    fun unlinkMedia(id: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto, level: String): List<TrailDto> {
        val unlinkedTrail = trailDAO.unlinkMedia(id, unLinkeMediaRequestDto.id, level)
        return unlinkedTrail.map { trailMapper.map(it) }
    }

    fun doesTrailExist(id: String, level: String): Boolean = trailDAO.getTrailById(id, true, level).isNotEmpty()

    fun linkPlace(id: String, placeRef: PlaceRefDto, level: String): List<TrailDto> {
        val linkedTrail = trailDAO.linkPlace(id, placeRefMapper.map(placeRef), level)
        placeDAO.addTrailIdToPlace(placeRef.placeId, id, placeRef.coordinates)
        return linkedTrail.map { trailMapper.map(it) }
    }

    fun unlinkPlace(id: String, placeRef: PlaceRefDto, level: String): List<TrailDto> {
        val unLinkPlace = trailDAO.unLinkPlace(id, placeRefMapper.map(placeRef), level)
        placeDAO.removeTrailFromPlace(
            placeRef.placeId, id,
            coordinatesMapper.map(placeRef.coordinates)
        )
        return unLinkPlace.map { trailMapper.map(it) }
    }

    fun count(): Long = trailDAO.countTrail()

    fun removePlaceRefFromTrails(placeId: String) {
        trailDAO.unlinkPlaceFromAllTrails(placeId)
    }

    fun findTrailsWithinRectangle(rectangleDto: RectangleDto): List<TrailDto>{
        val trails = trailDAO.findTrailWithinGeoSquare(
                CoordinatesRectangle(rectangleDto.bottomLeft, rectangleDto.topRight),0,100)
        return trails.map { trailMapper.map(it) }
    }

    fun findIntersection(geoLineDto: GeoLineDto, skip: Int, limit: Int): List<TrailIntersectionDto> {
        val outerGeoSquare = GeoCalculator.getOuterSquareForCoordinates(geoLineDto.coordinates)
        val foundTrailsWithinGeoSquare = trailDAO.findTrailWithinGeoSquare(outerGeoSquare, skip, limit)

        return foundTrailsWithinGeoSquare.filter {
            GeoCalculator.areSegmentsIntersecting(
                geoLineDto.coordinates, it.geoLineString
            )
        }.map { trail ->
            getTrailIntersection(geoLineDto.coordinates, trail)
        }
    }

    private fun getTrailIntersection(coordinates: List<Coordinates2D>, trail: Trail): TrailIntersectionDto {

        val coordinates2D = GeoCalculator.getIntersectionPointsBetweenSegments(
            coordinates, trail.geoLineString
        )

        val altitudeResultOrderedList =
            altitudeService.getAltituteByLongLat(coordinates2D.map { coord -> Pair(coord.latitude, coord.longitude) })

        val coordinatesForTrail = mutableListOf<Coordinates>()

        coordinates2D.forEachIndexed { index, coord ->
            coordinatesForTrail.add(
                CoordinatesWithAltitude(
                    coord.latitude, coord.longitude,
                    altitudeResultOrderedList[index]
                )
            )
        }

        return trailIntersectionMapper.map(TrailIntersection(trail, coordinatesForTrail))
    }
}

