package org.sc.common.rest

data class PositionDto(
    val name: String,
    val tags: List<String>,
    val coordinates: TrailCoordinatesDto)