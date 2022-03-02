package org.sc.data.validator.poi

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.PoiDto
import org.sc.data.model.Poi
import org.sc.data.validator.CoordinatesValidator
import org.sc.data.validator.KeyValValidator
import org.sc.data.validator.Validator
import org.sc.data.validator.ValidatorUtils
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.data.validator.trail.TrailExistenceValidator
import org.sc.manager.PoiManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PoiValidator @Autowired constructor(
    private val keyValValidator: KeyValValidator,
    private val coordinatesValidator: CoordinatesValidator,
    private val poiManager: PoiManager,
    private val trailExistenceValidator: TrailExistenceValidator,
    private val realmValidator: AuthRealmValidator
) : Validator<PoiDto> {

    companion object {
        const val blankIdError = "ID blank not accepted"
    }

    override fun validate(request: PoiDto): Set<String> {
        val errors = mutableSetOf<String>()
        if(request.id != null && request.id.isBlank()) { errors.add(blankIdError) }
        if(isEmpty(request.name)) { errors.add(String.format(ValidatorUtils.emptyFieldError, Poi.NAME)) }
        request.trailIds.forEach {
            errors.addAll(trailExistenceValidator.validate(it))
        }
        errors.addAll(coordinatesValidator.validate(request.coordinates))
        request.keyVal.forEach {
            val err = keyValValidator.validate(it)
            errors.addAll(err)
        }
        return errors
    }

    fun validateExistenceAndAuth(id: String): Set<String> {
        if(isEmpty(id)) {
            mutableSetOf(String.format(ValidatorUtils.notExistingItem, "POI", id))
        }
        val errors = mutableSetOf<String>()
        val byId = poiManager.getPoiByID(id)
        if (byId.isEmpty()) {
            return mutableSetOf(String.format(ValidatorUtils.notExistingItem, "POI", id))
        }
        errors.addAll(realmValidator.validate(byId.first().recordDetails.realm))
        return errors
    }
}