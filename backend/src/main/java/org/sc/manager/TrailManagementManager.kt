package org.sc.manager

import org.sc.common.rest.*
import org.sc.data.mapper.*
import org.sc.data.model.*
import org.sc.data.repository.TrailDatasetVersionDao
import org.sc.data.repository.TrailRawDAO
import org.sc.processor.TrailsStatsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailManagementManager @Autowired constructor(
    private val trailsManager: TrailManager,
    private val trailsStatsCalculator: TrailsStatsCalculator,
    private val trailDatasetVersionDao: TrailDatasetVersionDao,
    private val placeMapper: PlaceRefMapper,
    private val trailCoordinatesMapper: TrailCoordinatesMapper,
    private val trailRawMapper: TrailRawMapper,
    private val fileDetailsMapper: FileDetailsMapper,
    private val trailRawDao: TrailRawDAO,
    private val trailMapper: TrailMapper,
    private val placeManager: PlaceManager
) {

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

        val trail = Trail.builder().name(importingTrail.name)
            .startLocation(importingTrail.locations.map { placeMapper.map(it) }.first())
            .endLocation(importingTrail.locations.map { placeMapper.map(it) }.last())
            .description(importingTrail.description)
            .officialEta(importingTrail.officialEta)
            .code(importingTrail.code)
            .variant(importingTrail.isVariant)
            .locations(getConsistentLocations(importingTrail))
            .classification(importingTrail.classification)
            .country(importingTrail.country)
            .statsTrailMetadata(statsTrailMetadata)
            .coordinates(importingTrail.coordinates.map { trailCoordinatesMapper.map(it) })
            .createdOn(createdOn)
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
            .fileDetails(fileDetailsMapper.map(importingTrail.fileDetailsDto))
            .status(importingTrail.trailStatus)
            .build()

        val savedTrailDao = trailsManager.save(trail)

        trailDatasetVersionDao.increaseVersion()

        return savedTrailDao
    }

    fun updateTrail(requestedTrail: TrailDto) : List<TrailDto> {

        val savedTrail = trailsManager.getById(requestedTrail.id, false).first()

        val removedPlacesOnTrail = savedTrail.locations.filterNot { requestedTrail.locations.contains(it) }
        val addedPlacesOnTrail = requestedTrail.locations.filterNot { savedTrail.locations.contains(it) }

        removedPlacesOnTrail.forEach { trailsManager.unlinkPlace(requestedTrail.id, it) }
        addedPlacesOnTrail.forEach { trailsManager.linkPlace(requestedTrail.id, it) }

        val removedMediaOnTrail = savedTrail.mediaList.filterNot { requestedTrail.mediaList.contains(it) }
        val addedMediaOnTrail = requestedTrail.mediaList.filterNot { savedTrail.mediaList.contains(it) }

        removedMediaOnTrail.forEach {
            trailsManager.unlinkMedia(requestedTrail.id, UnLinkeMediaRequestDto(it.id))
        }
        addedMediaOnTrail.forEach {
            trailsManager.linkMedia(requestedTrail.id, LinkedMediaDto(it.id, it.description, it.keyVal))
        }

        // If the state has been changed, then reflect that on
        // the places connected with it.
        if(savedTrail.status != requestedTrail.status) {
            toggleDraftPublicState(savedTrail, requestedTrail);
        }

        savedTrail.name = requestedTrail.name
        savedTrail.description = requestedTrail.description
        savedTrail.officialEta = requestedTrail.officialEta
        savedTrail.code = requestedTrail.code
        savedTrail.isVariant = requestedTrail.isVariant
        savedTrail.locations = requestedTrail.locations
        savedTrail.classification = requestedTrail.classification
        savedTrail.country = requestedTrail.country
        savedTrail.lastUpdate = Date()
        savedTrail.maintainingSection = requestedTrail.maintainingSection
        savedTrail.territorialDivision = requestedTrail.territorialDivision
        savedTrail.cycloDetails = requestedTrail.cycloDetails
        savedTrail.status = requestedTrail.status

        return trailsManager.save(trailMapper.map(savedTrail))
    }

    fun countTrailRaw() = trailRawDao.count()

    private fun toggleDraftPublicState(oldTrailFromDb: TrailDto,
                                       requestedTrail: TrailDto) {
        if(requestedTrail.status == TrailStatus.DRAFT) {
            // For each place, unlink the trail
            unlinkTrailFromAllPlaces(trailMapper.map(oldTrailFromDb))
        } else {
           linkTrailToAllAssignedPlaces(requestedTrail)
        }
        // For each place in trail, link that back again
    }

    private fun linkTrailToAllAssignedPlaces(requestedTrail: TrailDto) {
        requestedTrail.locations.forEach {
            placeManager.linkTrailToPlace(it.placeId,
            requestedTrail.id,
            it.trailCoordinates)
        }
    }

    private fun unlinkTrailFromAllPlaces(oldTrailFromDb: Trail) {
        oldTrailFromDb.locations.forEach {
            placeManager.removeTrailFromPlaces(it.placeId,
                    oldTrailFromDb.id,
                    it.trailCoordinates)
        }
    }

    private fun getConsistentLocations(importingTrail: TrailImportDto) =
        sortLocationsByTrailCoordinates(importingTrail.locations.map { placeMapper.map(it) })

    private fun sortLocationsByTrailCoordinates(locations: List<PlaceRef>): List<PlaceRef> =
        locations.sortedBy { it.trailCoordinates.distanceFromTrailStart }
}