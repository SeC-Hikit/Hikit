package org.sc.service

import org.sc.adapter.AltitudeServiceAdapter
import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.TrailIntersectionDto
import org.sc.common.rest.geo.GeoLineDto
import org.sc.data.CoordinatesMapper
import org.sc.data.mapper.TrailIntersectionMapper
import org.sc.data.model.*
import org.sc.data.repository.TrailDAO
import org.sc.processor.GeoCalculator
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired

class TrailIntersectionService @Autowired constructor(private val trailDAO: TrailDAO,
                                                      private val altitudeAdapter: AltitudeServiceAdapter,
                                                      private val coordinatesMapper: CoordinatesMapper,
                                                      private val trailIntersectionMapper: TrailIntersectionMapper) {

    fun findIntersectionsByCoordsDto(coordinates: List<CoordinatesDto>) : List<TrailIntersectionDto> =
            findIntersectionsByCoords2D(coordinatesMapper.toCoordinates2D(coordinates))

    fun findIntersection(geoLineDto: GeoLineDto, skip: Int, limit: Int): List<TrailIntersectionDto> {
        val outerGeoSquare = GeoCalculator.getOuterSquareForCoordinates(geoLineDto.coordinates)
        val foundTrailsWithinGeoSquare = trailDAO.findTrailWithinGeoSquare(outerGeoSquare, skip, limit,
                TrailSimplifierLevel.FULL, true)

        return foundTrailsWithinGeoSquare.filter {
            GeoCalculator.areSegmentsIntersecting(
                    geoLineDto.coordinates, it.geoLineString
            )
        }.map { trail ->
            getTrailIntersection(geoLineDto.coordinates, trail)
        }
    }

    private fun getTrailIntersection(coordinates: List<Coordinates2D>, trail: Trail): TrailIntersectionDto {
        val coordinates2D = GeoCalculator.getIntersectionPointsBetweenSegments(
                coordinates, trail.geoLineString
        )
        val altitudeResultOrderedList =
                altitudeAdapter.getElevationsByLongLat(coordinates2D.map { coordinatePair -> Pair(coordinatePair.latitude, coordinatePair.longitude) })

        val coordinatesForTrail = mutableListOf<Coordinates>()
        coordinates2D.forEachIndexed { index, coordinatePair ->
            coordinatesForTrail.add(
                    CoordinatesWithAltitude(
                            coordinatePair.latitude, coordinatePair.longitude,
                            altitudeResultOrderedList[index]
                    )
            )
        }
        return trailIntersectionMapper.map(TrailIntersection(trail, coordinatesForTrail))
    }

    private fun findIntersectionsByCoords2D(coordinates: List<Coordinates2D>) : List<TrailIntersectionDto> =
            findIntersection(GeoLineDto(coordinates), 0, Int.MAX_VALUE)
}