package org.sc.manager

import org.sc.common.rest.*
import org.sc.configuration.auth.AuthFacade
import org.sc.configuration.auth.AuthHelper
import org.sc.data.mapper.*
import org.sc.data.model.*
import org.sc.data.repository.TrailDatasetVersionDao
import org.sc.data.repository.TrailRawDAO
import org.sc.processor.DistanceProcessor
import org.sc.processor.TrailSimplifier
import org.sc.processor.TrailSimplifierLevel
import org.sc.processor.TrailsStatsCalculator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailImporterService @Autowired constructor(
        private val trailsManager: TrailManager,
        private val trailFileManager: TrailFileManager,
        private val placeManager: PlaceManager,
        private val accessibilityNotificationManager: AccessibilityNotificationManager,
        private val maintenanceManager: MaintenanceManager,
        private val trailsStatsCalculator: TrailsStatsCalculator,
        private val trailDatasetVersionDao: TrailDatasetVersionDao,
        private val trailCoordinatesMapper: TrailCoordinatesMapper,
        private val coordinatesMapper: CoordinatesMapper,
        private val trailRawMapper: TrailRawMapper,
        private val trailRawDao: TrailRawDAO,
        private val trailMapper: TrailMapper,
        private val authFacade: AuthFacade,
        private val trailSimplifier: TrailSimplifier
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun saveRaw(trailRaw: TrailRawDto): TrailRawDto =
            trailRawDao.createRawTrail(trailRawMapper.map(trailRaw)).map { trailRawMapper.map(it) }
                    .first()

    fun save(importingTrail: TrailImportDto): List<TrailDto> {
        val statsTrailMetadata = StatsTrailMetadata(
                trailsStatsCalculator.calculateTotRise(importingTrail.coordinates),
                trailsStatsCalculator.calculateTotFall(importingTrail.coordinates),
                trailsStatsCalculator.calculateEta(importingTrail.coordinates),
                trailsStatsCalculator.calculateTrailLength(importingTrail.coordinates),
                trailsStatsCalculator.calculateHighestPlace(importingTrail.coordinates),
                trailsStatsCalculator.calculateLowestPlace(importingTrail.coordinates)
        )

        val createdOn = Date()

        // Create places in case they did not exist
        val authHelper = authFacade.authHelper

        logger.info("Creating or retrieving places for trail import...")

        val placesLocations: List<PlaceDto> = getLocationsWithInMemChangesFromPlacesRef(importingTrail.locations, authHelper)
        val trailCrosswaysFromLocations: List<PlaceDto> = getLocationsWithInMemChangesFromPlacesRef(importingTrail.crossways, authHelper)

        logger.info("Reordering places in memory...")
        val coordinates = importingTrail.coordinates.map { trailCoordinatesMapper.map(it) }
        val placesInOrder: List<PlaceRef> =
                getSortedIntermediateLocations(importingTrail.coordinates,
                        placesLocations.map { PlaceRef(it.name,
                                coordinatesMapper.map(it.coordinates.last()), it.id, it.crossingTrailIds) },
                        trailCrosswaysFromLocations.map { PlaceRef(it.name,
                                coordinatesMapper.map(it.coordinates.last()), it.id, it.crossingTrailIds) }
                )

        logger.info("Simplifying trail data...")
        val simplifyLowQuality = trailSimplifier.simplify(coordinates, TrailSimplifierLevel.LOW)
        val simplifyMediumQuality = trailSimplifier.simplify(coordinates, TrailSimplifierLevel.MEDIUM)
        val simplifyHighQuality = trailSimplifier.simplify(coordinates, TrailSimplifierLevel.HIGH)

        logger.info("Saving the trail...")
        val trail = Trail.builder()
                .name(importingTrail.name)
                .startLocation(placesInOrder.first())
                .endLocation(placesInOrder.last())
                .description(importingTrail.description)
                .officialEta(importingTrail.officialEta)
                .code(importingTrail.code)
                .variant(importingTrail.isVariant)
                .locations(placesInOrder)
                .classification(importingTrail.classification)
                .country(importingTrail.country)
                .statsTrailMetadata(statsTrailMetadata)
                .coordinates(coordinates)
                .coordinatesLow(simplifyLowQuality)
                .coordinatesMedium(simplifyMediumQuality)
                .coordinatesHigh(simplifyHighQuality)
                .lastUpdate(createdOn)
                .maintainingSection(importingTrail.maintainingSection)
                .territorialDivision(importingTrail.territorialDivision)
                .geoLineString(GeoLineString(importingTrail.coordinates.map {
                    Coordinates2D(
                            it.longitude,
                            it.latitude
                    )
                }))
                .cycloDetails(
                        CycloDetails(
                                CycloClassification.UNCLASSIFIED, 0,
                                CycloFeasibility(true, 0),
                                CycloFeasibility(true, 0), ""
                        )
                )
                .mediaList(emptyList())
                .fileDetails(
                        FileDetails(
                                importingTrail.fileDetailsDto.uploadedOn,
                                authHelper.username,
                                authHelper.instance,
                                authHelper.realm,
                                importingTrail.fileDetailsDto.filename,
                                importingTrail.fileDetailsDto.originalFilename,
                                authHelper.username
                        )
                )
                .status(importingTrail.trailStatus)
                .build()

        val savedTrailAsList = trailsManager.save(trail)
        if(savedTrailAsList.isEmpty()) {
            logger.warn("Something went wrong with saving trail data, rolling back...")
            // TODO rollback

        }

        val trailSaved = savedTrailAsList.first()
        logger.info("Linking places to trail...")
        updatePlacesWithSavedTrail(trailSaved)

        logger.info("Generating static resources for trail...")
        updateResourcesForTrail(trailSaved)

        logger.info("Updating trail set version...")
        trailDatasetVersionDao.increaseVersion()

        logger.info("Done importing trail.")
        return savedTrailAsList
    }

    private fun updatePlacesWithSavedTrail(trailSaved: TrailDto) {
        trailSaved.locations.map {
            logger.info("Connecting place with Id '${it.placeId}' to newly created trail with Id '${trailSaved.id}'")
            trailsManager.linkPlace(trailSaved.id, it)
        }
    }

    fun updateResourcesForTrail(savedTrail: TrailDto) {
        trailFileManager.writeTrailToOfficialGpx(savedTrail)
        trailFileManager.writeTrailToKml(savedTrail)
        generatePdfFile(savedTrail)
    }

    fun updateTrail(trailDto: TrailDto): List<TrailDto> {

        val trailToUpdate = trailsManager.getById(trailDto.id, TrailSimplifierLevel.LOW).first()

        val removedPlacesOnTrail = trailToUpdate.locations.filterNot { trailDto.locations.contains(it) }
        val addedPlacesOnTrail = trailToUpdate.locations.filterNot { trailDto.locations.contains(it) }
        removedPlacesOnTrail.forEach { trailsManager.unlinkPlace(trailDto.id, it) }
        addedPlacesOnTrail.forEach { trailsManager.linkPlace(trailDto.id, it) }

        val removedMediaOnTrail = trailToUpdate.mediaList.filterNot { trailDto.mediaList.contains(it) }
        val addedMediaOnTrail = trailDto.mediaList.filterNot { trailToUpdate.mediaList.contains(it) }

        removedMediaOnTrail.forEach {
            trailsManager.unlinkMedia(trailDto.id, UnLinkeMediaRequestDto(it.id))
        }
        addedMediaOnTrail.forEach {
            trailsManager.linkMedia(trailDto.id, LinkedMediaDto(it.id, it.description, it.keyVal))
        }

        trailToUpdate.name = trailDto.name
        trailToUpdate.description = trailDto.description
        trailToUpdate.officialEta = trailDto.officialEta
        trailToUpdate.code = trailDto.code
        trailToUpdate.isVariant = trailDto.isVariant
        trailToUpdate.locations = trailDto.locations
        trailToUpdate.classification = trailDto.classification
        trailToUpdate.country = trailDto.country
        trailToUpdate.lastUpdate = Date()
        trailToUpdate.maintainingSection = trailDto.maintainingSection
        trailToUpdate.territorialDivision = trailDto.territorialDivision
        trailToUpdate.cycloDetails = trailDto.cycloDetails

        generatePdfFile(trailToUpdate)

        return trailsManager.update(trailMapper.map(trailToUpdate))
    }

    fun switchToStatus(trailDto: TrailDto): List<TrailDto> {
        val trailToUpdate = trailsManager.getById(trailDto.id, TrailSimplifierLevel.LOW).first()

        if (trailDto.status == trailToUpdate.status) {
            logger.info("Did not change status to trail ${trailDto.id}")
            return trailsManager.update(trailMapper.map(trailToUpdate))
        }
        // Turn PUBLIC -> DRAFT
        if (isSwitchingToDraft(trailDto, trailToUpdate)) {
            logger.info("""Trail ${trailToUpdate.code} -> ${TrailStatus.DRAFT}""")
            trailToUpdate.locations.forEach { trailsManager.unlinkPlace(trailDto.id, it) }
            // DRAFT -> PUBLIC
        } else {
            logger.info("""Trail ${trailToUpdate.code} -> ${TrailStatus.PUBLIC}""")
            trailDto.locations.forEach { trailsManager.linkPlace(trailDto.id, it) }
        }
        trailToUpdate.status = trailDto.status
        return trailsManager.update(trailMapper.map(trailToUpdate))
    }

    private fun generatePdfFile(trailSaved: TrailDto) {
        val trailId = trailSaved.id
        val places = trailSaved.locations.flatMap { placeManager.getById(it.placeId) }
        val maintenancesByTrailId = maintenanceManager.getPastMaintenanceForTrailId(trailId, 0, Int.MAX_VALUE)
        val lastMaintenance = maintenancesByTrailId.maxByOrNull { it.date }
        val openIssues = accessibilityNotificationManager.getUnresolvedByTrailId(trailId, 0, Int.MAX_VALUE)
        logger.info("""Generating PDF file for trail '$trailId'""")
        trailFileManager.writeTrailToPdf(trailSaved, places, listOfNotNull(lastMaintenance), openIssues)
    }

    private fun isSwitchingToDraft(
            trailDto: TrailDto,
            trailToUpdate: TrailDto
    ) = trailDto.status == TrailStatus.DRAFT &&
            trailToUpdate.status == TrailStatus.PUBLIC

    fun countTrailRaw() = trailRawDao.count()

    private fun getLocationsWithInMemChangesFromPlacesRef(elements: List<PlaceRefDto>, authHelper: AuthHelper): List<PlaceDto> =
            elements.map {
                if (it.placeId.isEmpty()) {
                    val created = placeManager.create(PlaceDto(null, it.name, "",
                            emptyList(), emptyList(), listOf(it.coordinates), it.encounteredTrailIds,
                            RecordDetailsDto(Date(),
                                    authHelper.username,
                                    authHelper.instance,
                                    authHelper.realm))).first()
                    created.coordinates = created.coordinates.plus(it.coordinates)
                    created.crossingTrailIds = it.encounteredTrailIds
                    created
                } else {
                    val valueReturned = placeManager.getById(it.placeId).first()
                    valueReturned.coordinates = valueReturned.coordinates.plus(it.coordinates)
                    valueReturned.crossingTrailIds = it.encounteredTrailIds
                    valueReturned
                }
            }

    private fun getSortedIntermediateLocations(coordinates: List<TrailCoordinatesDto>,
                                               otherPlaces: List<PlaceRef>,
                                               trailCrosswaysFromLocations: List<PlaceRef>): List<PlaceRef> =
            sortLocationsByTrailCoordinates(
                    coordinates,
                    otherPlaces.plus(trailCrosswaysFromLocations))

    private fun sortLocationsByTrailCoordinates(
            coordinates: List<TrailCoordinatesDto>,
            locations: List<PlaceRef>
    ): List<PlaceRef> =
            // for each location, check closest trail Coordinate distance
            locations.sortedWith(compareBy { pr ->
                val closestCoordinatePoint: TrailCoordinatesDto? =
                        coordinates.minByOrNull { DistanceProcessor.distanceBetweenPoints(pr.coordinates, it) }

                val distance = closestCoordinatePoint!!.distanceFromTrailStart +
                        DistanceProcessor.distanceBetweenPoints(closestCoordinatePoint, pr.coordinates)
                distance
            })
}