package org.sc.common.rest

import java.util.*

data class TrailDto (val name : String,
                     val description: String,
                     val code: String,
                     val startPos : PositionDto,
                     val finalPos: PositionDto,
                     val locations : List<PositionDto>,
                     val classification: TrailClassification,
                     val country: String,
                     val statsMetadata: StatsTrailMetadataDto,
                     val coordinates: List<TrailCoordinatesDto>,
                     val lastUpdate: Date,
                     val maintainingSection: String) {
}