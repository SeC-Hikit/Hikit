package org.sc.manager

import org.sc.common.rest.TrailDto
import org.sc.common.rest.TrailImportDto
import org.sc.data.dto.PositionMapper
import org.sc.data.dto.TrailCoordinatesMapper
import org.sc.data.entity.StatsTrailMetadata
import org.sc.data.entity.Trail
import org.sc.data.repository.TrailDatasetVersionDao
import org.sc.processor.TrailsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailImporterManager @Autowired constructor(private val trailsManager : TrailManager,
                                                  private val trailsCalculator : TrailsCalculator,
                                                  private val trailDatasetVersionDao: TrailDatasetVersionDao,
                                                  private val positionMapper : PositionMapper,
                                                  private val trailCoordinatesMapper: TrailCoordinatesMapper
){

    fun save(importingTrail: TrailImportDto): List<TrailDto> {

        val statsTrailMetadata = StatsTrailMetadata(
            trailsCalculator.calculateTotRise(importingTrail.coordinates),
            trailsCalculator.calculateTotFall(importingTrail.coordinates),
            trailsCalculator.calculateEta(importingTrail.coordinates),
            trailsCalculator.calculateTrailLength(importingTrail.coordinates)
        )

        val trail = Trail(
            importingTrail.name,
            importingTrail.description,
            importingTrail.code,
            positionMapper.positionDtoToPosition(importingTrail.startPos),
            positionMapper.positionDtoToPosition(importingTrail.finalPos),
            importingTrail.locations.map { positionMapper.positionDtoToPosition(it) },
            importingTrail.classification,
            importingTrail.country,
            statsTrailMetadata,
            importingTrail.coordinates.map { trailCoordinatesMapper.trailCoordinatesDtoToTrailCoordinates(it) },
            Date(),
            importingTrail.maintainingSection
        )

        trailsManager.save(trail)
        trailDatasetVersionDao.increaseVersion()

        return trailsManager.getByCode(trail.code, false)
    }

    fun countImport(): Long = trailDatasetVersionDao.countImport()

}