package org.sc.data.validator.auth

import org.sc.configuration.auth.AuthFacade
import org.sc.configuration.auth.UserAttribute
import org.sc.data.validator.Validator
import org.springframework.stereotype.Component

@Component
class AuthRealmValidator constructor(private val authFacade: AuthFacade) : Validator<String> {

    companion object {
        const val NOT_ALLOWED_MSG = "You are not allowed to execute this operation on the target resource"
    }

    override fun validate(request: String): Set<String> {
        if(isAdminSameRealmAsResource(request)){
            return mutableSetOf()
        }
        return mutableSetOf(NOT_ALLOWED_MSG);
    }

    fun isAdminSameRealmAsResource(resourceRealm: String) =
        authFacade.authHelper.getAttribute(UserAttribute.realm) == resourceRealm
}