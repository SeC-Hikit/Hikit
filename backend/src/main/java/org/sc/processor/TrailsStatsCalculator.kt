package org.sc.processor

import org.sc.data.model.Coordinates
import org.sc.data.model.Coordinates2D
import org.springframework.stereotype.Component
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt

@Component
class TrailsStatsCalculator {

    companion object {
        private const val AVERAGE_SPEED_ON_FLAT_TERRAIN = 3.5
        private const val MINUTES_IN_HOUR = 60
    }

    fun calculateTotRise(coordinates: List<Coordinates>): Double {
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

    fun calculateTotFall(coordinates: List<Coordinates>): Double {
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

    fun calculateTrailLength(coordinates: List<Coordinates>): Double {
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

    fun calculateTrailLengthFromToPoint(coordinates: List<Coordinates>,
                                        fromTo: List<Coordinates2D>): Double {

        fun isStartOrEnd(targetCoord: Coordinates, fromTo : List<Coordinates2D>) =
            fromTo.any {
                targetCoord.latitude == it.latitude && targetCoord.longitude == it.longitude
            }

        var totalTrailDistance = 0.0
        var indexStart : Int = -1
        for (i in coordinates.indices) {
            if (i < coordinates.size - 1) { // Found start
                if (indexStart > -1) {
                    val currentPoint = coordinates[i]
                    val nextPoint = coordinates[i + 1]
                    totalTrailDistance += DistanceProcessor.distanceBetweenPoints(currentPoint, nextPoint)

                    if (isStartOrEnd(nextPoint, fromTo)) {
                        break
                    }

                } else {    // Not started calculating yet
                    // start from
                    if (isStartOrEnd(coordinates[i], fromTo)){
                        indexStart = i
                    }
                }
            }
        }
        return totalTrailDistance
    }

    /**
     * Used for determining where to place a point (use case > intersecting municipality territories) on an existing trail
     * Returns a pair of > distance -> index
     */
    fun getLowestCumulativeDistanceAndIndexForCoordinate(coordinates: List<Coordinates>, targetCoordinate : Coordinates): Pair<Double, Int>? {
        val results = ArrayList<Pair<Double, Int>>()

        for (i in coordinates.indices) {
            if (i < coordinates.size - 1) {
                val currentPoint = coordinates[i]
                val nextPoint = coordinates[i + 1]
                val cumulativeDistance = DistanceProcessor.distanceBetweenPoints(currentPoint, targetCoordinate) +
                        DistanceProcessor.distanceBetweenPoints(nextPoint, targetCoordinate)

                val nextIndex = i + 1
                results.add(Pair(cumulativeDistance, nextIndex))
            }
        }

        return results.minByOrNull { it.first }
    }

    fun calculateHighestPlace(coordinates: List<Coordinates>): Double = coordinates.maxOf { it.altitude }
    fun calculateLowestPlace(coordinates: List<Coordinates>): Double = coordinates.minOf { it.altitude }

    fun calculateLengthFromTo(coordinates: List<Coordinates>, coordinate: Coordinates): Int {
        return coordinates.filterIndexed { index, _ -> index < coordinates.indexOf(coordinate) }
            .mapIndexed { index: Int, coord: Coordinates -> toEntry(index, coord, coordinates) }
            .sumOf { DistanceProcessor.distanceBetweenPoints(it.first, it.second).roundToInt() }
    }

    fun calculateEta(coordinates: List<Coordinates>): Double {
        val averageTravelSpeed = calculateAverageTravelSpeed(coordinates)
        val trailDistance = calculateTrailLength(coordinates) / 1000
        return (trailDistance / averageTravelSpeed) * MINUTES_IN_HOUR
    }

    private fun calculateAverageTravelSpeed(coordinates: List<Coordinates>): Double =
            coordinates
                    .filterIndexed { index, _ -> index != coordinates.lastIndex }
                    .mapIndexed { index: Int, CoordinatesDto: Coordinates -> toEntry(index, CoordinatesDto, coordinates) }
                    .sumOf { calculateSpeedForSegment(it) } / (coordinates.size - 1)

    private fun calculateSpeedForSegment(it: Pair<Coordinates, Coordinates>): Double {
        val distanceBetweenPoints = DistanceProcessor.distanceBetweenPoints(it.first, it.second)
        val electedDistance = if (distanceBetweenPoints > 0) distanceBetweenPoints else 1.0
        return AVERAGE_SPEED_ON_FLAT_TERRAIN * exp(
                -3.5 * abs(
                        (getDifferenceInAltitude(it.first, it.second) / 1000) /
                                (electedDistance / 1000) + 0.05
                )
        )
    }

    private fun getDifferenceInAltitude(currentPoint: Coordinates, nextPoint: Coordinates) =
            nextPoint.altitude - currentPoint.altitude

    private fun toEntry(index: Int, trailCoordinates: Coordinates, coordinates: List<Coordinates>)
            : Pair<Coordinates, Coordinates> = Pair(trailCoordinates, coordinates[index + 1])

    private fun getFall(currentPoint: Coordinates, nextPoint: Coordinates) =
            getAbsDifferenceInAltitude(nextPoint, currentPoint)

    private fun getRise(currentPoint: Coordinates, nextPoint: Coordinates) =
            getAbsDifferenceInAltitude(currentPoint, nextPoint)

    private fun getAbsDifferenceInAltitude(currentPoint: Coordinates, nextPoint: Coordinates) =
            abs(nextPoint.altitude - currentPoint.altitude)

    private fun isRise(
            currentPoint: Coordinates,
            nextPoint: Coordinates
    ) = currentPoint.altitude < nextPoint.altitude

    private fun isFall(
            currentPoint: Coordinates,
            nextPoint: Coordinates
    ): Boolean = currentPoint.altitude > nextPoint.altitude
}
