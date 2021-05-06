package org.sc.data.validator

import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.PlaceManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlaceExistenceValidator @Autowired constructor(
    private val placeManager: PlaceManager,
    private val realmValidator: AuthRealmValidator
) : Validator<String> {

    override fun validate(request: String): Set<String> {
        if (!placeManager.doesPlaceExist(request))
            mutableSetOf(String.format(ValidatorUtils.notExistingItem, "PLACE", request))
        return mutableSetOf()
    }

    fun validatePlace(id: String): Set<String> {
        val errors = mutableSetOf<String>()
        errors.addAll(validate(id))
        if (errors.isNotEmpty()) {
            return errors
        }
        if (!realmValidator
                .isAdminSameRealmAsResource(
                    placeManager
                        .getById(id)
                        .first().recordDetails.realm
                )
        ) {
            errors.add(AuthRealmValidator.NOT_ALLOWED_MSG)
        }
        return errors
    }
}