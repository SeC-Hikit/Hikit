package org.sc.common.rest

data class HikeCalculatedElementsDto(
    val locations: List<PlaceRefDto>,
    val trails: List<TrailDto>,
    val pois: List<PoiDto>
)