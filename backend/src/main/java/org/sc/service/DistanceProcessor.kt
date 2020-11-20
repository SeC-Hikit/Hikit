package org.sc.service

import org.sc.common.rest.controller.CoordinatesWithAltitude
import kotlin.math.*

object DistanceProcessor {

    private const val earthRadius = 6378.137 // Radius of earth in KM

    /**
     * Returns the distance between two points in meters
     */
    fun distanceBetweenPoints(position: CoordinatesWithAltitude, toPoint: CoordinatesWithAltitude): Double =
            distance(position.latitude, toPoint.latitude,
                    position.longitude, toPoint.longitude,
                    position.altitude, toPoint.altitude)

    fun getRadialDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {  // generally used geo measurement function
        val dLat = lat2 * PI / 180 - lat1 * PI / 180
        val dLon = lon2 * PI / 180 - lon1 * PI / 180
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * PI / 180) * cos(lat2 * PI / 180) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val d = earthRadius * c
        return d * 1000 // meters
    }

    fun distance(lat1: Double, lat2: Double, lon1: Double,
                 lon2: Double, alt1: Double, alt2: Double): Double {
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = earthRadius * c * 1000 // convert to meters
        val height = alt1 - alt2
        distance = distance.pow(2.0) + height.pow(2.0)
        return sqrt(distance)
    }
}