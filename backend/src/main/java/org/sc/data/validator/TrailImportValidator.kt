package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.TrailImportDto
import org.sc.data.validator.ValidatorUtils.Companion.emptyFieldError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class TrailImportValidator @Autowired constructor (
    private val coordsValidatorTrail: TrailCoordinatesValidator,
    private val placeRefValidator: PlaceRefValidator): Validator<TrailImportDto> {

    companion object {
        private const val minGeoPoints = 3
        private const val minLocations = 2
        const val emptyListPointError = "No coordinates"
        const val minLocationBoundaryError = "At least $minLocations locations are required"
        const val tooFewPointsError = "At least $minGeoPoints geoPoints should be specified"
        const val dateInFutureError = "The provided date is in the future"
    }

    override fun validate(request: TrailImportDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (request.name == null) {
            errors.add(String.format(emptyFieldError, "Name"))
        }
        if (isEmpty(request.code)) {
            errors.add(String.format(emptyFieldError, "Code"))
        }

        if(request.lastUpdate == null) {
            errors.add("last update date not provided")
        } else {
            if (request.lastUpdate.after(Date())) {
                errors.add(dateInFutureError)
            }
        }
        if(request.locations.size < 2) {
            errors.add(minLocationBoundaryError)
        }

        if(request.coordinates == null || request.coordinates.isEmpty()) errors.add(emptyListPointError)
        if(request.coordinates.size < minGeoPoints) errors.add(tooFewPointsError)
        request.coordinates.map { coordsValidatorTrail.validate(it) }
                .forEach{ errors.addAll(it)}
        return errors
    }
}