package org.sc.importer

import org.sc.common.rest.controller.StatsTrailMetadata
import org.sc.common.rest.controller.Trail
import org.sc.data.TrailDatasetVersionDao
import org.sc.data.TrailImport
import org.sc.manager.TrailManager
import java.util.*
import javax.inject.Inject

class TrailImporterManager @Inject constructor(private val trailsManager : TrailManager,
                                               private val trailsCalculator : TrailsCalculator,
                                               private val trailDatasetVersionDao: TrailDatasetVersionDao){

    fun save(importingTrail: TrailImport) {

        val statsTrailMetadata = StatsTrailMetadata(
                trailsCalculator.calculateTotRise(importingTrail.coordinates),
                trailsCalculator.calculateTotFall(importingTrail.coordinates),
                trailsCalculator.calculateEta(importingTrail.coordinates),
                trailsCalculator.calculateTrailLength(importingTrail.coordinates))

        val trail = Trail(importingTrail.name,
                importingTrail.description,
                importingTrail.code,
                importingTrail.startPos,
                importingTrail.finalPos,
                importingTrail.classification,
                importingTrail.country,
                statsTrailMetadata,
                importingTrail.coordinates,
                Date(),
                importingTrail.maintainingSection)

        trailsManager.save(trail)
        trailDatasetVersionDao.increaseVersion()
    }

}