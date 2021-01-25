package org.sc.common.rest

import java.util.*

data class TrailImportDto(
     val name: String,
     val description: String,
     val code: String,
     val startPos: PositionDto,
     val finalPos: PositionDto,
     val locations: List<PositionDto>,
     val coordinates: List<TrailCoordinatesDto>,
     val classification: TrailClassification,
     val country: String,
    val lastUpdate: Date,
     val maintainingSection: String
)