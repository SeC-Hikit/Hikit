package org.sc.adapter

import org.hikit.common.adapter.AltitudeServiceAdapter
import org.sc.common.rest.CoordinatesDto
import org.sc.data.model.Coordinates
import org.sc.data.model.Coordinates2D
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AltitudeServiceWrapper @Autowired constructor(private val altitudeServiceAdapter: AltitudeServiceAdapter) {
    fun mapCoordsWithElevations(coordinates: List<Coordinates2D>): List<Coordinates> =
        altitudeServiceAdapter.getElevationsByLongLat(coordinates.map { Pair(it.latitude, it.longitude) })
            .mapIndexed{ index, altitude ->
                CoordinatesDto(coordinates[index].latitude,
                    coordinates[index].longitude, altitude) }

    fun getElevationsByLongLat(latitude: Double, longitude: Double) = altitudeServiceAdapter.getElevationsByLongLat(latitude, longitude)
    fun getElevationsByLongLat(coordinates: List<Pair<Double, Double>>) = altitudeServiceAdapter.getElevationsByLongLat(coordinates)
}