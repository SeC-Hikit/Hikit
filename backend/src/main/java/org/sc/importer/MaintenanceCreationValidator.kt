package org.sc.importer

import org.sc.common.rest.controller.Maintenance
import org.sc.data.validator.Validator
import org.springframework.stereotype.Component
import java.util.*

@Component
class MaintenanceCreationValidator : Validator<Maintenance> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
        const val dateInPast = "Planned date is in the past"
    }

    override fun validate(maintenance: Maintenance): Set<String> {
        val errors = mutableSetOf<String>()
        if (maintenance.contact.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Contact"))
        }
        if (maintenance.code.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Code"))
        }
        if (maintenance.description.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Description"))
        }
        if (maintenance.meetingPlace.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Meeting Place"))
        }
        if (maintenance.date.before(Date())) {
            errors.add(dateInPast)
        }
        return errors
    }
}