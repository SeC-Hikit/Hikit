package org.sc.data.validator

import org.apache.commons.lang3.StringUtils
import org.sc.common.rest.PlaceRefDto
import org.sc.manager.PlaceManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceRefValidator @Autowired constructor(
    private val trailCoordinatesCreationValidator: CoordinatesValidator,
    private val placeManager: PlaceManager
) : Validator<PlaceRefDto> {

    override fun validate(request: PlaceRefDto): Set<String> {
        val errors = mutableSetOf<String>();

        if (request.name == null || request.name.trim().isBlank()) {
            errors.add(String.format(ValidatorUtils.emptyFieldError, "Name"))
        }
        errors.addAll(trailCoordinatesCreationValidator.validate(request.coordinates))
        return errors
    }
}