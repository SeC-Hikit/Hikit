package org.sc.data.validator

import org.sc.common.rest.geo.GeoLineDto
import org.springframework.stereotype.Component

@Component
class GeoLineValidator constructor(private val coordinatesValidator: CoordinatesValidator) : Validator<GeoLineDto> {

    companion object {
        const val tooLittleCoordinates = "At least two points are needed for a line intersection request"
    }

    override fun validate(request: GeoLineDto): Set<String> {
        val errors = mutableSetOf<String>()

        if(request.coordinates.size < 2)
        {
            errors.add(tooLittleCoordinates)
            return errors
        }

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