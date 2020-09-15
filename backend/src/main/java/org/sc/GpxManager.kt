package org.sc

import com.google.inject.Inject
import org.sc.data.CoordinatesWithAltitude
import org.sc.data.TrailPreparationModel
import org.sc.service.AltitudeServiceHelper
import java.nio.file.Path

class GpxManager @Inject constructor(private val gpxHelper: GpxHelper,
                                     private val altitudeService: AltitudeServiceHelper) {

    private val emptyDefaultString = ""

    fun getTrailPreparationFromGpx(tempFile: Path): TrailPreparationModel? {
        val gpx = gpxHelper.readFromFile(tempFile)
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

}