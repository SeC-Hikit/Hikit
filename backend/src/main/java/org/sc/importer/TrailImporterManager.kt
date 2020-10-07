package org.sc.importer

import org.sc.data.CoordinatesWithAltitude
import org.sc.data.TrailDistance
import org.sc.data.UnitOfMeasurement
import org.sc.manager.TrailManager
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager){

    fun calculateEta(coords: List<CoordinatesWithAltitude>) : Int {
        throw NotImplementedException()
    }

}