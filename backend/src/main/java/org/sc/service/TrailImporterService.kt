package org.sc.service

import org.sc.common.rest.*
import org.sc.configuration.auth.AuthFacade
import org.sc.configuration.auth.AuthHelper
import org.sc.data.geo.TrailPlacesAligner
import org.sc.data.mapper.CoordinatesMapper
import org.sc.data.mapper.TrailMapper
import org.sc.data.mapper.TrailRawMapper
import org.sc.data.model.*
import org.sc.data.repository.TrailDatasetVersionDao
import org.sc.data.repository.TrailRawDAO
import org.sc.manager.PlaceManager
import org.sc.manager.ResourceManager
import org.sc.manager.TrailFileManager
import org.sc.manager.TrailManager
import org.sc.manager.regeneration.RegenerationActionType
import org.sc.manager.regeneration.RegenerationEntryType
import org.sc.processor.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class TrailImporterService @Autowired constructor(
        private val trailsManager: TrailManager,
        private val trailFileManager: TrailFileManager,
        private val placeManager: PlaceManager,
        private val resourceManager: ResourceManager,
        private val placesTrailSyncProcessor: PlacesTrailSyncProcessor,
        private val trailsStatsCalculator: TrailsStatsCalculator,
        private val trailDatasetVersionDao: TrailDatasetVersionDao,
        private val coordinatesMapper: CoordinatesMapper,
        private val trailPlacesAligner: TrailPlacesAligner,
        private val trailRawMapper: TrailRawMapper,
        private val trailRawDao: TrailRawDAO,
        private val trailMapper: TrailMapper,
        private val authFacade: AuthFacade,
        private val trailSimplifier: TrailSimplifier
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val maxDistanceForPlaceAssociationInM = 50

    fun saveRaw(trailRaw: TrailRawDto): TrailRawDto =
            trailRawDao.createRawTrail(trailRawMapper.map(trailRaw)).map { trailRawMapper.map(it) }
                    .first()

    fun save(importingTrail: TrailImportDto): List<TrailDto> {
        logger.info("Enforcing point calculation...")

        val coordinates = importingTrail.coordinates.map {
            TrailCoordinates(it.latitude, it.longitude, it.altitude,
                    trailsStatsCalculator.calculateLengthFromTo(importingTrail.coordinates, it))
        }

        logger.info("Calculating stats...")
        val statsTrailMetadata = StatsTrailMetadata(
                trailsStatsCalculator.calculateTotRise(coordinates),
                trailsStatsCalculator.calculateTotFall(coordinates),
                trailsStatsCalculator.calculateEta(coordinates),
                trailsStatsCalculator.calculateTrailLength(coordinates),
                trailsStatsCalculator.calculateHighestPlace(coordinates),
                trailsStatsCalculator.calculateLowestPlace(coordinates)
        )

        val createdOn = Date()

        val authHelper = authFacade.authHelper

        logger.info("Creating or retrieving crossway places for trail import...")
        val trailCrosswaysFromLocations: List<PlaceDto> = getLocationFromPlaceRef(
                listOf(), importingTrail.crossways, authHelper)

        logger.info("Creating or retrieving other places for trail import...")
        val placesLocations: List<PlaceDto> = getLocationFromPlaceRef(
                trailCrosswaysFromLocations,
                importingTrail.locations,
                authHelper)

        logger.info("Mapping retrieved places to refs")
        val locationsSet = getDistinctPlacesRefs(placesLocations, trailCrosswaysFromLocations)

        logger.info("Reordering places refs in memory...")
        val placesInOrder: List<PlaceRef> =
                trailPlacesAligner.sortLocationsByTrailCoordinates(
                        coordinates,
                        locationsSet.toList()
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
        if (savedTrailAsList.isEmpty()) {
            logger.warn("Something went wrong with saving trail data, rolling back...")
            // TODO rollback

        }

        val trailSaved = savedTrailAsList.first()
        logger.info("Linking places to trail...")
        placesTrailSyncProcessor.populatePlacesWithTrailData(trailSaved)

        logger.info("Generating static resources for trail...")
        val targetPlaces = trailSaved.locations.flatMap { placeManager.getById(it.placeId) }
        updateResourcesForTrail(trailSaved, targetPlaces)

        trailSaved.locations
                .flatMap { it.encounteredTrailIds }
                .filter { it != trailSaved.id }
                .flatMap { trailsManager.getById(it, TrailSimplifierLevel.LOW) }
                .forEach {
                    logger.info("Ri-generating static resources for related trail '${it.id}'...")
                    resourceManager.addEntry(it.id, RegenerationEntryType.TRAIL,
                            trailSaved.id, authHelper.username, RegenerationActionType.CREATE)
                }


        logger.info("Updating trail set version...")
        trailDatasetVersionDao.increaseVersion()

        logger.info("Done importing trail.")
        return savedTrailAsList
    }

    private fun getDistinctPlacesRefs(placesLocations: List<PlaceDto>,
                                      trailCrosswaysFromLocations: List<PlaceDto>): List<PlaceRef> {

        val otherPlacesRefs = placesLocations.map {
            PlaceRef(it.name,
                    coordinatesMapper.map(it.coordinates.last()), it.id, it.crossingTrailIds, it.isDynamic)
        }

        val trailCrosswaysFromLocationsRefs = trailCrosswaysFromLocations.map {
            PlaceRef(it.name,
                    coordinatesMapper.map(it.coordinates.last()), it.id, it.crossingTrailIds, it.isDynamic)
        }

        return otherPlacesRefs.plus(trailCrosswaysFromLocationsRefs)
                .distinctBy { it.placeId }
    }

    fun mappingMatchingTrail(targetTrailRaw: TrailRawDto): List<TrailMappingDto> {
        return trailsManager.getByMatchingStartEndPoint(targetTrailRaw.startPos, targetTrailRaw.finalPos);
    }

    fun updateResourcesForTrail(targetTrail: TrailDto, targetPlaces: List<PlaceDto>) {
        trailFileManager.writeTrailToOfficialGpx(targetTrail)
        trailFileManager.writeTrailToKml(targetTrail)
        trailFileManager.writeTrailToPdf(targetTrail, targetPlaces, emptyList(), emptyList())
    }

    fun updateTrail(trailDto: TrailDto): List<TrailDto> {

        val trailId = trailDto.id

        val trailToUpdate = trailsManager.getById(trailId, TrailSimplifierLevel.LOW).first()

        val removedPlacesOnTrail = trailToUpdate.locations.filterNot { trailDto.locations.contains(it) }
        val addedPlacesOnTrail = trailToUpdate.locations.filterNot { trailDto.locations.contains(it) }
        removedPlacesOnTrail.forEach {
            trailsManager.unlinkPlace(trailId, it)
            placeManager.unlinkTrailFromPlace(
                    it.placeId, trailId,
                    coordinatesMapper.map(it.coordinates)
            )
        }
        addedPlacesOnTrail.forEach { trailsManager.linkTrailToPlace(trailId, it) }

        val removedMediaOnTrail = trailToUpdate.mediaList.filterNot { trailDto.mediaList.contains(it) }
        val addedMediaOnTrail = trailDto.mediaList.filterNot { trailToUpdate.mediaList.contains(it) }

        removedMediaOnTrail.forEach {
            trailsManager.unlinkMedia(trailId, UnLinkeMediaRequestDto(it.id))
        }
        addedMediaOnTrail.forEach {
            trailsManager.linkMedia(trailId, LinkedMediaDto(it.id, it.description, it.keyVal))
        }

        trailToUpdate.name = trailDto.name
        trailToUpdate.description = trailDto.description
        trailToUpdate.officialEta = trailDto.officialEta
        trailToUpdate.code = trailDto.code
        trailToUpdate.isVariant = trailDto.isVariant
        trailToUpdate.locations = trailDto.locations
        trailToUpdate.classification = trailDto.classification
        trailToUpdate.country = trailDto.country
        trailToUpdate.lastUpdate = trailDto.lastUpdate
        trailToUpdate.maintainingSection = trailDto.maintainingSection
        trailToUpdate.territorialDivision = trailDto.territorialDivision
        trailToUpdate.cycloDetails = trailDto.cycloDetails

        val update = trailsManager.update(trailMapper.map(trailToUpdate))

        val updateTrailId = update.first().id
        resourceManager.addEntry(updateTrailId, RegenerationEntryType.TRAIL,
                updateTrailId, authFacade.authHelper.username, RegenerationActionType.UPDATE)

        return update
    }


    private fun getLocationFromPlaceRef(otherPlacesBeingSaved: List<PlaceDto>,
                                        elements: List<PlaceRefDto>,
                                        authHelper: AuthHelper): List<PlaceDto> =
            elements.map {

                val isPlaceNotPresentOnSystem = it.placeId == null || it.placeId.trim().isEmpty()

                if (isPlaceNotPresentOnSystem) {
                    val matchingPreviouslySubmittedCrossway =
                            placeSubmittedMatchingSubmittedOnes(otherPlacesBeingSaved, it)
                    if (matchingPreviouslySubmittedCrossway.isNotEmpty()) {
                        val first = matchingPreviouslySubmittedCrossway.first()
                        logger.info("Place with name '${it.name}' is matching submitted crossway with ID: " +
                                first.id +
                                ". Going to associate that, without creating new location")
                        first.crossingTrailIds =
                                first.crossingTrailIds.toSet().plus(it.encounteredTrailIds).toList()
                        return@map first
                    }

                    val created = placeManager.create(PlaceDto(null, it.name, "",
                            emptyList(), emptyList(), listOf(it.coordinates),
                            it.encounteredTrailIds,
                            it.isDynamicCrossway,
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

    private fun placeSubmittedMatchingSubmittedOnes(otherPlacesBeingSaved: List<PlaceDto>,
                                                    it: PlaceRefDto): List<PlaceDto> =
            otherPlacesBeingSaved
                    .filter { otherPlaceSubmitted -> otherPlaceSubmitted.name == it.name }
                    .filter { otherPlaceSubmitted ->
                        otherPlaceSubmitted.coordinates.any { placeSubmittedCoordinate ->
                            DistanceProcessor.distanceBetweenPoints(placeSubmittedCoordinate, it.coordinates) <= maxDistanceForPlaceAssociationInM
                        }
                    }
}