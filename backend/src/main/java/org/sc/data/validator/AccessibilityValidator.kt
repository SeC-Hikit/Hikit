package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.AccessibilityNotificationManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityValidator @Autowired constructor(
    private val authRealmValidator: AuthRealmValidator,
    private val accessibilityNotificationManager: AccessibilityNotificationManager
) : Validator<AccessibilityNotificationDto> {

    companion object {
        const val noParamSpecifiedError = "Empty field '%s'"
        const val dateInFutureError = "Date field with value '%s' is in the future"
    }

    override fun validate(request: AccessibilityNotificationDto): Set<String> {
        val errors = mutableSetOf<String>()

        if (isEmpty(request.trailId)) {
            errors.add(String.format(noParamSpecifiedError, "Code"))
        }
        if (isEmpty(request.description)) {
            errors.add(String.format(noParamSpecifiedError, "Description"))
        }
        if (request.reportDate == null || request.reportDate.after(Date()))
            errors.add(String.format(dateInFutureError, request.reportDate.toString()))

        return errors
    }

    fun validateDeleteRequest(id: String): Set<String> {

        val errors = mutableSetOf<String>()
        val byId = accessibilityNotificationManager.byId(id)

        if (byId.isEmpty()) {
            errors.add("Accessibility Notification does not exist")
            return errors;
        }
        errors.addAll(authRealmValidator.validate(byId.first().recordDetails.realm))
        return errors
    }

}