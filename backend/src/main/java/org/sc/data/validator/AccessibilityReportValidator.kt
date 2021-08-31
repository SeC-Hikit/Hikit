package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.AccessibilityNotificationDto
import org.sc.common.rest.AccessibilityReportDto
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.AccessibilityNotificationManager
import org.sc.manager.AccessibilityReportManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccessibilityReportValidator @Autowired constructor(
    private val authRealmValidator: AuthRealmValidator,
    private val accessibilityNotificationManager: AccessibilityReportManager
) : Validator<AccessibilityReportDto> {

    companion object {
        const val noParamSpecifiedError = "Empty field '%s'"
        const val dateInFutureError = "Date field with value '%s' is in the future"
    }

    override fun validate(request: AccessibilityReportDto): Set<String> {
        val errors = mutableSetOf<String>()

        if (isEmpty(request.trailId)) {
            errors.add(String.format(noParamSpecifiedError, "Trail ID"))
        }

        if (isEmpty(request.email)) {
            errors.add(String.format(noParamSpecifiedError, "Email"))
        }

        if (isEmpty(request.description)) {
            errors.add(String.format(noParamSpecifiedError, "Description"))
        }

        if (request.reportDate == null || request.reportDate.after(Date()))
            errors.add(String.format(dateInFutureError, request.reportDate.toString()))

        return errors
    }

    fun validateUpdateRequest(id: String): Set<String> {
        val errors = mutableSetOf<String>()
        val byId = accessibilityNotificationManager.byId(id)

        if (byId.isEmpty()) {
            errors.add("Accessibility Report does not exist")
            return errors
        }
        errors.addAll(authRealmValidator.validate(byId.first().recordDetails.realm))
        return errors
    }


}