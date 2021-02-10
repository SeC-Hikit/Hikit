package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.PoiDto
import org.sc.data.entity.Poi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PoiValidator @Autowired constructor(
    private val keyValValidator: KeyValValidator,
    private val coordinatesValidator: CoordinatesValidator) : Validator<PoiDto> {

    companion object {
        const val dateInFutureError = "The provided date for field '%s' is in the future"
        const val emptyField = "Empty field '%s'"

    }

    override fun validate(request: PoiDto): Set<String> {
        val errors = mutableSetOf<String>()
        if(isEmpty(request.name)) { errors.add(String.format(emptyField, Poi.NAME)) }
        if(request.createdOn == null) { String.format(dateInFutureError, Poi.CREATED_ON) }
        if(request.createdOn.after(Date())) errors.add(String.format(dateInFutureError, Poi.CREATED_ON))
        if(request.lastUpdatedOn == null) { String.format(dateInFutureError, Poi.LAST_UPDATE_ON) }
        if(request.lastUpdatedOn.after(Date())) errors.add(String.format(dateInFutureError, Poi.LAST_UPDATE_ON))
        errors.addAll(coordinatesValidator.validate(request.coordinates))
        request.keyVal.forEach {
            val err = keyValValidator.validate(it);
            errors.addAll(err)
        }
        return errors
    }
}