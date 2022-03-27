package org.sc.service

import org.sc.common.rest.TrailDto
import org.sc.manager.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.logging.Logger

@Service
class TrailService @Autowired constructor(private val trailManager: TrailManager,
                                          private val maintenanceManager: MaintenanceManager,
                                          private val accessibilityNotificationManager: AccessibilityNotificationManager,
                                          private val placeManager: PlaceManager,
                                          private val poiManager: PoiManager) {

    private val logger = Logger.getLogger(TrailManager::class.java.name)

    fun deleteById(id: String): List<TrailDto> {
        val deletedTrails = trailManager.deleteById(id)
        if(deletedTrails.isEmpty()) throw IllegalStateException()
        val deletedTrail = deletedTrails.first()
        maintenanceManager.deleteByTrailId(id)
        accessibilityNotificationManager.deleteByTrailId(id)
        placeManager.deleteTrailReference(deletedTrail.id, deletedTrail.locations)
        poiManager.deleteTrailReference(deletedTrail.id)
        logger.info("Purge deleting trail $id")
        return deletedTrails
    }
}