package org.sc.manager

import org.sc.common.rest.*
import org.sc.common.rest.geo.GeoLineDto
import org.sc.common.rest.geo.RectangleDto
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.repository.AccessibilityNotificationDAO
import org.sc.data.repository.MaintenanceDAO
import org.sc.data.repository.TrailDAO
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PlaceRefMapper
import org.sc.data.mapper.TrailIntersectionMapper
import org.sc.data.mapper.TrailMapper
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
    private val trailIntersectionMapper: TrailIntersectionMapper,
    private val altitudeService: AltitudeServiceAdapter
) {

    private val logger = Logger.getLogger(TrailManager::class.java.name)

    fun get(isLight: Boolean, page: Int, count: Int): List<TrailDto> = trailDAO.getTrails(isLight, page, count)
        .map { trailMapper.map(it) }

    fun getById(id: String, isLight: Boolean): List<TrailDto> =
        trailDAO.getTrailById(id, isLight).map { trailMapper.map(it) }

    fun getByPlaceRefId(code: String, isLight: Boolean, page: Int, limit: Int): List<TrailDto> =
        trailDAO.getTrailByPlaceId(code, isLight, page, limit).map { trailMapper.map(it) }

    fun delete(id: String, isPurged: Boolean): List<TrailDto> {
        if (isPurged) {
            val deletedMaintenance = maintenanceDAO.deleteByCode(id)
            val deletedAccessibilityNotification = accessibilityNotificationDAO.delete(id)
            logger.info("Purge deleting trail $id. Maintenance deleted: $deletedMaintenance, deleted notifications: $deletedAccessibilityNotification")
        }
        return trailDAO.delete(id).map { trailMapper.map(it) }
    }

    fun save(trail: Trail): List<TrailDto> {
        // TODO #60 create a PDF and KML document too
        trailFileHelper.writeTrailToOfficialGpx(trail)
        return trailDAO.upsert(trail).map { trailMapper.map(it) }
    }

    fun linkMedia(id: String, linkedMediaRequest: LinkedMediaDto): List<TrailDto> {
        val linkMedia = linkedMediaMapper.map(linkedMediaRequest)
        val result = trailDAO.linkMedia(id, linkMedia)
        return result.map { trailMapper.map(it) }
    }

    fun unlinkMedia(id: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto): List<TrailDto> {
        val unlinkedTrail = trailDAO.unlinkMedia(id, unLinkeMediaRequestDto.id)
        return unlinkedTrail.map { trailMapper.map(it) }
    }

    fun doesTrailExist(id: String): Boolean = trailDAO.getTrailById(id, true).isNotEmpty()

    fun linkPlace(id: String, placeRef: PlaceRefDto): List<TrailDto> {
        val linkedTrail = trailDAO.linkPlace(id, placeRefMapper.map(placeRef))
        placeDAO.addTrailIdToPlace(placeRef.placeId, id, placeRef.trailCoordinates)
        return linkedTrail.map { trailMapper.map(it) }
    }

    fun unlinkPlace(id: String, placeRef: PlaceRefDto): List<TrailDto> {
        val unLinkPlace = trailDAO.unLinkPlace(id, placeRefMapper.map(placeRef))
        placeDAO.removeTrailFromPlace(placeRef.placeId, id, placeRef.trailCoordinates)
        return unLinkPlace.map { trailMapper.map(it) }
    }

    fun count(): Long = trailDAO.countTrail()

    fun removePlaceRefFromTrails(placeId: String) {
        trailDAO.unlinkPlaceFromAllTrails(placeId)
    }

    fun findTrailsWithinRectangle(rectangleDto: RectangleDto): List<TrailDto>{
        val trails = trailDAO.findTrailWithinGeoSquare(
                CoordinatesRectangle(rectangleDto.bottomLeft,rectangleDto.topLeft,
                        rectangleDto.topRight,rectangleDto.bottomRight),0,100)
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

    private fun getTrailIntersection(coordinates : List<Coordinates2D>, trail: Trail): TrailIntersectionDto {

        val coordinates2D = GeoCalculator.getIntersectionPointsBetweenSegments(
            coordinates, trail.geoLineString
        )

        val altitudeResultOrderedList = altitudeService.getAltituteByLongLat(coordinates2D.map { coord -> Pair(coord.latitude, coord.longitude) })

        val coordinatesForTrail = mutableListOf<Coordinates>()

        coordinates2D.forEachIndexed { index, coord ->
            coordinatesForTrail.add(CoordinatesWithAltitude(
                coord.latitude, coord.longitude,
                altitudeResultOrderedList[index]
            ))
        }

        return trailIntersectionMapper.map(TrailIntersection(trail, coordinatesForTrail))
    }
}

