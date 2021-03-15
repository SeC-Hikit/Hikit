package org.sc.manager

import org.sc.common.rest.*
import org.sc.processor.MetricConverter
import org.sc.data.repository.AccessibilityNotificationDAO
import org.sc.data.repository.MaintenanceDAO
import org.sc.data.repository.TrailDAO
import org.sc.data.TrailDistance
import org.sc.data.mapper.LinkedMediaMapper
import org.sc.data.mapper.PlaceRefMapper
import org.sc.data.mapper.TrailMapper
import org.sc.data.mapper.TrailPreviewMapper
import org.sc.data.model.Coordinates
import org.sc.data.model.Trail
import org.sc.data.model.TrailRaw
import org.sc.data.repository.PlaceDAO
import org.sc.processor.DistanceProcessor
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
    private val trailPreviewMapper: TrailPreviewMapper,
    private val linkedMediaMapper: LinkedMediaMapper,
    private val placeRefMapper: PlaceRefMapper
) {

    private val logger = Logger.getLogger(TrailManager::class.java.name)

    fun get(isLight: Boolean, page: Int, count: Int): List<TrailDto> = trailDAO.getTrails(isLight, page, count)
        .map { trailMapper.trailToTrailDto(it) }

    fun getById(id: String, isLight: Boolean): List<TrailDto> =
        trailDAO.getTrailById(id, isLight).map { trailMapper.trailToTrailDto(it) }

    fun getByPlaceRefId(code: String, isLight: Boolean): List<TrailDto> =
        trailDAO.getTrailByPlaceId(code, isLight).map { trailMapper.trailToTrailDto(it) }

    fun delete(id: String, isPurged: Boolean): List<TrailDto> {
        if (isPurged) {
            val deletedMaintenance = maintenanceDAO.deleteByCode(id)
            val deletedAccessibilityNotification = accessibilityNotificationDAO.delete(id)
            logger.info("Purge deleting trail $id. Maintenance deleted: $deletedMaintenance, deleted notifications: $deletedAccessibilityNotification")
        }
        return trailDAO.delete(id).map { trailMapper.trailToTrailDto(it) }
    }

    fun getPreviews(page: Int, count: Int): List<TrailPreviewDto> =
        trailDAO.getTrailPreviews(page, count).map { trailPreviewMapper.trailPreviewToTrailPreviewDto(it) }

    fun previewById(id: String): List<TrailPreviewDto> = trailDAO.trailPreviewById(id)
        .map { trailPreviewMapper.trailPreviewToTrailPreviewDto(it) }

    fun save(trail: Trail): List<TrailDto> {
        trailFileHelper.writeTrailToOfficialGpx(trail)
        return trailDAO.upsert(trail).map { trailMapper.trailToTrailDto(it) }
    }

    fun linkMedia(id: String, linkedMediaRequest: LinkedMediaDto): List<TrailDto> {
        val linkMedia = linkedMediaMapper.map(linkedMediaRequest)
        val result = trailDAO.linkMedia(id, linkMedia)
        return result.map { trailMapper.trailToTrailDto(it) }
    }

    fun unlinkMedia(id: String, unLinkeMediaRequestDto: UnLinkeMediaRequestDto): List<TrailDto> {
        val unlinkedTrail = trailDAO.unlinkMedia(id, unLinkeMediaRequestDto.id)
        return unlinkedTrail.map { trailMapper.trailToTrailDto(it) }
    }

//    fun getByGeo(
//        coords: CoordinatesDto, distance: Int, unitOfMeasurement: UnitOfMeasurement,
//        isAnyPoint: Boolean, limit: Int
//    ): List<TrailDistance> {
////        val meters = getMeters(unitOfMeasurement, distance)
////        return if (!isAnyPoint) {
////
////            val trailsByStartPosMetricDistance = trailDAO.getTrailsByStartPosMetricDistance(
////                coords.longitude,
////                coords.latitude,
////                meters, limit
////            )
////            val trailsDto = trailsByStartPosMetricDistance.map { trailMapper.trailToTrailDto(it) }
////
////            trailsDto.map {
////                TrailDistance(
////                    DistanceProcessor.distanceBetweenPoints(coords, it.startPos.coordinates).roundToInt(),
////                    it.startPos.coordinates, it
////                )
////            }
////        } else {
////            getTrailDistancesWithinRangeAtPoint(coords, distance, unitOfMeasurement, limit)
////        }
//    }

    fun getTrailDistancesWithinRangeAtPoint(
        coordinates: CoordinatesDto,
        distance: Int,
        unitOfMeasurement: UnitOfMeasurement,
        limit: Int
    ): List<TrailDistance> {
        val meters = getMeters(unitOfMeasurement, distance)
        val trailsByPointDistance = trailDAO.trailsByPointDistance(
            coordinates.longitude,
            coordinates.latitude,
            meters, limit
        )

        val trailsDto = trailsByPointDistance.map { trailMapper.trailToTrailDto(it) }

        // for each trail, calculate the distance
        return trailsDto.map {
            val closestCoordinate = getClosestCoordinate(coordinates, it)
            TrailDistance(
                DistanceProcessor.distanceBetweenPoints(coordinates, closestCoordinate).toInt(),
                closestCoordinate, it
            )
        }
    }

    fun doesTrailExist(id: String): Boolean = trailDAO.getTrailById(id, true).isNotEmpty()

    fun linkPlace(id: String, placeRef: PlaceRefDto): List<TrailDto> {
        val linkedTrail = trailDAO.linkPlace(id, placeRefMapper.map(placeRef))
        placeDAO.addTrailIdToPlace(placeRef.placeId, id)
        return linkedTrail.map { trailMapper.trailToTrailDto(it) }
    }

    fun unlinkPlace(id: String, placeRef: PlaceRefDto): List<TrailDto> {
        val unLinkPlace = trailDAO.unLinkPlace(id, placeRefMapper.map(placeRef))
        placeDAO.removeTrailFromPlace(placeRef.placeId, id)
        return unLinkPlace.map { trailMapper.trailToTrailDto(it) }
    }

    /**
     * Get the trail closest point to a given coordinate
     *
     * @param givenCoordinatesWAltitude the given coordinate
     * @param trail to refer to
     */
    fun getClosestCoordinate(givenCoordinatesWAltitude: Coordinates, trail: TrailDto): Coordinates {
        return trail.coordinates
            .minByOrNull { DistanceProcessor.distanceBetweenPoints(it, givenCoordinatesWAltitude) }!!
    }

    private fun getMeters(unitOfMeasurement: UnitOfMeasurement, distance: Int) =
        if (unitOfMeasurement == UnitOfMeasurement.km) MetricConverter.toM(distance.toDouble()) else distance.toDouble()

    fun countTrail(): Long = trailDAO.countTrail()

    fun removePlaceRefFromTrails(placeId: String) {
        trailDAO.unlinkPlaceFromAllTrails(placeId)
    }
}

