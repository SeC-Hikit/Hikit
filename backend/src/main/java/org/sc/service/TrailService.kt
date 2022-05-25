package org.sc.service

import org.sc.common.rest.TrailDto
import org.sc.data.mapper.TrailMapper
import org.sc.data.model.TrailStatus
import org.sc.manager.*
import org.sc.processor.PlacesTrailSyncProcessor
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class TrailService @Autowired constructor(private val trailManager: TrailManager,
                                          private val maintenanceManager: MaintenanceManager,
                                          private val accessibilityNotificationManager: AccessibilityNotificationManager,
                                          private val placeManager: PlaceManager,
                                          private val placesTrailSyncProcessor: PlacesTrailSyncProcessor,
                                          private val poiManager: PoiManager,
                                          private val trailMapper: TrailMapper) {

    private val logger = Logger.getLogger(TrailManager::class.java.name)

    fun deleteById(id: String): List<TrailDto> {
        val deletedTrails = trailManager.deleteById(id)
        if (deletedTrails.isEmpty()) throw IllegalStateException()
        val deletedTrail = deletedTrails.first()
        maintenanceManager.deleteByTrailId(id)
        accessibilityNotificationManager.deleteByTrailId(id)
        placeManager.deleteTrailReference(deletedTrail.id, deletedTrail.locations)
        poiManager.deleteTrailReference(deletedTrail.id)
        logger.info("Purge deleting trail $id")
        return deletedTrails
    }

    fun switchToStatus(trailDto: TrailDto): List<TrailDto> {
        val trailToUpdate = trailManager.getById(trailDto.id, TrailSimplifierLevel.LOW).first()

        if (trailDto.status == trailToUpdate.status) {
            logger.info("Did not change status to trail ${trailDto.id}")
            return trailManager.update(trailMapper.map(trailToUpdate))
        }
        // Turn PUBLIC -> DRAFT
        if (isSwitchingToDraft(trailDto, trailToUpdate)) {
            logger.info("""Trail ${trailToUpdate.code} -> ${TrailStatus.DRAFT}""")
            trailManager.propagateChangesToTrails(trailDto.id)
            trailDto.locations.forEach {
                placeManager.unlinkTrailFromPlace(it.placeId, trailDto.id, it.coordinates)
            }
            // DRAFT -> PUBLIC
        } else {
            logger.info("""Trail ${trailToUpdate.code} -> ${TrailStatus.PUBLIC}""")
            placesTrailSyncProcessor.populatePlacesWithTrailData(trailDto)
        }
        trailToUpdate.status = trailDto.status
        return trailManager.update(trailMapper.map(trailToUpdate))
    }

    private fun isSwitchingToDraft(
            trailDto: TrailDto,
            trailToUpdate: TrailDto
    ) = trailDto.status == TrailStatus.DRAFT &&
            trailToUpdate.status == TrailStatus.PUBLIC


}