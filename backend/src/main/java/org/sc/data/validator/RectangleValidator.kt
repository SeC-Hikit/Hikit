package org.sc.data.validator

import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.geo.RectangleDto
import org.sc.processor.DistanceProcessor
import org.springframework.stereotype.Component

@Component
class RectangleValidator constructor(private val coordinatesValidator: CoordinatesValidator) : Validator<RectangleDto> {

    companion object {
        const val DIAGONAL_ONE_HUNDRED_FIFTY_KM = 150000
        const val diagonalLengthError = "Diagonal between selected vertexes is greater than 150 km!"
    }

    override fun validate(request: RectangleDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (DistanceProcessor.getRadialDistance(request.bottomLeft.latitude, request.bottomLeft.longitude,
                        request.topRight.latitude, request.topRight.longitude) > DIAGONAL_ONE_HUNDRED_FIFTY_KM) {
            errors.add(diagonalLengthError)
        }

        val errorsOnCoordinates = listOf(request.bottomLeft, request.topRight).flatMap {
            coordinatesValidator
                    .validate(CoordinatesDto(it.latitude, it.longitude))
        }

        errors.addAll(errorsOnCoordinates)

        return errors
    }
}