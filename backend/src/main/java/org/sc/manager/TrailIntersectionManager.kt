package org.sc.manager

import org.sc.adapter.AltitudeServiceAdapter
import org.sc.common.rest.TrailIntersectionDto
import org.sc.common.rest.geo.GeoLineDto
import org.sc.data.mapper.TrailIntersectionMapper
import org.sc.data.model.*
import org.sc.data.repository.TrailDAO
import org.sc.processor.GeoCalculator
import org.sc.processor.TrailIntersectionProcessor
import org.sc.processor.TrailSimplifierLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailIntersectionManager @Autowired constructor(
    private val trailDAO: TrailDAO,
    private val trailIntersectionProcessor: TrailIntersectionProcessor,
    private val trailIntersectionMapper: TrailIntersectionMapper,
    private val altitudeServiceAdapter: AltitudeServiceAdapter
) {

    fun findIntersection(geoLineDto: GeoLineDto, skip: Int, limit: Int): List<TrailIntersectionDto> {
        val outerGeoSquare = GeoCalculator.getOuterSquareForCoordinates(geoLineDto.coordinates, 0.001)
        val foundTrailsInGeoSquare = trailDAO.findTrailsWithinGeoSquare(
            outerGeoSquare, skip, limit,
            TrailSimplifierLevel.FULL, true, emptyList()
        )

        return foundTrailsInGeoSquare.filter {
            GeoCalculator.areSegmentsIntersecting(
                geoLineDto.coordinates, it.geoLineString
            )
        }.map { trail ->
            trailIntersectionProcessor.getTrailCrosswayIntersectionPoints(geoLineDto.coordinates, trail)
        }.map {
            toTrailIntersectionWithElevationData(it)
        }
    }

    private fun toTrailIntersectionWithElevationData(trailToIntersectionPoints: Pair<Trail, List<Coordinates2D>>): TrailIntersectionDto {
        val altitudeResultOrderedList =
            altitudeServiceAdapter
                .getElevationsByLongLat(trailToIntersectionPoints.second.map { coord -> Pair(coord.latitude, coord.longitude) })
        val coordinatesForTrail = mutableListOf<Coordinates>()
        trailToIntersectionPoints.second.forEachIndexed { index, coord ->
            coordinatesForTrail.add(
                CoordinatesWithAltitude(
                    coord.latitude, coord.longitude,
                    altitudeResultOrderedList[index]
                )
            )
        }
        return trailIntersectionMapper.map(TrailIntersection(trailToIntersectionPoints.first, coordinatesForTrail))
    }

}