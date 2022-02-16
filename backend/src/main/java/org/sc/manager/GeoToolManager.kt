package org.sc.manager

import org.sc.adapter.AltitudeServiceAdapter
import org.sc.common.rest.CoordinatesDto
import org.sc.processor.TrailsStatsCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GeoToolManager @Autowired constructor(private val altitudeService : AltitudeServiceAdapter,
                                            private val trailsStatsCalculator: TrailsStatsCalculator) {

    fun getAltitudeByLongLat(latitude: Double, longitude: Double) =
            altitudeService.getAltitudeByLongLat(latitude, longitude)

    fun getCoordinateByLongLat(latitude: Double, longitude: Double): CoordinatesDto =
            CoordinatesDto(latitude, longitude,altitudeService.getAltitudeByLongLat(latitude, longitude))

    fun getDistanceBetweenCoordinates(coords: List<CoordinatesDto>) : Double =
            trailsStatsCalculator.calculateTrailLength(coords)
}