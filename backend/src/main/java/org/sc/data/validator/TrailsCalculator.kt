package org.sc.data.validator

import org.sc.common.rest.CoordinatesWithAltitude
import org.sc.service.DistanceProcessor
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

@Service
class TrailsCalculator {

    companion object {
        private const val AVERAGE_SPEED_ON_FLAT_TERRAIN = 3.5
        private const val MINUTES_IN_HOUR = 60
    }

    fun calculateTotRise(coordinates: List<CoordinatesWithAltitude>): Double {
        var rise = 0.0
        for (i in coordinates.indices) {
            if (i < coordinates.size - 1) {
                val currentPoint = coordinates[i]
                val nextPoint = coordinates[i + 1]
                if (isRise(currentPoint, nextPoint))
                    rise += getRise(currentPoint, nextPoint)
            }
        }
        return rise
    }

    fun calculateTotFall(coordinates: List<CoordinatesWithAltitude>): Double {
        var fall = 0.0
        for (i in coordinates.indices) {
            if (i < coordinates.size - 1) {
                val currentPoint = coordinates[i]
                val nextPoint = coordinates[i + 1]
                if (isFall(currentPoint, nextPoint)) {
                    fall += getFall(currentPoint, nextPoint)
                }
            }
        }
        return fall
    }

    fun calculateTrailLength(coordinates: List<CoordinatesWithAltitude>): Double {
        var totalTrailDistance = 0.0
        for (i in coordinates.indices) {
            if (i < coordinates.size - 1) {
                val currentPoint = coordinates[i]
                val nextPoint = coordinates[i + 1]
                totalTrailDistance += DistanceProcessor.distanceBetweenPoints(currentPoint, nextPoint)
            }
        }
        return totalTrailDistance
    }

    fun calculateLengthFromTo(coordinates : List<CoordinatesWithAltitude>, coordinate : CoordinatesWithAltitude) : Int {
        return coordinates.filterIndexed { index, _ -> index < coordinates.indexOf(coordinate) }
                .mapIndexed { index: Int, coord: CoordinatesWithAltitude -> toEntry(index, coord, coordinates) }
                .map { DistanceProcessor.distanceBetweenPoints(it.first, it.second).roundToInt() }
                .sum()
    }

    fun calculateEta(coordinates: List<CoordinatesWithAltitude>): Double {
        val averageTravelSpeed = calculateAverageTravelSpeed(coordinates)
        val trailDistance = calculateTrailLength(coordinates) / 1000
        return (trailDistance / averageTravelSpeed) * MINUTES_IN_HOUR
    }

    private fun calculateAverageTravelSpeed(coordinates: List<CoordinatesWithAltitude>) =
            coordinates
                    .filterIndexed { index, _ -> index != coordinates.lastIndex }
                    .mapIndexed { index: Int, coordinatesWithAltitude: CoordinatesWithAltitude -> toEntry(index, coordinatesWithAltitude, coordinates) }
                    .map { calculateSpeedForSegment(it) }
                    .sum() / (coordinates.size - 1)

    private fun calculateSpeedForSegment(it: Pair<CoordinatesWithAltitude, CoordinatesWithAltitude>) =
            AVERAGE_SPEED_ON_FLAT_TERRAIN * exp(-3.5 * abs((getDifferenceInAltitude(it.first, it.second) / 1000) /
                    (DistanceProcessor.distanceBetweenPoints(it.first, it.second) / 1000) + 0.05))

    private fun getDifferenceInAltitude(currentPoint: CoordinatesWithAltitude, nextPoint: CoordinatesWithAltitude) =
            nextPoint.altitude - currentPoint.altitude

    private fun toEntry(index: Int, trailCoordinates: CoordinatesWithAltitude, coordinates: List<CoordinatesWithAltitude>)
            : Pair<CoordinatesWithAltitude, CoordinatesWithAltitude> = Pair(trailCoordinates, coordinates[index + 1])

    private fun getFall(currentPoint: CoordinatesWithAltitude, nextPoint: CoordinatesWithAltitude) =
            getAbsDifferenceInAltitude(nextPoint, currentPoint)

    private fun getRise(currentPoint: CoordinatesWithAltitude, nextPoint: CoordinatesWithAltitude) =
            getAbsDifferenceInAltitude(currentPoint, nextPoint)

    private fun getAbsDifferenceInAltitude(currentPoint: CoordinatesWithAltitude, nextPoint: CoordinatesWithAltitude) =
            abs(nextPoint.altitude - currentPoint.altitude)

    private fun isRise(currentPoint: CoordinatesWithAltitude,
                       nextPoint: CoordinatesWithAltitude
    ) = currentPoint.altitude < nextPoint.altitude

    private fun isFall(currentPoint: CoordinatesWithAltitude,
                       nextPoint: CoordinatesWithAltitude
    ): Boolean = currentPoint.altitude > nextPoint.altitude
}
