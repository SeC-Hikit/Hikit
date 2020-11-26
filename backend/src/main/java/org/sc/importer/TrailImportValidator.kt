package org.sc.importer

import org.sc.data.TrailImport
import org.sc.data.validator.PositionCreationValidator
import org.sc.data.validator.TrailCoordinatesCreationValidator
import org.sc.data.validator.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailImportValidator @Autowired constructor (
        private val coordsValidatorTrail: TrailCoordinatesCreationValidator,
        private val positionValidator: PositionCreationValidator): Validator<TrailImport> {

    companion object {
        private const val minGeoPoints = 3
        const val emptyListPointError = "No coordinates"
        const val tooFewPointsError = "At least $minGeoPoints geoPoints should be specified"
        const val noParamSpecified = "Empty field '%s'"
        const val dateInFutureError = "The provided date is in the future"
        const val posToTrailCoordError = "First position element does not match the first coordinate"
        const val lastPosToTrailCoordError = "Last position element does not match the first coordinate"
    }

    override fun validate(request: TrailImport): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.name.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Name"))
        }
        if (request.code.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Code"))
        }

        if(request.lastUpdate == null) {
            errors.add("last update date not provided")
        } else {
            if (request.lastUpdate.after(Date())) {
                errors.add(dateInFutureError)
            }
        }
        if (request.startPos == null || request.finalPos == null ) {
            errors.add("The initial or final position is not set");
            return errors
        }
        if (request.startPos.coordinates != request.coordinates.first()) errors.add(posToTrailCoordError)
        if (request.finalPos.coordinates != request.coordinates.last()) errors.add(lastPosToTrailCoordError)

        errors.addAll(positionValidator.validate(request.startPos))
        errors.addAll(positionValidator.validate(request.finalPos))
        if(request.coordinates.isEmpty()) errors.add(emptyListPointError)
        if(request.coordinates.size < minGeoPoints) errors.add(tooFewPointsError)
        request.coordinates.map { coordsValidatorTrail.validate(it) }
                .forEach{ errors.addAll(it)}
        return errors
    }
}