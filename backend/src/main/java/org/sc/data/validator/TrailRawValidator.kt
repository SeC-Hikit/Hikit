package org.sc.data.validator

import org.apache.commons.lang3.StringUtils
import org.sc.common.rest.Coordinates2DDto
import org.sc.common.rest.TrailRawDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.sc.data.validator.ValidatorUtils.Companion.emptyFieldError

@Component
class TrailRawValidator @Autowired constructor(
        private val coordinatesValidator: CoordinatesValidator,
        private val trailCoordinatesValidator: TrailCoordinatesValidator) : Validator<TrailRawDto> {

    override fun validate(request: TrailRawDto): Set<String> {
        val errors = mutableSetOf<String>()
        if(StringUtils.isEmpty(request.name)) {
            errors.add(String.format(emptyFieldError, "name"))
        }
        errors.addAll(coordinatesValidator
                .validate2D(Coordinates2DDto(request.startPos.longitude, request.startPos.latitude)))
        errors.addAll(coordinatesValidator
                .validate2D(Coordinates2DDto(request.finalPos.longitude, request.finalPos.latitude)))
        errors.addAll(request.coordinates.flatMap { trailCoordinatesValidator.validate(it) })
        return errors
    }

}
