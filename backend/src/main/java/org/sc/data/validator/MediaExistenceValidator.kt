package org.sc.data.validator

import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.MediaManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MediaExistenceValidator @Autowired constructor(
    private val mediaManager: MediaManager,
    private val authRealmValidator: AuthRealmValidator
) : Validator<String> {
    override fun validate(request: String): Set<String> {
        if (!mediaManager.doesMediaExist(request))
            return mutableSetOf(String.format(ValidatorUtils.notExistingItem, "MEDIA", request))
        return mutableSetOf()
    }

    fun validateDeleteRequest(id: String): Set<String> {
        val byId = mediaManager.getById(id)
        if (byId.isEmpty())
            return mutableSetOf(String.format(ValidatorUtils.notExistingItem, "MEDIA", id))
        if (!authRealmValidator.isAdminSameRealmAsResource(byId.first().fileDetails.realm)){
            return mutableSetOf(AuthRealmValidator.NOT_ALLOWED_MSG)
        }
        return mutableSetOf()
    }
}