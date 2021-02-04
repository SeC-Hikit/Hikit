package org.sc.data.validator

import org.sc.common.rest.MaintenanceCreationDto
import org.springframework.stereotype.Component
import java.util.*

@Component
class MaintenanceValidator : Validator<MaintenanceCreationDto> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
        const val dateInPast = "Planned date is in the past"
    }

    override fun validate(request: MaintenanceCreationDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.contact.isBlank()) {
            errors.add(String.format(noParamSpecified, "Contact"))
        }
        if (request.code.isBlank()) {
            errors.add(String.format(noParamSpecified, "Code"))
        }
        if (request.meetingPlace.isBlank()) {
            errors.add(String.format(noParamSpecified, "Meeting Place"))
        }
        if (request.date.before(Date())) {
            errors.add(dateInPast)
        }
        return errors
    }
}