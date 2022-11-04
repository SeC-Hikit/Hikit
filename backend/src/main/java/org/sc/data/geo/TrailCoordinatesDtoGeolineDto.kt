package org.sc.data.geo

import org.sc.common.rest.TrailCoordinatesDto
import org.sc.common.rest.geo.GeoLineDto
import org.sc.data.model.Coordinates2D
import org.springframework.stereotype.Component

@Component
class TrailCoordinatesDtoGeolineDto {
    fun mapToGeoline(trailCoordinates: List<TrailCoordinatesDto>): GeoLineDto =
            GeoLineDto(trailCoordinates.map { Coordinates2D(it.longitude, it.latitude) })
}