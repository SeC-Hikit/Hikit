package org.sc.manager

import com.google.inject.Inject
import org.sc.DistanceProcessor
import org.sc.GpxManager
import org.sc.common.rest.controller.*
import org.sc.configuration.AppProperties
import org.sc.converter.MetricConverter
import org.sc.data.TrailDAO
import org.sc.data.TrailDistance
import org.sc.service.AltitudeServiceHelper
import java.io.File
import kotlin.math.roundToInt

class TrailManager @Inject constructor(private val trailDAO: TrailDAO,
                                       private val altitudeService: AltitudeServiceHelper,
                                       private val gpxHelper: GpxManager,
                                       private val appProperties: AppProperties){



    fun getAll() = trailDAO.getTrails()
    fun getByCode(code: String) = trailDAO.getTrailByCode(code)
    fun delete(code: String) = trailDAO.delete(code)
    fun allPreview() : List<TrailPreview> = trailDAO.allTrailPreviews;
    fun previewByCode(code: String): List<TrailPreview> = trailDAO.trailPreviewByCode(code)
    fun save(trail: Trail) {
        trailDAO.upsert(trail)
        gpxHelper.writeTrailToGpx(trail)
    }

    fun getDownloadableLink(code: String): String = appProperties.pathToGpxDirectory + File.separator + code + ".gpx"

    fun getByGeo(coordinates: Coordinates, distance: Int, unitOfMeasurement: UnitOfMeasurement, isAnyPoint: Boolean, limit: Int): List<TrailDistance> {
        val coords = CoordinatesWithAltitude(coordinates.longitude,
                coordinates.latitude, altitudeService.getAltitudeByLongLat(coordinates.latitude, coordinates.longitude))
        val meters = getMeters(unitOfMeasurement, distance)
        if (!isAnyPoint) {
            val trailsByStartPosMetricDistance = trailDAO.getTrailsByStartPosMetricDistance(
                    coords.longitude,
                    coords.latitude,
                    meters, limit)
            return trailsByStartPosMetricDistance.map {
                TrailDistance(DistanceProcessor.distanceBetweenPoints(coords, it.startPos.coordinates).roundToInt(),
                        it.startPos.coordinates, it)
            }
        } else {
            return getTrailDistancesWithinRangeAtPoint(coords, distance, unitOfMeasurement, limit)
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