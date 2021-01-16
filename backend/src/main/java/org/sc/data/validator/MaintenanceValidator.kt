package org.sc.data.validator

import org.sc.common.rest.Maintenance
import org.springframework.stereotype.Component
import java.util.*

@Component
class MaintenanceValidator : Validator<Maintenance> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
        const val dateInPast = "Planned date is in the past"
    }

    override fun validate(request: Maintenance): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.contact.isNullOrBlank()) {
            errors.add(String.format(noParamSpecified, "Contact"))
        }
        if (request.code.isNullOrBlank()) {
            errors.add(String.format(noParamSpecified, "Code"))
        }
        if (request.meetingPlace.isNullOrBlank()) {
            errors.add(String.format(noParamSpecified, "Meeting Place"))
        }
        if (request.date.before(Date())) {
            errors.add(dateInPast)
        }
        return errors
    }
}