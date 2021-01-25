package org.sc.common.rest

data class StatsTrailMetadataDto(
    val totalRise: Double,
    val totalFall: Double,
    val eta: Double,
    val length: Double
)