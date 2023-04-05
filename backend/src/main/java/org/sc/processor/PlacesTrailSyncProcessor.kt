package org.sc.processor

import org.sc.common.rest.PlaceDto
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


            val updatedPlace = placeManager.getById(it.placeId).first()
            updatedPlace.crossingTrailIds
//                .filter { encounteredTrail -> encounteredTrail.equals(trailSaved.id) }
                    .forEach { encounteredTrailNotTrailSaved ->
                        run {
                            logger.info("Ensuring also place with Id '${it.placeId}' " +
                                    "to other existing trail with Id '${encounteredTrailNotTrailSaved}'")
                            trailManager.linkTrailToPlace(encounteredTrailNotTrailSaved, it)
                        }
                    }

            if (it.isDynamicCrossway) {
                updateDynamicCrosswayNameWithTrailsPassingCodes(it.placeId)
            }
        }
    }

    fun ensureEmptyDynamicCrosswayDeletion(placeId: String) {
        val placeList = placeManager.getById(placeId)
        if (placeList.isEmpty()) {
            logger.error("Cannot find dynamic crossway ${placeId} for ensuring deletion)")
            return
        }
        val place = placeList.first()
        if(isEmptyDynamicCrossway(place)){
            placeManager.deleteById(place.id)
        }
    }

    fun updateDynamicCrosswayNameWithTrailsPassingCodes(placeId: String) {
        val placeList = placeManager.getById(placeId)
        if (placeList.isEmpty()) {
            logger.warn("Cannot update dynamic crossway name of a not-existing location (id=${placeId})")
            return
        }
        if (placeList.any { !it.isDynamic }) {
            logger.warn("Cannot update no-auto place (id=${placeId})")
            return
        }
        val place = placeList.first()
        val placeCrossingTrailsNames =
                trailManager.getCodesByTrailIds(place.crossingTrailIds).joinToString(", ")
        val updatedName = "Crocevia $placeCrossingTrailsNames";
        logger.info("Updating place with id ${place.id} with previous name='${place.name}' with new name='$updatedName'")
        place.name = updatedName
        placeManager.update(place)
        // update trails places references
        place.crossingTrailIds.forEach { trailManager.updateTrailPlaceNamesReference(it, placeId, updatedName)}
    }

    private fun isEmptyDynamicCrossway(place: PlaceDto) =
            place.isDynamic && place.crossingTrailIds.isEmpty()

}