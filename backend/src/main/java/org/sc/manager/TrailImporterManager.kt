package org.sc.manager

import org.sc.common.rest.TrailDto
import org.sc.common.rest.TrailImportDto
import org.sc.data.model.Trail
import org.sc.data.model.StatsTrailMetadata
import org.sc.data.model.SimpleCoordinates
import org.sc.data.mapper.PositionMapper
import org.sc.data.mapper.TrailCoordinatesMapper
import org.sc.data.model.GeoLineString
import org.sc.data.repository.TrailDatasetVersionDao
import org.sc.processor.TrailsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailImporterManager @Autowired constructor(
    private val trailsManager: TrailManager,
    private val trailsCalculator: TrailsCalculator,
    private val trailDatasetVersionDao: TrailDatasetVersionDao,
    private val positionMapper: PositionMapper,
    private val trailCoordinatesMapper: TrailCoordinatesMapper
) {

    fun save(importingTrail: TrailImportDto): List<TrailDto> {

        val statsTrailMetadata = StatsTrailMetadata(
            trailsCalculator.calculateTotRise(importingTrail.coordinates),
            trailsCalculator.calculateTotFall(importingTrail.coordinates),
            trailsCalculator.calculateEta(importingTrail.coordinates),
            trailsCalculator.calculateTrailLength(importingTrail.coordinates)
        )

        val createdOn = Date()

        val trail = Trail.builder().name(importingTrail.name)
            .description(importingTrail.description)
            .code(importingTrail.code)
            .variant(importingTrail.isVariant)
            .startPos(positionMapper.positionDtoToPosition(importingTrail.startPos))
            .finalPos(positionMapper.positionDtoToPosition(importingTrail.finalPos))
            .locations(importingTrail.locations.map { positionMapper.positionDtoToPosition(it) })
            .classification(importingTrail.classification)
            .country(importingTrail.country)
            .statsTrailMetadata(statsTrailMetadata)
            .coordinates(importingTrail.coordinates.map {
                trailCoordinatesMapper.trailCoordinatesDtoToTrailCoordinates(
                    it
                )
            })
            .createdOn(createdOn)
            .lastUpdate(createdOn)
            .maintainingSection(importingTrail.maintainingSection)
            .territorialDivision(importingTrail.territorialDivision)
            .geoLineString(GeoLineString( importingTrail.coordinates.map {
                SimpleCoordinates(
                    it.longitude,
                    it.latitude
                )
            }))
            .mediaList(emptyList())

        .build()

        val savedTrailDao = trailsManager.save(trail)
        trailDatasetVersionDao.increaseVersion()

        return savedTrailDao
    }

    fun countImport(): Long = trailDatasetVersionDao.countImport()

}