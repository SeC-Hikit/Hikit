package org.sc.common.rest.geo

data class LocateDto(
    val rectangleDto: RectangleDto,
    val trailIdsNotToLoad: List<String>
)
