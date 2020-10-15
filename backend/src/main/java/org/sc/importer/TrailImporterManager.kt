package org.sc.importer

import org.sc.data.Trail
import org.sc.manager.TrailManager
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager, private val trailsCalculator : TrailsCalculator){

    fun save(trailRequest: Trail) {
        trailsManager.save(trailRequest, trailsCalculator.calculateEta(trailRequest.coordinates), trailsCalculator.calculateTotFall(trailRequest.coordinates),
                trailsCalculator.calculateTotRise(trailRequest.coordinates))
    }

}