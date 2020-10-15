package org.sc.manager

import com.google.inject.Inject
import org.sc.PositionProcessor
import org.sc.converter.MetricConverter
import org.sc.data.*
import org.sc.service.AltitudeServiceHelper
import kotlin.math.roundToInt

class TrailManager @Inject constructor(private val trailDAO: TrailDAO,
                                       private val altitudeService: AltitudeServiceHelper,
                                       private val metricConverter: MetricConverter){


    fun getAll() = trailDAO.getTrails()
    fun getByCode(code: String) = trailDAO.getTrailByCode(code)
    fun delete(code: String) = trailDAO.delete(code)
    fun save(trail: Trail, eta: Int, calculateTotFall: Int, calculateTotRise: Int) {
        // TODO: add calculation to persistency
        trailDAO.upsertTrail(trail)
        exportStoredGpxFile(trail)
    }

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
                TrailDistance(PositionProcessor.distanceBetweenPoints(coords, it.startPos.coordinates).roundToInt(),
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
                    PositionProcessor.distanceBetweenPoints(coordinates, closestCoordinate).toInt(),
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
                .minBy { PositionProcessor.distanceBetweenPoints(it, givenCoordinatesWAltitude) }!!
    }

    private fun getMeters(unitOfMeasurement: UnitOfMeasurement, distance: Int) =
            if (unitOfMeasurement == UnitOfMeasurement.km) metricConverter.getMetersFromKm(distance) else distance

    private fun exportStoredGpxFile(trail: Trail): Any {
        throw NotImplementedError()
    }
}