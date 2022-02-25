package org.sc.processor

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.model.Coordinates2D
import org.sc.data.model.GeoLineString

object GeoCalculator {

    private val geometryFactory = GeometryFactory()

    fun getOuterSquareForCoordinates(coordinates2D: List<Coordinates2D>): CoordinatesRectangle {
        val topRight = Coordinates2D(coordinates2D.maxOf { it.longitude }, coordinates2D.maxOf { it.latitude })
        val bottomLeft = Coordinates2D(coordinates2D.minOf { it.longitude }, coordinates2D.minOf { it.latitude })
        return CoordinatesRectangle(bottomLeft,topRight)
    }

    fun areSegmentsIntersecting(subjectSegment: List<Coordinates2D>, foundSegment: GeoLineString): Boolean {
        val subjectMappedCoordinates = subjectSegment.map { Coordinate(it.longitude, it.latitude) }
        val foundSegmentCoordinates = foundSegment.coordinates.map { Coordinate(it.longitude, it.latitude) }
        val subjectSegmentEuclidean = geometryFactory.createLineString(subjectMappedCoordinates.toTypedArray())
        val targetSegmentEuclidean = geometryFactory.createLineString(foundSegmentCoordinates.toTypedArray())

        return subjectSegmentEuclidean.intersects(targetSegmentEuclidean)
    }

    fun getIntersectionPointsBetweenSegments(subjectSegment: List<Coordinates2D>, foundSegment: GeoLineString)
            : List<Coordinates2D> {
        val subjectMappedCoordinates = subjectSegment.map { Coordinate(it.longitude, it.latitude) }
        val foundSegmentCoordinates = foundSegment.coordinates.map { Coordinate(it.longitude, it.latitude) }
        val subjectSegmentEuclidean = geometryFactory.createLineString(subjectMappedCoordinates.toTypedArray())
        val targetSegmentEuclidean = geometryFactory.createLineString(foundSegmentCoordinates.toTypedArray())

        return subjectSegmentEuclidean.intersection(targetSegmentEuclidean).coordinates.map { Coordinates2D(it.x, it.y) }
    }


}