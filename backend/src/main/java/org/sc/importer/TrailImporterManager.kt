package org.sc.importer

import org.sc.data.StatsTrailMetadata
import org.sc.data.Trail
import org.sc.data.TrailImport
import org.sc.manager.TrailManager
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager,
                                               private val trailsCalculator : TrailsCalculator){

    fun save(importingTrail: TrailImport) {

        val statsTrailMetadata = StatsTrailMetadata(
                trailsCalculator.calculateEta(importingTrail.coordinates),
                trailsCalculator.calculateTotFall(importingTrail.coordinates),
                trailsCalculator.calculateTotRise(importingTrail.coordinates),
                trailsCalculator.calculateTrailLength(importingTrail.coordinates));

        val trail = Trail(importingTrail.name,
                importingTrail.description,
                importingTrail.code,
                importingTrail.startPos,
                importingTrail.finalPos,
                importingTrail.classification,
                importingTrail.country,
                statsTrailMetadata,
                importingTrail.coordinates,
                importingTrail.date,
                importingTrail.maintainingSection)

        trailsManager.save(trail);
    }

}