package org.sc.processor

import org.sc.common.rest.TrailDto
import org.sc.manager.PlaceManager
import org.sc.manager.TrailManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlacesTrailSyncProcessor @Autowired constructor(private val trailManager: TrailManager,
                                                      private val placeManager: PlaceManager) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun populatePlacesWithTrailData(trailSaved: TrailDto) {
        trailSaved.locations.map {
            logger.info("Connecting place with Id '${it.placeId}' to newly created trail with Id '${trailSaved.id}'")
            trailManager.linkTrailToPlace(trailSaved.id, it)
            // Reload place
            val updatedPlace = placeManager.getById(it.placeId).first()
            updatedPlace.crossingTrailIds.filter { encounteredTrail -> encounteredTrail.equals(trailSaved.id) }
                    .forEach { encounteredTrailNotTrailSaved ->
                        run {
                            logger.info("Ensuring also place with Id '${it.placeId}' " +
                                    "to other existing trail with Id '${encounteredTrailNotTrailSaved}'")
                            trailManager.linkTrailToPlace(encounteredTrailNotTrailSaved, it)
                        }
                    }
        }
    }

}