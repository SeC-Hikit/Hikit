package org.sc.processor

import org.sc.data.model.Coordinates2D
import org.sc.data.model.Trail
import org.springframework.stereotype.Component

@Component
class TrailIntersectionProcessor {

    companion object {
        private const val DISTANCE_THRESHOLD_BETWEEN_SAME_TRAIL_CROSSWAYS = 250
    }

    fun getTrailCrosswayIntersectionPoints(coordinates: List<Coordinates2D>, trail: Trail): Pair<Trail, List<Coordinates2D>> {
        val intersectingPoints = GeoCalculator.getIntersectionPointsBetweenSegments(
            coordinates, trail.geoLineString
        )

        val selectedCoordinates = intersectingPoints.filter {
            isIntersectionElectableCrossway(it, intersectingPoints)
        }

        return Pair(trail, selectedCoordinates)
    }

    fun getIntersectionPointsBetweenSegments(trail: Trail, municipalityCoords: List<Coordinates2D>): Pair<Trail, List<Coordinates2D>> {
        val intersectingPoints = GeoCalculator.getIntersectionPointsBetweenSegments(
            trail.coordinates.map { Coordinates2D(it.longitude, it.latitude) }, municipalityCoords
        )

//        val selectedCoordinates = intersectingPoints.filter {
//            isIntersectionElectableCrossway(it, intersectingPoints)
//        }

        return Pair(trail, intersectingPoints)
    }

    private fun isIntersectionElectableCrossway(
        it: Coordinates2D,
        intersectingPoints: List<Coordinates2D>
    ): Boolean {

        val indexOf = intersectingPoints.indexOf(it)

        val nextIndex = indexOf + 1
        val previousIndex = indexOf - 1

        val nextElementOrNull = intersectingPoints.elementAtOrNull(nextIndex)
        val previousElementOrNull = intersectingPoints.elementAtOrNull(previousIndex)

        val isIntersectionIsolated =
            nextElementOrNull == null &&
                    previousElementOrNull == null

        if (isIntersectionIsolated) return true

        val isCrosswayAtAOverlayStart = nextElementOrNull != null &&
                intersectingPoints.elementAtOrNull(previousIndex) == null

        if (isCrosswayAtAOverlayStart) {
            return true
        }

        val isCrosswayAtAOverlayEnd = nextElementOrNull == null && intersectingPoints.elementAtOrNull(previousIndex) != null

        if (isCrosswayAtAOverlayEnd) {
            return true
        }

        val isNextCrossingSpanningSetDistance = nextElementOrNull != null &&
                getRadialDistance(it, nextElementOrNull) >= DISTANCE_THRESHOLD_BETWEEN_SAME_TRAIL_CROSSWAYS

        if (isNextCrossingSpanningSetDistance) {
            return true
        }

        val isPreviousCrossingSpanningSetDistance = previousElementOrNull != null &&
                getRadialDistance(it, previousElementOrNull) >= DISTANCE_THRESHOLD_BETWEEN_SAME_TRAIL_CROSSWAYS
        return isPreviousCrossingSpanningSetDistance
    }

    private fun getRadialDistance(it: Coordinates2D, nextElementOrNull: Coordinates2D) =
        DistanceProcessor.getRadialDistance(
            lat1 = it.latitude, lon1 = it.longitude,
            lat2 = nextElementOrNull.latitude, lon2 = nextElementOrNull.longitude
        )

}