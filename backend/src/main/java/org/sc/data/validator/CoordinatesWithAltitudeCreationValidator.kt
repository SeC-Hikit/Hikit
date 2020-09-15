package org.sc.data.validator

import org.sc.data.CoordinatesWithAltitude

class CoordinatesWithAltitudeCreationValidator : Validator<CoordinatesWithAltitude>, CoordinatesValidator {

    override fun validate(request: CoordinatesWithAltitude): Set<String> {
        val listOfErrorMessages = mutableSetOf<String>()

        val validateLongitude = validateCoordinates(request.longitude, CoordinatesValidator.Companion.CoordDimension.LONGITUDE)
        if (validateLongitude.isNotEmpty()) listOfErrorMessages.add(validateLongitude)
        val validateLatitude = validateCoordinates(request.latitude, CoordinatesValidator.Companion.CoordDimension.LATITUDE)
        if (validateLatitude.isNotEmpty()) listOfErrorMessages.add(validateLatitude)
        return listOfErrorMessages
    }

}