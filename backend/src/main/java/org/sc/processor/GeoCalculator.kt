package org.sc.processor

import org.sc.data.geo.CoordinatesSquare
import org.sc.data.model.Coordinates2D
import org.sc.data.model.GeoLineString

object GeoCalculator {

    fun getOuterSquareForCoordinates(coordinates2D: List<Coordinates2D>): CoordinatesSquare {
        val topLeft = Coordinates2D(coordinates2D.minOf { it.longitude }, coordinates2D.maxOf { it.latitude })
        val topRight = Coordinates2D(coordinates2D.maxOf { it.longitude }, coordinates2D.maxOf { it.latitude })
        val bottomLeft = Coordinates2D(coordinates2D.minOf { it.longitude }, coordinates2D.minOf { it.latitude })
        val bottomRight = Coordinates2D(coordinates2D.maxOf { it.longitude }, coordinates2D.minOf { it.latitude })
        return CoordinatesSquare(topLeft, topRight, bottomLeft, bottomRight)
    }

    fun areGeometriesIntersecting(coordinates: MutableList<Coordinates2D>, geoLineString: GeoLineString): Boolean {
        throw NotImplementedError()
    }

    fun getIntersectionPointBetweenTrails(coordinates: MutableList<Coordinates2D>, geoLineString: GeoLineString): Coordinates2D {
        throw NotImplementedError()
    }


}