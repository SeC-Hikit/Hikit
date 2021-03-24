package org.sc.util.coordinates

import org.sc.data.model.Coordinates

object CoordinatesUtil {
    fun getLongLatFromCoordinates(coordinates: Coordinates): List<Double> =
        listOf(coordinates.longitude, coordinates.latitude)
}