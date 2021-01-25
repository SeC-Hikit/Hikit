package org.sc.data.validator

import org.sc.common.rest.PositionDto
import org.sc.data.entity.Position
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PositionValidator @Autowired constructor(
        private val trailCoordinatesCreationValidator: TrailCoordinatesValidator) : Validator<PositionDto> {

    companion object {
        const val noNameError = "No name specified in position"
    }

    override fun validate(request: PositionDto): Set<String> {
        val listOfErrorMessages = mutableSetOf<String>()

        val coordinatesError = trailCoordinatesCreationValidator.validate(request.coordinates)
        listOfErrorMessages.addAll(coordinatesError)
        if (request.name.isBlank()) listOfErrorMessages.add(noNameError)
        listOfErrorMessages.addAll(trailCoordinatesCreationValidator.validate(request.coordinates))
        return listOfErrorMessages
    }

}