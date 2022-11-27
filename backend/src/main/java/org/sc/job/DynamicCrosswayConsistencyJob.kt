package org.sc.job

import org.sc.manager.PlaceManager
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DynamicCrosswayConsistencyJob @Autowired constructor(private val placeManager: PlaceManager,
                                                private val trailManager: TrailManager) {

    @Scheduled(fixedRate = 60_000)
    fun doEnsureDynamicCrosswayConsistency() {
        val latest = placeManager.getLatestPaginated(0, Int.MAX_VALUE, true)

        latest.forEach {
            val nearest = placeManager.findNearestByCoordinatesExcludingById(it.id, it.coordinates, 20.0)
            if (nearest.isNotEmpty()) {
                val placeWithMorePassingTrailsNearby = nearest.maxByOrNull { nears -> nears.crossingTrailIds.size }!!
                if (placeWithMorePassingTrailsNearby.crossingTrailIds.size > it.crossingTrailIds.size) {
                    // TODO assign all trails passing ids to the other place, update all previously connected trails.
                }
            }
        }
        throw NotImplementedError()
    }
}