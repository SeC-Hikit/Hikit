package org.sc.data.validator

import org.sc.common.rest.AccessibilityNotificationCreation
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityCreationValidator : Validator<AccessibilityNotificationCreation> {

    companion object {
        const val noParamSpecifiedError = "Empty field '%s'"
        const val dateInFutureError = "Date field with value '%s' is in the future"
    }

    override fun validate(request: AccessibilityNotificationCreation): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.code.isBlank()) {
            errors.add(String.format(noParamSpecifiedError, "Code"))
        }
        if(request.description.isBlank()) {
            errors.add(String.format(noParamSpecifiedError, "Description"))
        }
        if(request.reportDate.after(Date())) errors.add(String.format(dateInFutureError, request.reportDate.toString()))

        return errors
    }
}