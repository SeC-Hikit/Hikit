package org.sc.importer

import com.google.gson.JsonSyntaxException
import com.google.inject.Inject
import org.sc.common.rest.controller.AccessibilityNotification
import org.sc.common.rest.controller.helper.GsonBeanHelper
import org.sc.data.validator.Validator
import org.sc.data.validator.Validator.Companion.requestMalformedErrorMessage
import spark.Request

class AccessibilityCreationValidator @Inject constructor (
        private val gsonBeanHelper: GsonBeanHelper) : Validator<Request> {

    companion object {
        const val noParamSpecified = "Empty field '%s'"
    }

    override fun validate(request: Request): Set<String> {
        val errors = mutableSetOf<String>()

        val accessibilityNotification = gsonBeanHelper.gsonBuilder!!.fromJson(request.body(), AccessibilityNotification::class.java)
        checkNotNull(accessibilityNotification)
        try {
            if (accessibilityNotification.code.isEmpty()) {
                errors.add(String.format(noParamSpecified, "Code"))
            }
            if(accessibilityNotification.description.isEmpty()) {
                errors.add(String.format(noParamSpecified, "Description"))
            }
        } catch (e: JsonSyntaxException) {
            errors.add(requestMalformedErrorMessage)
        }
        return errors
    }
}