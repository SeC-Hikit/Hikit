package org.sc.importer

import org.sc.common.rest.controller.AccessibilityNotification
import org.sc.data.validator.Validator
import org.springframework.stereotype.Component

@Component
class AccessibilityCreationValidator : Validator<AccessibilityNotification> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
    }

    override fun validate(request: AccessibilityNotification): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.code.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Code"))
        }
        if(request.description.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Description"))
        }
        return errors
    }
}