package org.sc.importer

import org.sc.common.rest.controller.AccessibilityNotificationCreation
import org.sc.data.validator.Validator
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityCreationValidator : Validator<AccessibilityNotificationCreation> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
    }

    override fun validate(request: AccessibilityNotificationCreation): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.code.isBlank()) {
            errors.add(String.format(noParamSpecified, "Code"))
        }
        if(request.description.isBlank()) {
            errors.add(String.format(noParamSpecified, "Description"))
        }
        if(request.reportDate.after(Date())) errors.add(String.format(noParamSpecified, "Description"))

        return errors
    }
}