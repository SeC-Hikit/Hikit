package org.sc.importer

import org.sc.data.CoordinatesWithAltitude
import kotlin.math.abs

class TrailsCalculator {

    fun calculateTotRise(coordinates: List<CoordinatesWithAltitude>): Int {
        var rise = 0.0
        for (i in coordinates.indices) {
            if(i < coordinates.size - 1) {
                val currentPoint = coordinates[i]
                val nextPoint = coordinates[i + 1]
                if(isRise(currentPoint, nextPoint)) rise +=
                        getRise(nextPoint, currentPoint)
            }
        }
        return rise.toInt()
    }

    fun calculateTotFall(coordinates: List<CoordinatesWithAltitude>): Int {
        var fall = 0.0
        for (i in coordinates.indices) {
            if(i < coordinates.size - 1) {
                val currentPoint = coordinates[i]
                val nextPoint = coordinates[i + 1]
                if(isFall(currentPoint, nextPoint)) {
                    fall += getFall(currentPoint, nextPoint)
                }
            }
        }
        return abs(fall).toInt()
    }

    fun calculateEta(coordinates: List<CoordinatesWithAltitude>): Int {
        TODO("Not yet implemented")
    }

    private fun getFall(currentPoint: CoordinatesWithAltitude, nextPoint: CoordinatesWithAltitude) =
            getDifferenceInAltitude(currentPoint, nextPoint)

    private fun getRise(nextPoint: CoordinatesWithAltitude, currentPoint: CoordinatesWithAltitude) =
            getDifferenceInAltitude(nextPoint, currentPoint)

    private fun getDifferenceInAltitude(nextPoint: CoordinatesWithAltitude, currentPoint: CoordinatesWithAltitude) =
            abs(nextPoint.altitude - currentPoint.altitude)

    private fun isRise(currentPoint: CoordinatesWithAltitude,
                       nextPoint: CoordinatesWithAltitude) = currentPoint.altitude < nextPoint.altitude

    private fun isFall(currentPoint: CoordinatesWithAltitude,
                       nextPoint: CoordinatesWithAltitude): Boolean = currentPoint.altitude > nextPoint.altitude

}
