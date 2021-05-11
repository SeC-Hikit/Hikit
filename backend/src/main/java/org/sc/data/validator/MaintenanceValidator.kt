package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.MaintenanceDto
import org.sc.data.validator.ValidatorUtils.Companion.emptyFieldError
import org.sc.data.validator.auth.AuthRealmValidator
import org.sc.manager.MaintenanceManager
import org.springframework.stereotype.Component
import java.util.*


@Component
class MaintenanceValidator constructor(
    private val maintenanceManager: MaintenanceManager,
    private val realmValidator: AuthRealmValidator
) : Validator<MaintenanceDto> {

    companion object {
        const val dateInPast = "Planned date is in the past"
    }

    override fun validate(request: MaintenanceDto): Set<String> {
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

    fun validateExistenceAndRealm(id: String): Set<String> {
        val maintenance = maintenanceManager.getById(id);
        if (maintenance.isEmpty()) {
            return mutableSetOf(String.format(ValidatorUtils.notExistingItem, "Maintenance", id))
        }
        val errors = mutableSetOf<String>()
        val isAdminRealm =
            realmValidator.isAdminSameRealmAsResource(maintenance.first().recordDetails.realm)
        if (!isAdminRealm) {
            errors.add(AuthRealmValidator.NOT_ALLOWED_MSG)
        }
        return errors
    }

}