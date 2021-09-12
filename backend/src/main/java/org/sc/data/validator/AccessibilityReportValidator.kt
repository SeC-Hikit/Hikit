package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.AccessibilityReportDto
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.AccessibilityReportManager
import org.sc.manager.TrailManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

@Component
class AccessibilityReportValidator @Autowired constructor(
    private val authRealmValidator: AuthRealmValidator,
    private val accessibilityNotificationManager: AccessibilityReportManager,
    private val trailManager: TrailManager
) : Validator<AccessibilityReportDto> {

    companion object {
        const val emailNotValid = "Field 'mail' is not valid"
        const val noParamSpecifiedError = "Empty field '%s'"
        const val noTrailError = "Trail with id '%s', does not exist"
        const val dateInFutureError = "Date field with value '%s' is in the future"
        val emailRegex: Pattern = compile("^[A-Za-z](.*)([@])(.+)(\\.)(.+)")
    }

    override fun validate(request: AccessibilityReportDto): Set<String> {
        val errors = mutableSetOf<String>()

        val trailId = request.trailId
        if (isEmpty(trailId)) {
            errors.add(String.format(noParamSpecifiedError, "Trail ID"))
            return errors
        }

        if(!trailManager.doesTrailExist(trailId)){
            errors.add(String.format(noTrailError, trailId))
        }

        if (isEmpty(request.email)) {
            errors.add(String.format(noParamSpecifiedError, "Email"))
            return errors
        }

        if(!emailRegex.matcher(request.email).matches()) {
            errors.add(emailNotValid)
            return errors
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