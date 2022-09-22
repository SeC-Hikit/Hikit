package org.sc.processor.helper

import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.StatsTrailMetadataDto
import org.sc.processor.TrailsStatsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailCoordinatesCalculator constructor(@Autowired private val trailsStatsCalculator: TrailsStatsCalculator)
{
    fun calculateStats(coordinates: List<CoordinatesDto>) =
            StatsTrailMetadataDto(
                    trailsStatsCalculator.calculateTotRise(coordinates),
                    trailsStatsCalculator.calculateTotFall(coordinates),
                    trailsStatsCalculator.calculateEta(coordinates),
                    trailsStatsCalculator.calculateTrailLength(coordinates),
                    trailsStatsCalculator.calculateHighestPlace(coordinates),
                    trailsStatsCalculator.calculateLowestPlace(coordinates)
            )
}