package org.sc.data.validator

import org.apache.commons.lang3.StringUtils
import org.sc.common.rest.PlaceRefDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceRefValidator @Autowired constructor(
        private val trailCoordinatesCreationValidator: CoordinatesValidator
) : Validator<PlaceRefDto> {

    override fun validate(request: PlaceRefDto): Set<String> {
        val errors = mutableSetOf<String>();

        if (StringUtils.isEmpty(request.name) && !request.isDynamicCrossway) {
            errors.add(String.format(ValidatorUtils.emptyFieldError, "Name"))
        }

        errors.addAll(trailCoordinatesCreationValidator.validate(request.coordinates))
        return errors
    }
}