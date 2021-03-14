package org.sc.manager

import io.jenetics.jpx.GPX
import io.jenetics.jpx.Metadata
import org.sc.common.rest.*
import org.sc.configuration.AppProperties
import org.sc.configuration.AppProperties.VERSION
import org.sc.data.mapper.CoordinatesMapper
import org.sc.data.mapper.TrailCoordinatesMapper
import org.sc.data.model.TrailCoordinates
import org.sc.data.model.Trail
import org.sc.processor.TrailsCalculator
import org.sc.service.AltitudeServiceAdapter
import org.sc.processor.GpxFileHandlerHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path

@Component
class GpxManager @Autowired constructor(
    private val gpxFileHandlerHelper: GpxFileHandlerHelper,
    private val trailsCalculator: TrailsCalculator,
    private val altitudeService: AltitudeServiceAdapter,
    private val trailCoordinatesMapper: TrailCoordinatesMapper,
    private val coordinatesMapper: CoordinatesMapper,
    appProps: AppProperties
) {

    private val pathToStoredFiles = File(appProps.trailStorage).toPath()
    private val emptyDefaultString = ""

    fun getTrailPreparationFromGpx(tempFile: Path): TrailPreparationModelDto {
        val gpx = gpxFileHandlerHelper.readFromFile(tempFile)
        val track = gpx.tracks.first()
        val segment = track.segments.first()

        val coordinatesWithAltitude: List<CoordinatesDto> = segment.points.map { point ->
            CoordinatesDto(
                point.longitude.toDegrees(), point.latitude.toDegrees(),
                altitudeService.getAltitudeByLongLat(point.latitude.toDegrees(), point.longitude.toDegrees())
            )
        }

        val trailCoordinates = coordinatesWithAltitude.map {
            TrailCoordinates(
                it.longitude, it.latitude, it.altitude,
                trailsCalculator.calculateLengthFromTo(coordinatesWithAltitude, it)
            )
        }

        return TrailPreparationModelDto(
            track.name.orElse(emptyDefaultString),
            track.description.orElse(emptyDefaultString),
            PlaceDto(
                "", "", "",
                emptyList(), emptyList(),
                listOf(coordinatesMapper.trailCoordsToDto(trailCoordinates.first())),
                emptyList()
            ),
            PlaceDto(
                "", "", "",
                emptyList(), emptyList(),
                listOf(coordinatesMapper.trailCoordsToDto(trailCoordinates.last())),
                emptyList()
            ),
            trailCoordinates.map { trailCoordinatesMapper.map(it) }
        )
    }

    fun writeTrailToGpx(trail: Trail) {
        val creator = "S&C_$VERSION"
        val gpx = GPX.builder(creator)
            .addTrack { track ->
                track.addSegment { segment ->
                    trail.coordinates.forEach {
                        segment.addPoint { p ->
                            p.lat(it.latitude).lon(it.longitude).ele(it.altitude)
                        }
                    }
                }
            }.metadata(
                Metadata.builder()
                    .author("S&C - $creator")
                    .name(trail.code).time(trail.lastUpdate.toInstant()).build()
            ).build()
        gpxFileHandlerHelper.writeToFile(gpx, pathToStoredFiles.resolve(trail.code + ".gpx"))
    }

}