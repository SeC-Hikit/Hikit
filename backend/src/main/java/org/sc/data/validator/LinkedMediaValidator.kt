package org.sc.data.validator

import org.apache.commons.lang3.StringUtils
import org.sc.common.rest.LinkedMediaDto
import org.sc.data.entity.LinkedMedia
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LinkedMediaValidator @Autowired constructor(
    private val keyValValidator: KeyValValidator): Validator<LinkedMediaDto> {

    override fun validate(request: LinkedMediaDto): Set<String> {
        val errors = mutableSetOf<String>()
        if(StringUtils.isEmpty(request.id)) { errors.add(String.format(ValidatorUtils.emptyFieldError, LinkedMedia.ID)) }
        if(StringUtils.isEmpty(request.description)) { String.format(ValidatorUtils.emptyFieldError, LinkedMedia.DESCRIPTION) }
        if(request.keyVal == null) { String.format(ValidatorUtils.nullValueError, LinkedMedia.KEY_VAL) }
        request.keyVal.forEach {
            val err = keyValValidator.validate(it)
            errors.addAll(err)
        }
        return errors
    }
}