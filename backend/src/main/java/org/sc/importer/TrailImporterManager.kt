package org.sc.importer

import org.sc.data.CoordinatesWithAltitude
import org.sc.data.TrailDistance
import org.sc.data.UnitOfMeasurement
import org.sc.manager.TrailManager
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager){

    fun findPossibleConnectingWayPoints(coordinatesWithAltitudes: List<CoordinatesWithAltitude>): List<TrailDistance> {
        val trailDistancesWithinRangeAtPoint = arrayListOf<TrailDistance>()
        coordinatesWithAltitudes.forEach {
            trailDistancesWithinRangeAtPoint.addAll(trailsManager.getTrailDistancesWithinRangeAtPoint(it, 200, UnitOfMeasurement.m, 10))
        }
        return trailDistancesWithinRangeAtPoint
    }


}