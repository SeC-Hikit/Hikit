package org.sc.data.validator

import org.sc.common.rest.geo.GeoLineDto
import org.springframework.stereotype.Component

@Component
class GeoLineValidator constructor(private val coordinatesValidator: CoordinatesValidator) : Validator<GeoLineDto> {
    override fun validate(request: GeoLineDto): Set<String> {
        val errors = mutableSetOf<String>()
        errors.addAll(request.coordinates.map {
            coordinatesValidator.validateCoordinates(it.latitude, CoordinatesValidator.Companion.CoordDimension.LATITUDE)
        })
        errors.addAll(request.coordinates.map {
            coordinatesValidator.validateCoordinates(it.longitude, CoordinatesValidator.Companion.CoordDimension.LONGITUDE)
        })
        return errors
    }
}