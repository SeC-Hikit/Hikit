package org.sc.service

import org.openapitools.model.MunicipalityDto
import org.sc.adapter.AltitudeServiceAdapter
import org.sc.adapter.microservice.ErtMunicipalityMicroserviceAdapter
import org.sc.common.rest.*
import org.sc.common.rest.geo.LocateDto
import org.sc.data.mapper.TrailMapper
import org.sc.data.model.Coordinates
import org.sc.data.model.Coordinates2D
import org.sc.data.model.MunicipalityDetails
import org.sc.data.model.TrailStatus
import org.sc.job.import.MunicipalityForTrailsImporter
import org.sc.manager.*
import org.sc.processor.PlacesTrailSyncProcessor
import org.sc.processor.TrailSimplifierLevel
import org.sc.processor.TrailsStatsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class TrailService @Autowired constructor(
    private val trailManager: TrailManager,
    private val maintenanceManager: MaintenanceManager,
    private val accessibilityNotificationManager: AccessibilityNotificationManager,
    private val placeManager: PlaceManager,
    private val placesTrailSyncProcessor: PlacesTrailSyncProcessor,
    private val poiManager: PoiManager,
    private val trailMapper: TrailMapper,
    private val municipalityForTrailsImporter: MunicipalityForTrailsImporter,
    private val trailIntersector: TrailIntersectionManager,
    private val trailsStatsCalculator: TrailsStatsCalculator,
    private val altitudeServiceAdapter: AltitudeServiceAdapter,
    private val municipalityMicroserviceAdapter: ErtMunicipalityMicroserviceAdapter,
) {

    private val logger = Logger.getLogger(TrailService::class.java.name)

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

    fun unlinkPlace(
        trailId: String,
        placeRef: PlaceRefDto
    ): List<TrailDto> {
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

    private fun ensureDeletionForDynamicEmptyCrossway(deletedTrail: TrailDto) {
        deletedTrail.locations.forEach {
            if (it.isDynamicCrossway) placesTrailSyncProcessor.ensureEmptyDynamicCrosswayDeletion(it.placeId)
        }
    }

    fun findTrailsWithinSearchArea(
        locateRequest: LocateDto,
        level: TrailSimplifierLevel,
        isDraftTrailVisible: Boolean
    ): List<TrailDto> =
        trailManager.findTrailsWithinRectangle(
            locateRequest.rectangleDto.bottomLeft,
            locateRequest.rectangleDto.topRight,
            locateRequest.trailIdsNotToLoad,
            level, isDraftTrailVisible
        )

    fun findMunicipalityForTrailCoordinates(coordinates: List<Coordinates>): List<MunicipalityDetails> =
        municipalityForTrailsImporter.findMunicipalities(coordinates)

    fun intersectMunicipalitiesCalculatingDistances(trailId: String): List<MunicipalityToTrailDto> {
        val byId = trailManager.getById(trailId, TrailSimplifierLevel.HIGH)
        val trail = byId.first()


        // In case only one municipality, return the whole distance
        if (trail.municipalities.size == 1) {

            val municipality = municipalityMicroserviceAdapter.getByName(trail.municipalities.first().city)
            val municipalityFull = municipality!!.body!!.content.first()
            return listOf(
                MunicipalityToTrailDto(
                    emptyList(),
                    trail.statsTrailMetadata.length, trail.municipalities.first(),
                    municipalityFull.geometry.map { Coordinates2D(it.longitude, it.latitude) }
                )
            )
        }

        val municipalityToIntersectingPoints =
            trailIntersector.findIntersectionWithMunicipalities(trailMapper.map(trail))

        // Place points on trail and calculate distance
        // find indexes for inserting
        return calculateDistances(municipalityToIntersectingPoints, trail)
    }

    private fun calculateDistances(
        municipalityToIntersectingPoints: List<Pair<MunicipalityDto, List<Coordinates2D>>>,
        trail: TrailDto
    ): List<MunicipalityToTrailDto> = municipalityToIntersectingPoints.map {
        // Got indexes to place points
        val calculatedIndexesDistances = it.second.map { targetCoord ->
            Pair(
                targetCoord, trailsStatsCalculator.getLowestCumulativeDistanceAndIndexForCoordinate(
                    trail.coordinates,
                    CoordinatesDto(targetCoord.latitude, targetCoord.longitude)
                )
            )
        }.requireNoNulls()


        // Add points to construct a line including them
        val coordinates = trail.coordinates
        calculatedIndexesDistances.forEach { coordsToIndex ->
            val index = coordsToIndex.second!!.second
            val lat = coordsToIndex.first.latitude
            val long = coordsToIndex.first.longitude
            coordinates.add(
                index, TrailCoordinatesDto(
                    lat, long,
                    altitudeServiceAdapter.getElevationsByLongLat(lat, long).first(), 0
                )
            )
        }

        val fromTo = calculatedIndexesDistances.map { coordsToDistanceAndIndex -> coordsToDistanceAndIndex.first }
        val distance =
            trailsStatsCalculator.calculateTrailLengthFromToPoint(coordinates, fromTo)
        val intersectionPointsWithElevation = calculatedIndexesDistances.map { cx ->
            coordinates[cx.second!!.second]
        }

        MunicipalityToTrailDto(
            intersectionPointsWithElevation.map { c ->
                CoordinatesDto(c.latitude, c.longitude, c.altitude)
            },
            distance,
            MunicipalityDetailsDto(
                it.first.id,
                it.first.name,
                it.first.relatingCity.province,
                it.first.relatingCity.provinceShort
            ),
            it.first.geometry.map { c -> Coordinates2D(c.longitude, c.latitude) }
        )
    }
}