package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.PlaceDto
import org.sc.data.validator.trail.TrailExistenceValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceValidator @Autowired constructor(
        private val trailCoordinatesCreationValidator: TrailCoordinatesValidator,
        private val mediaExistenceValidator: MediaExistenceValidator,
        private val trailExistenceValidator: TrailExistenceValidator) : Validator<PlaceDto> {

    companion object {
        const val noNameError = "No name specified in position"
    }

    override fun validate(request: PlaceDto): Set<String> {
        val listOfErrorMessages = mutableSetOf<String>()

        listOfErrorMessages.addAll(request.mediaIds.flatMap { mediaExistenceValidator.validate(it) })
        listOfErrorMessages.addAll(request.crossingTrailIds.flatMap { trailExistenceValidator.validate(it) })
        val coordinatesError = trailCoordinatesCreationValidator.validate(request.coordinates)
        listOfErrorMessages.addAll(coordinatesError)
        if (isEmpty(request.name)) listOfErrorMessages.add(noNameError)
        listOfErrorMessages.addAll(trailCoordinatesCreationValidator.validate(request.coordinates))
        return listOfErrorMessages
    }

}