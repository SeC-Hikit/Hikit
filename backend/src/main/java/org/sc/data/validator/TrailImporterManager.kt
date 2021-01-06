package org.sc.data.validator

import org.sc.common.rest.StatsTrailMetadata
import org.sc.common.rest.Trail
import org.sc.data.TrailDatasetVersionDao
import org.sc.data.TrailImport
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailImporterManager @Autowired constructor(private val trailsManager : TrailManager,
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
                importingTrail.locations,
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