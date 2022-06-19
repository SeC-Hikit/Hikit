package org.sc.service

import org.sc.common.rest.PlaceRefDto
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

    fun deleteById(trailId: String): List<TrailDto> {
        val deletedTrails = trailManager.deleteById(trailId)
        if (deletedTrails.isEmpty()) throw IllegalStateException()
        val deletedTrail = deletedTrails.first()
        maintenanceManager.deleteByTrailId(trailId)
        accessibilityNotificationManager.deleteByTrailId(trailId)
        placeManager.deleteTrailReference(deletedTrail.id, deletedTrail.locations)
        updateDynamicCrosswayNamesForTrail(deletedTrail)
        ensureDeletionForDynamicEmptyCrossway(deletedTrail)
        poiManager.deleteTrailReference(deletedTrail.id)
        logger.info("Purge deleting trail $trailId")
        return deletedTrails
    }

    private fun ensureDeletionForDynamicEmptyCrossway(deletedTrail: TrailDto) {
        deletedTrail.locations.forEach {
            if (it.isDynamicCrossway) placesTrailSyncProcessor.ensureEmptyDynamicCrosswayDeletion(it.placeId)
        }
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
        updateDynamicCrosswayNamesForTrail(trailToUpdate)
        return trailManager.update(trailMapper.map(trailToUpdate))
    }

    fun unlinkPlace(trailId: String,
                    placeRef: PlaceRefDto): List<TrailDto> {
        val unLinkPlace = trailManager.unlinkPlace(trailId, placeRef)
        if (placeRef.isDynamicCrossway)
            placesTrailSyncProcessor.updateDynamicCrosswayNameWithTrailsPassingCodes(placeRef.placeId)
        return unLinkPlace
    }

    fun linkTrailToPlace(id: String, placeRef: PlaceRefDto): List<TrailDto> {
        val linkedPlaces = trailManager.linkTrailToPlace(id, placeRef)
        if (placeRef.isDynamicCrossway)
            placesTrailSyncProcessor.updateDynamicCrosswayNameWithTrailsPassingCodes(placeRef.placeId)
        return linkedPlaces
    }

    private fun updateDynamicCrosswayNamesForTrail(trailToUpdate: TrailDto) {
        trailToUpdate.locations.forEach {
            if (it.isDynamicCrossway) placesTrailSyncProcessor.updateDynamicCrosswayNameWithTrailsPassingCodes(it.placeId)
        }
    }

    private fun isSwitchingToDraft(
            trailDto: TrailDto,
            trailToUpdate: TrailDto
    ) = trailDto.status == TrailStatus.DRAFT &&
            trailToUpdate.status == TrailStatus.PUBLIC


}