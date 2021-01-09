package org.sc.manager

import org.sc.common.rest.CoordinatesWithAltitude
import org.sc.common.rest.Trail
import org.sc.common.rest.TrailPreview
import org.sc.common.rest.UnitOfMeasurement
import org.sc.configuration.AppProperties
import org.sc.utils.MetricConverter
import org.sc.data.repository.AccessibilityNotificationDAO
import org.sc.data.repository.MaintenanceDAO
import org.sc.data.repository.TrailDAO
import org.sc.data.TrailDistance
import org.sc.service.AltitudeServiceAdapter
import org.sc.service.DistanceProcessor
import org.sc.service.GpxManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.util.logging.Logger
import kotlin.math.roundToInt

@Component
class TrailManager @Autowired constructor(private val trailDAO: TrailDAO,
                                          private val maintenanceDAO: MaintenanceDAO,
                                          private val accessibilityNotificationDAO: AccessibilityNotificationDAO,
                                          private val altitudeService: AltitudeServiceAdapter,
                                          private val gpxHelper: GpxManager,
                                          private val appProperties: AppProperties) {

    private val logger = Logger.getLogger(TrailManager::class.java.name)


    fun get(isLight: Boolean, page: Int, count: Int): List<Trail> = trailDAO.getTrails(isLight, page, count)
    fun getByCode(code: String, isLight: Boolean): List<Trail> = trailDAO.getTrailByCode(code, isLight)
    fun delete(code: String, isPurged: Boolean): Boolean {
        if (isPurged) {
            val deletedMaintenance = maintenanceDAO.deleteByCode(code)
            val deletedAccessibilityNotification = accessibilityNotificationDAO.deleteByCode(code)
            logger.info("Purge deleting trail $code. Maintenance deleted: $deletedMaintenance, deleted notifications: $deletedAccessibilityNotification")
        }
        return trailDAO.delete(code)
    }

    fun getPreviews(page: Int, count: Int): List<TrailPreview> = trailDAO.getTrailPreviews(page, count)
    fun previewByCode(code: String): List<TrailPreview> = trailDAO.trailPreviewByCode(code)
    fun save(trail: Trail) {
        trailDAO.upsert(trail)
        gpxHelper.writeTrailToGpx(trail)
    }

    fun getDownloadableLink(code: String): String = appProperties.trailStorage + File.separator + code + ".gpx"

    fun getByGeo(coordinates: CoordinatesWithAltitude, distance: Int, unitOfMeasurement: UnitOfMeasurement,
                 isAnyPoint: Boolean, limit: Int): List<TrailDistance> {
        val coords = CoordinatesWithAltitude(coordinates.longitude,
                coordinates.latitude, altitudeService.getAltitudeByLongLat(coordinates.latitude, coordinates.longitude))
        val meters = getMeters(unitOfMeasurement, distance)
        return if (!isAnyPoint) {
            val trailsByStartPosMetricDistance = trailDAO.getTrailsByStartPosMetricDistance(
                    coords.longitude,
                    coords.latitude,
                    meters, limit)
            trailsByStartPosMetricDistance.map {
                TrailDistance(DistanceProcessor.distanceBetweenPoints(coords, it.startPos.coordinates).roundToInt(),
                        it.startPos.coordinates, it)
            }
        } else {
            getTrailDistancesWithinRangeAtPoint(coords, distance, unitOfMeasurement, limit)
        }
    }

    /**
     * Get a list of trail distances from a given point.
     *
     * @param coordinates the given coordinate
     * @param distance the distance value
     * @param unitOfMeasurement a specific unit of measurement to range within
     * @param limit maximum number of trails to be found near the given coordinate
     */
    fun getTrailDistancesWithinRangeAtPoint(coordinates: CoordinatesWithAltitude, distance: Int, unitOfMeasurement: UnitOfMeasurement, limit: Int): List<TrailDistance> {
        val meters = getMeters(unitOfMeasurement, distance)
        val trailsByPointDistance = trailDAO.trailsByPointDistance(
                coordinates.longitude,
                coordinates.latitude,
                meters, limit)

        // for each trail, calculate the distance
        return trailsByPointDistance.map {
            val closestCoordinate = getClosestCoordinate(coordinates, it)
            TrailDistance(
                    DistanceProcessor.distanceBetweenPoints(coordinates, closestCoordinate).toInt(),
                    closestCoordinate, it)
        }
    }

    /**
     * Get the trail closest point to a given coordinate
     *
     * @param givenCoordinatesWAltitude the given coordinate
     * @param trail to refer to
     */
    fun getClosestCoordinate(givenCoordinatesWAltitude: CoordinatesWithAltitude, trail: Trail): CoordinatesWithAltitude {
        return trail.coordinates
                .minBy { DistanceProcessor.distanceBetweenPoints(it, givenCoordinatesWAltitude) }!!
    }

    private fun getMeters(unitOfMeasurement: UnitOfMeasurement, distance: Int) =
            if (unitOfMeasurement == UnitOfMeasurement.km) MetricConverter.toM(distance.toDouble()) else distance.toDouble()

}