package org.sc.data.validator

import org.sc.common.rest.PointGeolocationDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PointGeolocationValidatorDto @Autowired constructor(private val coordinatesValidator: CoordinatesValidator) :
    Validator<PointGeolocationDto> {

    companion object {
        const val MAX_DISTANCE : Int = 5000

        const val ERROR_START_MESSAGE : String = "Invalid distance."
    }

    override fun validate(request: PointGeolocationDto): Set<String> {
        val errors = mutableSetOf<String>()
        errors.addAll(coordinatesValidator.validate(request.coordinatesDto))
        if(request.distance < 0) {
            errors.add("$ERROR_START_MESSAGE Distance cannot be negative")
        }
        if(request.distance > MAX_DISTANCE) {
            errors.add("$ERROR_START_MESSAGE Maximum distance allowed is $MAX_DISTANCE")
        }
        return errors
    }

}