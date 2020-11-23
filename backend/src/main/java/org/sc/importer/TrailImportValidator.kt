package org.sc.importer

import org.sc.data.TrailImport
import org.sc.data.validator.PositionCreationValidator
import org.sc.data.validator.TrailCoordinatesCreationValidator
import org.sc.data.validator.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
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
    }

    override fun validate(request: TrailImport): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.name.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Name"))
        }
        if (request.code.isEmpty()) {
            errors.add(String.format(noParamSpecified, "code"))
        }

        val givenTime = LocalDate.from(request.date.toInstant())
        val tomorrow = LocalDate.from(Date().toInstant()).plusDays(1).atStartOfDay().toLocalDate()
        if (givenTime.isAfter(tomorrow)){
            errors.add(dateInFutureError)
        }
        errors.addAll(positionValidator.validate(request.startPos))
        errors.addAll(positionValidator.validate(request.finalPos))
        if(request.coordinates.isEmpty()) errors.add(emptyListPointError)
        if(request.coordinates.size < minGeoPoints) errors.add(tooFewPointsError)
        request.coordinates.map { coordsValidatorTrail.validate(it) }
                .forEach{ errors.addAll(it)}
        return errors
    }
}