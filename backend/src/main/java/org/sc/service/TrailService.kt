package org.sc.service

import org.openapitools.model.CityRefDto
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
import org.sc.processor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.logging.Logger

private const val unknown = "unknown"
private const val intersection = "intersection"

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

        val municipalityToIntersectingPoints =
            trailIntersector.findIntersectionWithMunicipalities(trailMapper.map(trail))

        // Place points on trail and calculate distance
        // find indexes for inserting
        return calculateDistances(municipalityToIntersectingPoints, trail)
    }

    private fun mapToElevationCoord(it: Coordinates2D): CoordinatesDto {
        return CoordinatesDto(
            it.latitude,
            it.longitude,
            altitudeServiceAdapter.getElevationsByLongLat(it.longitude, it.latitude)[0]
        )
    }

    private fun calculateDistances(
        municipalityToIntersectingPoints: List<Pair<MunicipalityDto, List<Coordinates2D>>>,
        trail: TrailDto
    ): List<MunicipalityToTrailDto> {
        val municipalityToIntersectionEnriched = municipalityToIntersectingPoints
            .plus(getMunicipality(unknown))
            .plus(getMunicipality(intersection))
        val municipalityToDistance = mutableMapOf<String, Double>()
        val municipalities = municipalityToIntersectionEnriched.map {
            Pair(
                it.first.name,
                it.first.geometry.map { coord -> Coordinates2D(coord.longitude, coord.latitude) })
        }
        municipalities.forEach { municipalityToDistance[it.first] = 0.0 }

        trail.coordinates.forEachIndexed { index, coord ->

            if (index < trail.coordinates.size - 1) {
                val pointWithinPolygon = GeoCalculator.isPointWithinPolygon(
                    Coordinates2D(coord.longitude, coord.latitude),
                    municipalities
                )
                val nextCoord = trail.coordinates[index + 1]
                val nextPointWithinPolygon = GeoCalculator.isPointWithinPolygon(
                    Coordinates2D(nextCoord.longitude, nextCoord.latitude),
                    municipalities
                )

                // If both in same municipality
                if (pointWithinPolygon.first == nextPointWithinPolygon.first) {
                    val d: Double = municipalityToDistance[pointWithinPolygon.first]!!
                    municipalityToDistance[pointWithinPolygon.first] =
                        d + DistanceProcessor.distanceBetweenPoints(coord, nextCoord)
                // Intersection of known areas
                } else if (pointWithinPolygon.first != unknown && nextPointWithinPolygon.first != unknown) {
                    municipalityToDistance[intersection] =
                        municipalityToDistance[intersection]!! + DistanceProcessor.distanceBetweenPoints(coord, nextCoord)
                // Intersection with unknown areas
                } else {
                    municipalityToDistance[unknown] =
                        municipalityToDistance[unknown]!! + DistanceProcessor.distanceBetweenPoints(coord, nextCoord)
                }
            }


        }

        return municipalities.map { m ->
            val municipalityToTrailDto = municipalityToIntersectionEnriched.filter { it.first.name.equals(m.first) }[0]

            MunicipalityToTrailDto(
                municipalityToTrailDto.second.map { mapToElevationCoord(it) },
                municipalityToDistance[m.first],
                MunicipalityDetailsDto(
                    municipalityToTrailDto.first.id,
                    municipalityToTrailDto.first.name,
                    municipalityToTrailDto.first.relatingCity.province,
                    municipalityToTrailDto.first.relatingCity.provinceShort
                ),
                municipalityToTrailDto.first.geometry.map { c -> Coordinates2D(c.longitude, c.latitude) }
            )
        }
    }

    private fun getMunicipality(name: String): Pair<MunicipalityDto, List<Coordinates2D>> {
        val first = MunicipalityDto()
        first.name = name
        first.id = ""
        val cityRefDto = CityRefDto()
        cityRefDto.province = name
        cityRefDto.provinceShort = name
        first.relatingCity = cityRefDto
        first.geometry = emptyList()
        return Pair(first, emptyList())
    }


//        municipalityToIntersectingPoints.map {
//
//
//        // Got indexes to place points
//        val calculatedIndexesDistances = it.second.map { targetCoord ->
//            Pair(
//                targetCoord, trailsStatsCalculator.getLowestCumulativeDistanceAndIndexForCoordinate(
//                    trail.coordinates,
//                    CoordinatesDto(targetCoord.latitude, targetCoord.longitude)
//                )
//            )
//        }.requireNoNulls()
//
//
//        // Add points to construct a line including them
//        val coordinates = trail.coordinates
//        calculatedIndexesDistances.forEach { coordsToIndex ->
//            val index = coordsToIndex.second!!.second
//            val lat = coordsToIndex.first.latitude
//            val long = coordsToIndex.first.longitude
//            coordinates.add(
//                index, TrailCoordinatesDto(
//                    lat, long,
//                    altitudeServiceAdapter.getElevationsByLongLat(lat, long).first(), 0
//                )
//            )
//        }
//
//        val fromTo = calculatedIndexesDistances.map { coordsToDistanceAndIndex -> coordsToDistanceAndIndex.first }
//        val distance =
//            trailsStatsCalculator.calculateTrailLengthFromToPoint(coordinates, fromTo)
//        val intersectionPointsWithElevation = calculatedIndexesDistances.map { cx ->
//            coordinates[cx.second!!.second]
//        }
//
//

}