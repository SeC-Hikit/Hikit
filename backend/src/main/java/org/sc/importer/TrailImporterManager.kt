package org.sc.importer

import org.sc.data.Trail
import org.sc.manager.TrailManager
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager,
                                               private val trailsCalculator : TrailsCalculator){

    fun save(importingTrail: Trail) {
        trailsManager.save(importingTrail,
                trailsCalculator.calculateEta(importingTrail.coordinates),
                trailsCalculator.calculateTotFall(importingTrail.coordinates),
                trailsCalculator.calculateTotRise(importingTrail.coordinates),
                trailsCalculator.calculateTrailLength(importingTrail.coordinates))
    }

}