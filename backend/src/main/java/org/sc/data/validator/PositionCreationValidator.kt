package org.sc.data.validator

import com.google.inject.Inject
import org.sc.data.Position

class PositionCreationValidator @Inject constructor(private val coordinatesWithAltitudeCreationValidator: CoordinatesWithAltitudeCreationValidator) : Validator<Position>, CoordinatesValidator {

    companion object {
        const val noNameError = "No name specified in position"
    }

    override fun validate(request: Position): Set<String> {
        val listOfErrorMessages = mutableSetOf<String>()

        val coordinatesError = coordinatesWithAltitudeCreationValidator.validate(request.coords)
        listOfErrorMessages.addAll(coordinatesError)
        if (request.name.isBlank()) listOfErrorMessages.add(noNameError)
        if (request.postCode.isBlank()) listOfErrorMessages.add(Validator.postcodeEmptyErrorMessage)

        return listOfErrorMessages
    }

}