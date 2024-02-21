package org.sc.common.rest

import org.sc.data.model.TrailClassification

data class HikeStepDto(
    val id: String,
    val classification: TrailClassification,
    val trailCoords: TrailCoordinatesDto,
    val calculatedElements: HikeCalculatedElementsDto,
    val statsTrailMetadata : StatsTrailMetadataDto,
    val linkedMedia: List<LinkedMediaDto>,
    val cycloDetails: CycloDetailsDto
)