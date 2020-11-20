package org.sc.importer

import org.sc.data.TrailImport
import org.sc.data.validator.Validator
import org.springframework.stereotype.Component

@Component
class TrailCreationValidator : Validator<TrailImport> {

    companion object {
        private const val minGeoPoints = 3

        const val emptyListPointError = "No geo points specified"
        const val tooFewPointsError = "At least $minGeoPoints geoPoints should be specified"
        const val noParamSpecified = "Empty field '%s'"
    }

    override fun validate(request: TrailImport): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.name.isEmpty()) {
            errors.add(String.format(noParamSpecified, "Name"))
        }
        return errors
    }
}