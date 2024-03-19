package org.sc.service

import org.sc.adapter.AltitudeServiceAdapter
import org.sc.common.rest.CustomItineraryRequestDto
import org.sc.common.rest.CustomItineraryResultDto
import org.sc.common.rest.StatsTrailMetadataDto
import org.sc.common.rest.TrailPreviewDto
import org.sc.data.mapper.TrailPreviewMapper
import org.sc.data.model.Coordinates2D
import org.sc.data.model.TrailCoordinates
import org.sc.manager.AccessibilityNotificationManager
import org.sc.manager.TrailIntersectionManager
import org.sc.processor.TrailsStatsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomItineraryService @Autowired constructor(
    private val trailPreviewMapper: TrailPreviewMapper,
    private val accessibilityNotificationManager: AccessibilityNotificationManager,
    private val altitudeService: AltitudeServiceAdapter,
    private val trailsStatsCalculator: TrailsStatsCalculator,
    private val trailIntersectionManager: TrailIntersectionManager
) {

    fun calculateItinerary(customItinerary: CustomItineraryRequestDto): CustomItineraryResultDto {
        val coordinatesWithAltitudes =
            altitudeService.mapCoordsWithElevations(customItinerary.geoLineDto.coordinates)
        val coordinates = coordinatesWithAltitudes.map {
            TrailCoordinates(
                it.latitude, it.longitude, it.altitude,
                trailsStatsCalculator.calculateLengthFromTo(coordinatesWithAltitudes, it)
            )
        }
        val statsTrailMetadata = StatsTrailMetadataDto(
            trailsStatsCalculator.calculateTotRise(coordinates),
            trailsStatsCalculator.calculateTotFall(coordinates),
            trailsStatsCalculator.calculateEta(coordinates),
            trailsStatsCalculator.calculateTrailLength(coordinates),
            trailsStatsCalculator.calculateHighestPlace(coordinates),
            trailsStatsCalculator.calculateLowestPlace(coordinates)
        )
        val trailIntersections =
            trailIntersectionManager
                .findIntersection(customItinerary.geoLineDto, 0, Integer.MAX_VALUE)
        val intersectionTrails: Set<TrailPreviewDto> =
            trailIntersections.map { trailPreviewMapper.map(it.trail) }.toSet()
        val intersectionTrailsIds = intersectionTrails.map { it.id }
        val encounteredIssues =
            coordinatesWithAltitudes.map {
                Coordinates2D(it.longitude, it.latitude)
            }.flatMap {
                accessibilityNotificationManager.findNearbyUnsolved(it, 250.0)
            }.filter {
                intersectionTrailsIds.contains(it.trailId)
            }.distinctBy { it.id }.toSet()

        return CustomItineraryResultDto(
            coordinates, intersectionTrails,
            encounteredIssues, statsTrailMetadata
        )
    }


}