package org.sc.processor

import org.sc.common.rest.CoordinatesDto
import org.springframework.stereotype.Component
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

@Component
class TrailsCalculator {

    companion object {
        private const val AVERAGE_SPEED_ON_FLAT_TERRAIN = 3.5
        private const val MINUTES_IN_HOUR = 60
    }

    fun calculateTotRise(coordinates: List<CoordinatesDto>): Double {
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

    fun calculateTotFall(coordinates: List<CoordinatesDto>): Double {
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

    fun calculateTrailLength(coordinates: List<CoordinatesDto>): Double {
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

    fun calculateLengthFromTo(coordinates : List<CoordinatesDto>, coordinate : CoordinatesDto) : Int {
        return coordinates.filterIndexed { index, _ -> index < coordinates.indexOf(coordinate) }
                .mapIndexed { index: Int, coord: CoordinatesDto -> toEntry(index, coord, coordinates) }
                .map { DistanceProcessor.distanceBetweenPoints(it.first, it.second).roundToInt() }
                .sum()
    }

    fun calculateEta(coordinates: List<CoordinatesDto>): Double {
        val averageTravelSpeed = calculateAverageTravelSpeed(coordinates)
        val trailDistance = calculateTrailLength(coordinates) / 1000
        return (trailDistance / averageTravelSpeed) * MINUTES_IN_HOUR
    }

    private fun calculateAverageTravelSpeed(coordinates: List<CoordinatesDto>) =
            coordinates
                    .filterIndexed { index, _ -> index != coordinates.lastIndex }
                    .mapIndexed { index: Int, CoordinatesDto: CoordinatesDto -> toEntry(index, CoordinatesDto, coordinates) }
                    .map { calculateSpeedForSegment(it) }
                    .sum() / (coordinates.size - 1)

    private fun calculateSpeedForSegment(it: Pair<CoordinatesDto, CoordinatesDto>) =
            AVERAGE_SPEED_ON_FLAT_TERRAIN * exp(-3.5 * abs((getDifferenceInAltitude(it.first, it.second) / 1000) /
                    (DistanceProcessor.distanceBetweenPoints(it.first, it.second) / 1000) + 0.05))

    private fun getDifferenceInAltitude(currentPoint: CoordinatesDto, nextPoint: CoordinatesDto) =
            nextPoint.altitude - currentPoint.altitude

    private fun toEntry(index: Int, trailCoordinates: CoordinatesDto, coordinates: List<CoordinatesDto>)
            : Pair<CoordinatesDto, CoordinatesDto> = Pair(trailCoordinates, coordinates[index + 1])

    private fun getFall(currentPoint: CoordinatesDto, nextPoint: CoordinatesDto) =
            getAbsDifferenceInAltitude(nextPoint, currentPoint)

    private fun getRise(currentPoint: CoordinatesDto, nextPoint: CoordinatesDto) =
            getAbsDifferenceInAltitude(currentPoint, nextPoint)

    private fun getAbsDifferenceInAltitude(currentPoint: CoordinatesDto, nextPoint: CoordinatesDto) =
            abs(nextPoint.altitude - currentPoint.altitude)

    private fun isRise(currentPoint: CoordinatesDto,
                       nextPoint: CoordinatesDto
    ) = currentPoint.altitude < nextPoint.altitude

    private fun isFall(currentPoint: CoordinatesDto,
                       nextPoint: CoordinatesDto
    ): Boolean = currentPoint.altitude > nextPoint.altitude
}
