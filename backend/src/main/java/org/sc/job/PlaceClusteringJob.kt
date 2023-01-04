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
class PlaceClusteringJob @Autowired constructor(
        private val placeManager: PlaceManager,
        private val trailManager: TrailManager,
        private val appProperties: AppProperties) {

    private val logger = LogManager.getLogger(PlaceClusteringJob::class.java)

    companion object {
        const val batchSize = 2
    }

    @Scheduled(fixedRate = 180_000, initialDelay = 180_000)
    fun ensurePlacesConsistency() {
        logger.info("Starting clustering job for non-dynamic places in instance")
        val countPlaces = placeManager.countByRealm(appProperties.instanceRealm, false)
        val clusteredInThisRun = mutableListOf<Place>()

        var i = 0;
        while (i < countPlaces) {
            val latest = placeManager.getOldestPaginated(i, batchSize,
                    false, appProperties.instanceRealm)

            latest.forEach { subjectPlace ->
                val nearestMatches =
                        placeManager.findNearestMatchByCoordinatesExcludingById(
                                subjectPlace.id, subjectPlace.coordinates,
                                appProperties.jobCrosswayConsistencyDistance,
                                appProperties.instanceRealm
                        )
                val clusteringPlaces: List<Place> = nearestMatches
                        .filter { !clusteredInThisRun.any { it.id.equals(subjectPlace.id) } }
                        .filter { match -> match.name.lowercase().startsWith(subjectPlace.name.lowercase()) }
                        .filter { it.coordinates.size >= subjectPlace.coordinates.size }
                        .filter { subjectPlace.recordDetails.uploadedOn.before(it.recordDetails.uploadedOn) }

                clusterElectedSpaces(subjectPlace, clusteringPlaces)

                if (clusteringPlaces.isNotEmpty()) {
                    clusteredInThisRun.addAll(clusteringPlaces)
                }
            }

            i += batchSize
        }

        logger.info("Clustering job for non-dynamic places over, " +
                "places which were clustered and are going to " +
                "be removed: ${clusteredInThisRun.map { it.id }}")
        clusteredInThisRun.forEach {
            placeManager.deleteById(it.id)
        }
    }

    private fun clusterElectedSpaces(subjectPlace: PlaceDto, clusteringPlaces: List<Place>) {
        clusteringPlaces.forEach { placeForClustering ->
            logger.info("Going to cluster NOT DYNAMIC places '${subjectPlace.id}' with '${placeForClustering.id}'")

            updateTrailWithPlaceReferences(
                    placeForClustering.id,
                    subjectPlace.id,
                    subjectPlace.name)
            updatePlaceWithTrailReferences(
                    subjectPlace.id,
                    placeForClustering.crossingTrailIds)
            updatePlacePositions(subjectPlace.id, placeForClustering.id)
        }
    }

    private fun updatePlacePositions(placeId: String, placeIdToBeMerged: String) {
        placeManager.mergePlacePositions(placeId, placeIdToBeMerged)
    }

    private fun updatePlaceWithTrailReferences(id: String, crossingTrailIds: List<String>) {
        placeManager.addNotExistingTrailsIdToPlaceId(id, crossingTrailIds)
    }

    private fun updateTrailWithPlaceReferences(placeIdToBeMerged: String, id: String, name: String) {
        trailManager.updateTrailPlaceReferences(placeIdToBeMerged, id, name)
    }
}