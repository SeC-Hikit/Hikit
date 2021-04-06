package org.sc.data.validator

import org.apache.commons.lang3.StringUtils.isEmpty
import org.sc.common.rest.AccessibilityNotificationCreationDto
import org.sc.common.rest.geo.SquareDto
import org.sc.data.model.Coordinates2D
import org.sc.processor.DistanceProcessor
import org.springframework.stereotype.Component
import java.util.*

@Component
class SquareValidator : Validator<SquareDto> {

    companion object {
        const val vertexDistanceError = "Distance between selected vertexes is greater than 100 km!"
    }

    override fun validate(request: SquareDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (DistanceProcessor.getRadialDistance(request.bottomLeft.latitude,request.bottomLeft.longitude,
                        request.topLeft.latitude,request.topLeft.longitude) > 100000)
                        {errors.add(vertexDistanceError)}
        if (DistanceProcessor.getRadialDistance(request.bottomRight.latitude,request.bottomRight.longitude,
                        request.topRight.latitude,request.topRight.longitude) > 100000)
                        {errors.add(vertexDistanceError)}
        if (DistanceProcessor.getRadialDistance(request.bottomRight.latitude,request.bottomRight.longitude,
                        request.bottomLeft.latitude,request.bottomLeft.longitude) > 100000)
                         {errors.add(vertexDistanceError)}
        if (DistanceProcessor.getRadialDistance(request.topLeft.latitude,request.topLeft.longitude,
                        request.topRight.latitude,request.topRight.longitude) > 100000)
                        {errors.add(vertexDistanceError)}
        return errors
    }
}