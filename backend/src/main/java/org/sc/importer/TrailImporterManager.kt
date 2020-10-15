package org.sc.importer

import org.sc.data.CoordinatesWithAltitude
import org.sc.data.Trail
import org.sc.manager.TrailManager
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager){

    fun calculateEta(coords: List<CoordinatesWithAltitude>) : Int {
        throw NotImplementedException()
    }

    fun calculateRise(coords: List<CoordinatesWithAltitude>) : Double {
        throw NotImplementedException()
    }

    fun calculateFall(coords: List<CoordinatesWithAltitude>) : Double {
        throw NotImplementedException()
    }

    fun save(trailRequest: Trail) {
        trailsManager.save(trailRequest, 0.0)
    }

}