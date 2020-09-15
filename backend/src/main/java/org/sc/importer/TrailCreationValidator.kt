package org.sc.importer

import com.google.gson.JsonSyntaxException
import com.google.inject.Inject
import org.sc.data.Trail
import org.sc.data.helper.GsonBeanHelper
import org.sc.data.validator.Validator
import org.sc.data.validator.Validator.Companion.requestMalformedErrorMessage
import spark.Request

class TrailCreationValidator @Inject constructor (
        private val gsonBeanHelper: GsonBeanHelper) : Validator<Request> {

    companion object {
        const val minGeoPoints = 3

        const val emptyListPointError = "No geo points specified"
        const val tooFewPointsError = "At least $minGeoPoints geoPoints should be specified"
        const val noParamSpecified = "Empty field '%s'"
    }

    override fun validate(request: Request): Set<String> {
        val errors = mutableSetOf<String>()

        val trailRequest = gsonBeanHelper.gsonBuilder!!.fromJson(request.body(), Trail::class.java)
        checkNotNull(trailRequest)
        try {
            if (trailRequest.name.isEmpty()) {
                errors.add(String.format(noParamSpecified, "Name"))
            }

        } catch (e: JsonSyntaxException) {
            errors.add(requestMalformedErrorMessage)
        }
        return errors
    }
}