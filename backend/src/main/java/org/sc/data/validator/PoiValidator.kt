package org.sc.data.validator

import org.sc.common.rest.PoiDto
import org.springframework.beans.factory.annotation.Autowired

class PoiValidator @Autowired constructor(
    private val trailCoordinatesCreationValidator: TrailCoordinatesCreationValidator) : Validator<PoiDto> {

    override fun validate(request: PoiDto): Set<String> {
        val errors = mutableSetOf("")
        request.createdOn
        return errors
    }
}