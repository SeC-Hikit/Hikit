package org.sc.common.rest

import org.sc.data.model.CoordinatesWithDistanceDto
import org.sc.data.model.TrailPreview

data class CustomItineraryResultDto(
    val coordinates: List<CoordinatesWithDistanceDto>,
    val trailPreviews: List<TrailPreview>,
    val notifications: List<AccessibilityNotificationDto>,
    val stats: StatsTrailMetadataDto
    )