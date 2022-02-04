package org.sc.data.geo

import org.sc.common.rest.PlaceRefDto
import org.sc.common.rest.TrailCoordinatesDto
import org.sc.data.mapper.PlaceRefMapper
import org.sc.data.mapper.TrailCoordinatesMapper
import org.sc.data.model.PlaceRef
import org.sc.data.model.TrailCoordinates
import org.sc.processor.DistanceProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrailPlacesAligner @Autowired constructor(
         private val trailCoordinatesMapper: TrailCoordinatesMapper,
         private val locationRefMapper: PlaceRefMapper) {

    fun sortLocationsByTrailCoordinatesDto(coordinates: List<TrailCoordinatesDto>,
                                        locations: List<PlaceRefDto>) =
            sortLocationsByTrailCoordinates(coordinates.map { trailCoordinatesMapper.map(it) },
                    locations.map { locationRefMapper.map(it) })

     fun sortLocationsByTrailCoordinates(
             coordinates: List<TrailCoordinates>,
             locations: List<PlaceRef>): List<PlaceRef> =
            // for each location, check closest trail Coordinate distance
            locations.sortedWith(compareBy { pr ->
                val closestCoordinatePoint: TrailCoordinates? =
                        coordinates.minByOrNull { DistanceProcessor.distanceBetweenPoints(pr.coordinates, it) }

                val distance = closestCoordinatePoint!!.distanceFromTrailStart +
                        DistanceProcessor.distanceBetweenPoints(closestCoordinatePoint, pr.coordinates)
                distance
            })
}