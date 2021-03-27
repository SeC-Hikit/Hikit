package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.MaintenanceCreationDto
import org.sc.data.validator.ValidatorUtils.Companion.emptyFieldError
import org.springframework.stereotype.Component
import java.util.*

@Component
class MaintenanceValidator : Validator<MaintenanceCreationDto> {

    companion object {
        const val dateInPast = "Planned date is in the past"
    }

    override fun validate(request: MaintenanceCreationDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (isEmpty(request.contact)) {
            errors.add(String.format(emptyFieldError, "Contact"))
        }
        if (isEmpty(request.trailId)) {
            errors.add(String.format(emptyFieldError, "Code"))
        }
        if (isEmpty(request.meetingPlace)) {
            errors.add(String.format(emptyFieldError, "Meeting Place"))
        }
        if (request.date == null) {
            errors.add(String.format(emptyFieldError, "date"))
        }
        if (request.date.before(Date())) {
            errors.add(dateInPast)
        }
        return errors
    }
}