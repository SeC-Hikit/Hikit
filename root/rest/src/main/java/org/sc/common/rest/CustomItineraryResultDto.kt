package org.sc.common.rest

import org.sc.data.model.TrailCoordinates

data class CustomItineraryResultDto(
    val coordinates: List<TrailCoordinates>,
    val trailPreviews: Set<TrailPreviewDto>,
    val notifications: Set<AccessibilityNotificationDto>,
    val stats: StatsTrailMetadataDto
    )