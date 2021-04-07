package org.sc.data.validator

import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.geo.SquareDto
import org.sc.processor.DistanceProcessor
import org.springframework.stereotype.Component

@Component
class SquareValidator constructor(private val coordinatesValidator: CoordinatesValidator) : Validator<SquareDto> {

    companion object {
        const val UPPER_BOUND_ONE_HUNDRED_KM = 100000
        const val vertexDistanceError = "Distance between selected vertexes is greater than 100 km!"
    }

    override fun validate(request: SquareDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (DistanceProcessor.getRadialDistance(request.bottomLeft.latitude, request.bottomLeft.longitude,
                        request.topLeft.latitude, request.topLeft.longitude) > UPPER_BOUND_ONE_HUNDRED_KM) {
            errors.add(vertexDistanceError)
        }
        if (DistanceProcessor.getRadialDistance(request.bottomRight.latitude, request.bottomRight.longitude,
                        request.topRight.latitude, request.topRight.longitude) > UPPER_BOUND_ONE_HUNDRED_KM) {
            errors.add(vertexDistanceError)
        }
        if (DistanceProcessor.getRadialDistance(request.bottomRight.latitude, request.bottomRight.longitude,
                        request.bottomLeft.latitude, request.bottomLeft.longitude) > UPPER_BOUND_ONE_HUNDRED_KM) {
            errors.add(vertexDistanceError)
        }
        if (DistanceProcessor.getRadialDistance(request.topLeft.latitude, request.topLeft.longitude,
                        request.topRight.latitude, request.topRight.longitude) > UPPER_BOUND_ONE_HUNDRED_KM) {
            errors.add(vertexDistanceError)
        }

        val errorsOnCoordinates = listOf(request.bottomLeft, request.topLeft,
                request.topRight, request.bottomRight).flatMap {
            coordinatesValidator
                    .validate(CoordinatesDto(it.latitude, it.longitude))
        }

        errors.addAll(errorsOnCoordinates)

        return errors
    }
}