package org.sc.data.validator

interface CoordinatesValidator {
    companion object {
        enum class CoordDimension {
            LATITUDE, LONGITUDE
        }

        const val limitLat = 90
        const val limitLong = 180
        const val longitudeValueOutOfBoundErrorMessage = "Longitude value out of bound"
        const val latitudeValueOutOfBoundErrorMessage = "Latitude value out of bound"
    }

    fun validateCoordinates(value: Double, dim: CoordDimension): String {
        if (dim == CoordDimension.LONGITUDE) {
            if (value > limitLong || value < -limitLong) {
                return longitudeValueOutOfBoundErrorMessage
            }
            return ""
        }

        if (value > limitLat || value < -limitLat) {
            return latitudeValueOutOfBoundErrorMessage
        }
        return ""
    }
}