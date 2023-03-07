package org.sc.processor

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import org.sc.data.geo.CoordinatesRectangle
import org.sc.data.model.Coordinates2D
import org.sc.data.model.GeoLineString

private const val fiftyMeter = 500

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

    fun getIntersectionPointsBetweenSegments(subjectSegment: List<Coordinates2D>, foundSegment: GeoLineString)
            : List<Coordinates2D> {
        val subjectMappedCoordinates = mapToCoords(subjectSegment)
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

    fun electPossibleCrossway(
        it: Coordinates2D,
        intersectingPoints: List<Coordinates2D>
    ): Boolean {

        val indexOf = intersectingPoints.indexOf(it)

        val nextIndex = indexOf + 1
        val previousIndex = indexOf - 1

        val nextElementOrNull = intersectingPoints.elementAtOrNull(nextIndex)
        val previousElementOrNull = intersectingPoints.elementAtOrNull(previousIndex)

        val isIsolated = nextElementOrNull == null &&
                previousElementOrNull == null

        if (isIsolated) return true

        val isCrosswayOfAStartingOverlay = nextElementOrNull != null &&
                intersectingPoints.elementAtOrNull(previousIndex) == null

        if (isCrosswayOfAStartingOverlay) {
            return true
        }

        val isCrosswayOfAnEndingOverlay = nextElementOrNull == null && intersectingPoints.elementAtOrNull(previousIndex) != null

        if (isCrosswayOfAnEndingOverlay) {
            return true
        }

        val isNextCrossingSpanningSetDistance = nextElementOrNull != null &&
                getRadialDistance(it, nextElementOrNull) >= fiftyMeter
        if (isNextCrossingSpanningSetDistance
        ) {
            return true
        }

        val isPreviousCrossingSpanningSetDistance = previousElementOrNull != null &&
                getRadialDistance(it, previousElementOrNull) >= fiftyMeter
        if (isPreviousCrossingSpanningSetDistance) {
            return true
        }

        return false
    }

    private fun getRadialDistance(it: Coordinates2D, nextElementOrNull: Coordinates2D) =
        DistanceProcessor.getRadialDistance(
            lat1 = it.latitude, lon1 = it.longitude,
            lat2 = nextElementOrNull.latitude, lon2 = nextElementOrNull.longitude
        )

    private fun mapToCoords(subjectSegment: List<Coordinates2D>) =
        subjectSegment.map { Coordinate(it.longitude, it.latitude) }
}