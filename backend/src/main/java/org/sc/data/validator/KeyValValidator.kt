package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.KeyValueDto
import org.sc.data.validator.ValidatorUtils.Companion.emptyFieldError
import org.springframework.stereotype.Component
import java.lang.String.format

@Component
class KeyValValidator : Validator<KeyValueDto> {

    override fun validate(request: KeyValueDto): Set<String> {
        val errors = mutableSetOf<String>()
        if(isEmpty(request.key)) { errors.add(format(emptyFieldError, "key")) }
        if(isEmpty(request.value)) { errors.add(format(emptyFieldError, "value")) }
        return errors
    }

}