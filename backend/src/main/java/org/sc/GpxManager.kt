package org.sc

import com.google.inject.Inject
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Metadata
import org.sc.configuration.AppProperties
import org.sc.configuration.ConfigurationProperties
import org.sc.data.CoordinatesWithAltitude
import org.sc.data.Trail
import org.sc.data.TrailPreparationModel
import org.sc.service.AltitudeServiceHelper
import java.io.File
import java.nio.file.Path

class GpxManager @Inject constructor(private val gpxFileHandlerHelper: GpxFileHandlerHelper,
                                     private val altitudeService: AltitudeServiceHelper,
                                     private val appProps: AppProperties) {

    private val pathToStoredFiles = File(appProps.pathToGpxDirectory).toPath()
    private val emptyDefaultString = ""

    fun getTrailPreparationFromGpx(tempFile: Path): TrailPreparationModel? {
        val gpx = gpxFileHandlerHelper.readFromFile(tempFile)
        val track = gpx.tracks.first()
        val segment = track.segments.first()

        val coordinatesWithAltitude = segment.points.map { point ->
            CoordinatesWithAltitude(point.longitude.toDegrees(), point.latitude.toDegrees(),
                    altitudeService.getAltitudeByLongLat(point.latitude.toDegrees(), point.longitude.toDegrees()))
        }

        return TrailPreparationModel(
                track.name.orElse(emptyDefaultString),
                track.description.orElse(emptyDefaultString),
                coordinatesWithAltitude.first(),
                coordinatesWithAltitude.last(),
                coordinatesWithAltitude
        )
    }

    fun writeTrailToGpx(trail: Trail) {
        val creator = "S&C_BO_" + ConfigurationProperties.VERSION
        val gpx = GPX.builder(creator)
                .addTrack { track ->
                    track.addSegment { segment ->
                        trail.coordinates.forEach {
                            segment.addPoint {
                                p -> p.lat(it.latitude).lon(it.longitude).ele(it.altitude)
                            }
                        }
                    }
                }.metadata(
                        Metadata.builder()
                                .author("CAI Bologna - $creator")
                                .name(trail.code).time(trail.date.toInstant()).build()
                ).build()
        gpxFileHandlerHelper.writeToFile(gpx, pathToStoredFiles.resolve(trail.code + ".gpx"))
    }

}