package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.AccessibilityNotificationCreationDto
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityValidator : Validator<AccessibilityNotificationCreationDto> {

    companion object {
        const val noParamSpecifiedError = "Empty field '%s'"
        const val dateInFutureError = "Date field with value '%s' is in the future"
    }

    override fun validate(request: AccessibilityNotificationCreationDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (isEmpty(request.code)) {
            errors.add(String.format(noParamSpecifiedError, "Code"))
        }
        if(isEmpty(request.description)) {
            errors.add(String.format(noParamSpecifiedError, "Description"))
        }
        if(request.reportDate == null || request.reportDate.after(Date()))
            errors.add(String.format(dateInFutureError, request.reportDate.toString()))

        return errors
    }
}