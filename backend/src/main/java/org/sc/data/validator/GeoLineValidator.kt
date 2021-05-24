package org.sc.data.validator

import org.sc.common.rest.geo.GeoLineDto
import org.springframework.stereotype.Component

@Component
class GeoLineValidator constructor(private val coordinatesValidator: CoordinatesValidator) : Validator<GeoLineDto> {
    override fun validate(request: GeoLineDto): Set<String> {
        val errors = mutableSetOf<String>()
        val mappedCoordsLat = request.coordinates.map {
            coordinatesValidator.validateCoordinates(
                it.latitude,
                CoordinatesValidator.Companion.CoordDimension.LATITUDE
            )
        }
        errors.addAll(mappedCoordsLat.filter { it.isNotEmpty() })
        val mappedCoordsLong = request.coordinates.map {
            coordinatesValidator.validateCoordinates(
                it.longitude,
                CoordinatesValidator.Companion.CoordDimension.LONGITUDE
            )
        }
        errors.addAll(mappedCoordsLong.filter { it.isNotEmpty() })
        return errors
    }
}