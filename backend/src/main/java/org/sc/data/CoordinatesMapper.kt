package org.sc.data

import org.sc.data.model.Coordinates
import org.sc.data.model.Coordinates2D

class CoordinatesMapper {
    fun toCoordinates2D(coordinates: List<Coordinates>) : List<Coordinates2D> =
            coordinates.map { Coordinates2D(it.longitude, it.latitude) }
}