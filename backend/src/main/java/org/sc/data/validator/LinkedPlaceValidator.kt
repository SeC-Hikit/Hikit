package org.sc.data.validator

import org.sc.common.rest.LinkedPlaceDto
import org.sc.data.validator.trail.TrailExistenceValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LinkedPlaceValidator @Autowired constructor(private val placeExistenceValidator: PlaceExistenceValidator,
                                                  private val trailExistenceValidator: TrailExistenceValidator,
                                                  private val coordinatesValidator: CoordinatesValidator) : Validator<LinkedPlaceDto> {

    override fun validate(request: LinkedPlaceDto): Set<String> {
        val errors = mutableSetOf<String>()
        errors.addAll(placeExistenceValidator.validate(request.placeId))
        errors.addAll(coordinatesValidator.validate(request.coordinatesDto))
        errors.addAll(trailExistenceValidator.validate(request.trailId))
        return errors
    }


}