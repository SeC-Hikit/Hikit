package org.sc.job

import org.apache.logging.log4j.LogManager
import org.sc.common.rest.PlaceDto
import org.sc.configuration.AppProperties
import org.sc.data.model.Place
import org.sc.manager.PlaceManager
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DynamicCrosswayConsistencyJob @Autowired constructor(
        private val placeManager: PlaceManager,
        private val trailManager: TrailManager,
        private val appProperties: AppProperties) {

    private val logger = LogManager.getLogger(CompressImageJob::class.java)

    @Scheduled(fixedRate = 60_000)
    fun doEnsureDynamicCrosswayConsistency() {
        val latest = placeManager.getLatestPaginated(0, Int.MAX_VALUE, false)

        latest.forEach {
            val nearestMatches = placeManager.findNearestMatchByCoordinatesExcludingById(
                    it.id, it.coordinates,
                    appProperties.jobCrosswayConsistencyDistance
            )
            if (nearestMatches.any { match -> match.name.lowercase().startsWith(it.name.lowercase()) }) {
                val placeWithMorePassingTrailsNearby = nearestMatches.maxByOrNull { nears -> nears.crossingTrailIds.size }!!
                if (isNearbyPlaceGreaterCrossway(placeWithMorePassingTrailsNearby, it)) {
                    // assign all trails passing ids to the other place, update all previously connected trails.
                    logger.info("Going to merge NOT DYNAMIC places '${it.id}' with '${placeWithMorePassingTrailsNearby.id}'")
                    updateTrailReferences(it.id,
                            placeWithMorePassingTrailsNearby.id,
                            placeWithMorePassingTrailsNearby.name)
                    updatePlaceWithTrailReferences(
                            placeWithMorePassingTrailsNearby.id,
                            it.crossingTrailIds)
                    placeManager.deleteById(it.id)
                }
            }
        }
    }

    private fun isNearbyPlaceGreaterCrossway(placeWithMorePassingTrailsNearby: Place, it: PlaceDto) =
            placeWithMorePassingTrailsNearby.crossingTrailIds.size >= it.crossingTrailIds.size

    private fun updatePlaceWithTrailReferences(id: String, crossingTrailIds: List<String>) {
        placeManager.addNotExistingTrailsIdToPlaceId(id, crossingTrailIds)
    }

    private fun updateTrailReferences(oldId: String, id: String, name: String) {
        trailManager.updateTrailPlaceReferences(oldId, id, name)
    }
}