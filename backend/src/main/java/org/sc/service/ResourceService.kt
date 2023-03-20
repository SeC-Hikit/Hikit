package org.sc.service

import org.sc.common.rest.TrailDto
import org.sc.data.model.StaticTrailDetails
import org.sc.manager.*
import org.sc.processor.TrailSimplifierLevel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class ResourceService @Autowired constructor(
        private val trailManager: TrailManager,
        private val resourceManager: ResourceManager,
        private val maintenanceManager: MaintenanceManager,
        private val accessibilityNotificationManager: AccessibilityNotificationManager,
        private val placeManager: PlaceManager,
        private val trailFileManager: TrailFileManager) {

    val isJobRunning = AtomicBoolean(false)

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute() {
        if (!isJobRunning.get()) {
            logger.trace("Resource generation Job is not running. Executing...")
            isJobRunning.set(true)
            generateResources()
            logger.trace("Resource generation Job completed.")
            isJobRunning.set(false)
        }
        logger.trace("Previous resource generation job is still running...")
    }

    private fun generateResources() {
        val entries = resourceManager.getTrailEntries()
        val distinctEntries = entries.distinctBy { it.targetingTrail }
        distinctEntries.forEach {
            val trailList = trailManager.getById(it.targetingTrail, TrailSimplifierLevel.LOW)
            if(trailList.isNotEmpty()) {
                val targetTrail = trailList.first()
                logger.info("Ri-generating resource for trail with id: $targetTrail")
                generatePdfFile(targetTrail)
            }
            logger.trace("Trail '${it.targetingTrail}' seems be removed while waiting for jobs to complete. Skipping...")

        }
        logger.trace("Resolved n.${distinctEntries.size} entries for processing")
        resourceManager.deleteEntries(entries)
    }

    private fun generatePdfFile(trailSaved: TrailDto) {
        val trailId = trailSaved.id
        val places = trailSaved.locations.flatMap { placeManager.getById(it.placeId) }
        val maintenancesByTrailId = maintenanceManager.getPastMaintenanceForTrailId(trailId, 0, Int.MAX_VALUE)
        val lastMaintenance = maintenancesByTrailId.maxByOrNull { it.date }
        val openIssues = accessibilityNotificationManager.getUnresolvedByTrailId(trailId, 0, Int.MAX_VALUE)
        logger.info("Generating PDF file for trail '$trailId'")
        val resolvedName = trailFileManager.writeTrailToPdf(
            trailSaved,
            places,
            listOfNotNull(lastMaintenance),
            openIssues,
            fileName = trailFileManager.getFilename(trailSaved)
        )
        trailManager.updateStaticResources(
            trailId, StaticTrailDetails(
            trailSaved.staticTrailDetails.pathGpx,
            trailSaved.staticTrailDetails.pathKml,
            resolvedName)
        )
    }

}