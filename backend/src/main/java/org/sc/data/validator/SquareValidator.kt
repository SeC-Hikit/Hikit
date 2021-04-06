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
        const val topBottomLeftError = "Distance bottom-top left vertex is greater than 60 km!"
        const val topBottomRightError = "Distance bottom-top right vertex is greater than 60 km!"
    }

    override fun validate(request: SquareDto): Set<String> {
        val errors = mutableSetOf<String>()
        if (DistanceProcessor.getRadialDistance(request.bottomLeft.latitude,request.bottomLeft.longitude,
                        request.topLeft.latitude,request.topLeft.longitude) > 60000)
                        {errors.add(topBottomLeftError)}
        if (DistanceProcessor.getRadialDistance(request.bottomRight.latitude,request.bottomRight.longitude,
                        request.topRight.latitude,request.topRight.longitude) > 60000)
                        {errors.add(topBottomRightError)}

        return errors
    }
}