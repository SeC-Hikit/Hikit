package org.sc.data.validator

import org.apache.commons.lang3.StringUtils
import org.sc.common.rest.PlaceRefDto
import org.sc.manager.PlaceManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceRefValidator @Autowired constructor(
    private val trailCoordinatesCreationValidator: TrailCoordinatesValidator,
    private val placeManager: PlaceManager
) : Validator<PlaceRefDto> {

    override fun validate(request: PlaceRefDto): Set<String> {
        val errors = mutableSetOf<String>();

        if (StringUtils.isBlank(request.name)) {
            errors.add(String.format(ValidatorUtils.emptyFieldError, "Name"))
        }
        errors.addAll(trailCoordinatesCreationValidator.validate(request.trailCoordinates))
        if (!placeManager.doesItExist(request.placeId)) {
            errors.add("Place with id:'${request.placeId}' does not exist")
        }
        return errors
    }
}