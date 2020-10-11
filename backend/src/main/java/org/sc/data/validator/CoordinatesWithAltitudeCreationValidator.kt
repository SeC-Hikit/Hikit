package org.sc.data.validator

import org.sc.data.CoordinatesWithAltitude

class CoordinatesWithAltitudeCreationValidator : Validator<CoordinatesWithAltitude>, CoordinatesValidator {

    companion object {
        const val topPeakInTheWorld = 8848.0
        const val altitudeOutOfBoundsErrorMessage = "Altitude should be a value contained between 0 and $topPeakInTheWorld"
    }

    override fun validate(request: CoordinatesWithAltitude): Set<String> {
        val listOfErrorMessages = mutableSetOf<String>()
        if (request.altitude < 0.0 || request.altitude > topPeakInTheWorld ) listOfErrorMessages.add(altitudeOutOfBoundsErrorMessage)
        val validateLongitude = validateCoordinates(request.longitude, CoordinatesValidator.Companion.CoordDimension.LONGITUDE)
        if (validateLongitude.isNotEmpty()) listOfErrorMessages.add(validateLongitude)
        val validateLatitude = validateCoordinates(request.latitude, CoordinatesValidator.Companion.CoordDimension.LATITUDE)
        if (validateLatitude.isNotEmpty()) listOfErrorMessages.add(validateLatitude)
        return listOfErrorMessages
    }

}