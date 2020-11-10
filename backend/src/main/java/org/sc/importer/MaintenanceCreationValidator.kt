package org.sc.importer

import com.google.gson.JsonSyntaxException
import com.google.inject.Inject
import org.sc.common.rest.controller.Maintenance
import org.sc.common.rest.controller.helper.GsonBeanHelper
import org.sc.data.validator.Validator
import org.sc.data.validator.Validator.Companion.requestMalformedErrorMessage
import spark.Request
import java.util.*

class MaintenanceCreationValidator @Inject constructor (
        private val gsonBeanHelper: GsonBeanHelper) : Validator<Request> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
        const val dateInPast = "Planned date is in the past"
    }

    override fun validate(request: Request): Set<String> {
        val errors = mutableSetOf<String>()

        val maintenance = gsonBeanHelper.gsonBuilder!!.fromJson(request.body(), Maintenance::class.java)
        checkNotNull(maintenance)
        try {
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

        } catch (e: JsonSyntaxException) {
            errors.add(requestMalformedErrorMessage)
        }
        return errors
    }
}