package org.sc.data.validator

import org.sc.common.rest.controller.Trail
import org.springframework.beans.factory.annotation.Autowired

class TrailCreationValidator @Autowired constructor (
        private val coordsValidator: CoordinatesWithAltitudeCreationValidator,
        private val positionValidator: PositionCreationValidator) : Validator<Trail> {

    companion object {
        const val minGeoPoints = 3

        const val emptyListPointError = "No geo points specified"
        const val tooFewPointsError = "At least $minGeoPoints geoPoints should be specified"
        const val noParamSpecified = "Empty field '%s'"
    }

    override fun validate(trailRequest: Trail): Set<String> {

        val errors = mutableSetOf<String>()
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
        return errors
    }
}