package org.sc.processor

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.model.Coordinates2D
import org.sc.data.model.GeoLineString

object GeoCalculator {

    private val geometryFactory = GeometryFactory()

    fun getOuterSquareForCoordinates(
        coordinates2D: List<Coordinates2D>,
        paddingDistance: Double = 0.0
    ): CoordinatesRectangle {
        val topRight = Coordinates2D(coordinates2D.maxOf { it.longitude + paddingDistance },
            coordinates2D.maxOf { it.latitude + paddingDistance })
        val bottomLeft = Coordinates2D(coordinates2D.minOf { it.longitude - paddingDistance },
            coordinates2D.minOf { it.latitude - paddingDistance })
        return CoordinatesRectangle(bottomLeft, topRight)
    }

    fun areSegmentsIntersecting(subjectSegment: List<Coordinates2D>, foundSegment: GeoLineString): Boolean {
        val subjectMappedCoordinates = mapToCoords(subjectSegment)
        val foundSegmentCoordinates = mapToCoords(foundSegment.coordinates)
        val subjectSegmentEuclidean = geometryFactory.createLineString(subjectMappedCoordinates.toTypedArray())
        val targetSegmentEuclidean = geometryFactory.createLineString(foundSegmentCoordinates.toTypedArray())

        return subjectSegmentEuclidean.intersects(targetSegmentEuclidean)
    }

    fun isPointWithinPolygon(subject: Coordinates2D, polygons: List<Pair<String, List<Coordinates2D>>>): Pair<String, List<Coordinates2D>> {
        val createPoint = geometryFactory.createPoint(Coordinate(subject.longitude, subject.latitude))
        for (shape in polygons) {
            val mapToCoords = mapToCoords(shape.second)
            val polygon = geometryFactory.createPolygon(mapToCoords.toTypedArray())
            if(polygon.intersects(createPoint)) {
                return shape
            }
        }
        return Pair("unknown", emptyList())
    }

    fun getIntersectionPointsBetweenSegments(subjectSegment: List<Coordinates2D>, foundSegment: GeoLineString)
            : List<Coordinates2D> {
        val subjectMappedCoordinates = mapToCoords(subjectSegment)
        return getIntersectionPointsBetweenSegments(foundSegment, subjectMappedCoordinates)
    }

    fun getIntersectionPointsBetweenSegments(
        subject: List<Coordinates2D>,
        otherLine: List<Coordinates2D>
    ): List<Coordinates2D> {
        val subjectMappedCoordinates = mapToCoords(otherLine)
        return getIntersectionPointsBetweenSegments(
            GeoLineString(subject),
            subjectMappedCoordinates)
    }


    private fun getIntersectionPointsBetweenSegments(
        foundSegment: GeoLineString,
        subjectMappedCoordinates: List<Coordinate>
    ): List<Coordinates2D> {
        val foundSegmentCoordinates = mapToCoords(foundSegment.coordinates)
        val subjectSegmentEuclidean = geometryFactory.createLineString(subjectMappedCoordinates.toTypedArray())
        val targetSegmentEuclidean = geometryFactory.createLineString(foundSegmentCoordinates.toTypedArray())

        return subjectSegmentEuclidean.intersection(targetSegmentEuclidean).coordinates.map {
            Coordinates2D(
                it.x,
                it.y
            )
        }
    }

    private fun mapToCoords(subjectSegment: List<Coordinates2D>) =
        subjectSegment.map { Coordinate(it.longitude, it.latitude) }
}