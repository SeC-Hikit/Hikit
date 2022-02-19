package org.sc.data.validator

import org.sc.common.rest.Coordinates2DDto
import org.sc.common.rest.CoordinatesDto
import org.sc.data.model.Coordinates2D
import org.springframework.stereotype.Component

@Component
class CoordinatesValidator : Validator<CoordinatesDto> {
    companion object {
        enum class CoordDimension {
            LATITUDE, LONGITUDE, ALTITUDE
        }
        const val topPeakInTheWorld = 8848.0
        const val bottomPointKnown = -100
        const val limitLat = 90
        const val limitLong = 180
        const val longitudeValueOutOfBoundErrorMessage = "Longitude value out of bound"
        const val latitudeValueOutOfBoundErrorMessage = "Latitude value out of bound"
        const val altitudeOutOfBoundsErrorMessage = "Altitude should be a value contained between $bottomPointKnown and $topPeakInTheWorld"
    }

    override fun validate(request: CoordinatesDto): Set<String> {
        val errors = mutableSetOf<String>()
        val validateLongitude = validateCoordinates(request.longitude, CoordDimension.LONGITUDE)
        if (validateLongitude.isNotEmpty()) errors.add(validateLongitude)
        val validateLatitude = validateCoordinates(request.latitude, CoordDimension.LATITUDE)
        if (validateLatitude.isNotEmpty()) errors.add(validateLatitude)
        return errors
    }

    fun validate2D (request: Coordinates2DDto): Set<String> {
        val errors = mutableSetOf<String>()
        val validateLongitude = validateCoordinates(request.longitude, CoordDimension.LONGITUDE)
        if (validateLongitude.isNotEmpty()) errors.add(validateLongitude)
        val validateLatitude = validateCoordinates(request.latitude, CoordDimension.LATITUDE)
        if (validateLatitude.isNotEmpty()) errors.add(validateLatitude)
        return errors
    }

    fun validateCoordinates(value: Double, dim: CoordDimension): String {

        if (dim == CoordDimension.LONGITUDE) {
            if (value > limitLong || value < -limitLong) {
                return longitudeValueOutOfBoundErrorMessage
            }
            return ""
        }

        if (dim == CoordDimension.ALTITUDE) {
            if (value > topPeakInTheWorld || value < bottomPointKnown) {
                return altitudeOutOfBoundsErrorMessage
            }
            return ""
        }

        if (value > limitLat || value < -limitLat) {
            return latitudeValueOutOfBoundErrorMessage
        }

        return ""
    }
}