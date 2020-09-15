package org.sc.data.validator

import com.google.gson.JsonSyntaxException
import com.google.inject.Inject
import org.sc.data.Trail
import org.sc.data.helper.GsonBeanHelper
import org.sc.data.validator.Validator.Companion.requestMalformedErrorMessage
import spark.Request

class TrailCreationValidator @Inject constructor (
        private val gsonBeanHelper: GsonBeanHelper,
        private val coordsValidator: CoordinatesWithAltitudeCreationValidator,
        private val positionValidator: PositionCreationValidator) : Validator<Request> {

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
            if (trailRequest.code.isEmpty()) {
                errors.add(String.format(noParamSpecified, "Code"))
            }
            if (trailRequest.coordinates.isEmpty()) {
                errors.add(emptyListPointError)
            }
            if (trailRequest.coordinates.size < minGeoPoints) {
                errors.add(tooFewPointsError)
            }

            val errorsStartPos = positionValidator.validate(trailRequest.startPos)
            errors.addAll(errorsStartPos)

            val errorsFinalPos = positionValidator.validate(trailRequest.finalPos)
            errors.addAll(errorsFinalPos)

            val coordinatesSetErrors = trailRequest.coordinates
                    .map { coordsValidator.validate(it) }
            coordinatesSetErrors.forEach { errors.addAll(it) }

        } catch (e: JsonSyntaxException) {
            errors.add(requestMalformedErrorMessage)
        }
        return errors
    }
}