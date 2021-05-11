package org.sc.data.validator.trail

import org.sc.data.validator.Validator
import org.sc.data.validator.ValidatorUtils
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.TrailManager
import org.springframework.stereotype.Component

@Component
class TrailExistenceValidator constructor(
        private val trailManager: TrailManager,
        private val realmValidator: AuthRealmValidator
) : Validator<String> {
    override fun validate(request: String): Set<String> {
        val byId = trailManager.doesTrailExist(request)
        if(!byId) {
            return mutableSetOf((String.format(ValidatorUtils.notExistingItem, "Trail", request)))
        }
        return mutableSetOf()
    }

    fun validateExistenceAndRealm(id: String) : Set<String> {
        val errors = mutableSetOf<String>()
        errors.addAll(validate(id))
        if(errors.isNotEmpty()) {
            return errors
        }
        val byId = trailManager.getById(id, true)
        if(!realmValidator.isAdminSameRealmAsResource(byId.first().fileDetails.realm)){
            errors.add(AuthRealmValidator.NOT_ALLOWED_MSG)
        }
        return errors
    }
}