package org.sc.manager

import io.jenetics.jpx.GPX
import io.jenetics.jpx.Metadata
import org.sc.common.rest.CoordinatesDto
import org.sc.common.rest.FileDetailsDto
import org.sc.common.rest.TrailRawDto
import org.sc.configuration.AppProperties
import org.sc.configuration.AppProperties.VERSION
import org.sc.configuration.auth.AuthFacade
import org.sc.data.mapper.TrailCoordinatesMapper
import org.sc.data.model.Coordinates
import org.sc.data.model.Trail
import org.sc.data.model.TrailCoordinates
import org.sc.data.validator.FileNameValidator
import org.sc.processor.GpxFileHandlerHelper
import org.sc.processor.TrailsStatsCalculator
import org.sc.service.AltitudeServiceAdapter
import org.sc.util.FileManagementUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.function.Consumer

@Component
class TrailFileManager @Autowired constructor(
    private val gpxFileHandlerHelper: GpxFileHandlerHelper,
    private val trailsStatsCalculator: TrailsStatsCalculator,
    private val altitudeService: AltitudeServiceAdapter,
    private val trailCoordinatesMapper: TrailCoordinatesMapper,
    private val fileManagementUtil: FileManagementUtil,
    private val fileNameValidator: FileNameValidator,
    private val authFacade: AuthFacade,
    private val appProps: AppProperties
) {

    companion object {
        private const val TRAIL_MID = "file"
        const val GPX_TRAIL_MID = "$TRAIL_MID/gpx"
        const val KML_TRAIL_MID = "$TRAIL_MID/kml"
        const val PDF_TRAIL_MID = "$TRAIL_MID/pdf"

        const val IMPORT_FILE_EXTENSION = "gpx"
    }

    private val pathToStoredFiles = File(fileManagementUtil.getTrailGpxStoragePath()).toPath()
    private val uploadDir = File(appProps.tempStorage)
    private val emptyDefaultString = ""

    fun saveRawGpx(fileName: String, tempFile: Path): Path {
        val pathToSavedFile = makePathToSavedFile(fileName)
        val saveFile = saveFile(tempFile, fileName)

        if (hasFileBeenSaved(saveFile)) {
            return File(pathToSavedFile).toPath()
        }
        throw IllegalStateException()
    }

    fun getTrailRawModel(uniqueFileName: String, originalFilename: String, tempFile: Path): TrailRawDto {
        val gpx = gpxFileHandlerHelper.readFromFile(tempFile)
        val track = gpx.tracks.first()
        val segment = track.segments.first()

        val altitudeResultOrderedList = altitudeService.getAltituteByLongLat(segment.points.map { coord -> Pair(coord.latitude.toDegrees(), coord.longitude.toDegrees()) })

        val coordinatesWithAltitude = mutableListOf<Coordinates>()

        segment.points.forEachIndexed { index, coord ->
            coordinatesWithAltitude.add(
                CoordinatesDto(coord.longitude.toDegrees(), coord.latitude.toDegrees(),
                altitudeResultOrderedList[index]
            ))
        }

        val trailCoordinates = coordinatesWithAltitude.map {
            TrailCoordinates(
                it.longitude, it.latitude, it.altitude,
                trailsStatsCalculator.calculateLengthFromTo(coordinatesWithAltitude, it)
            )
        }

        val authHelper = authFacade.authHelper
        return TrailRawDto(
            "",
            track.name.orElse(emptyDefaultString),
            track.description.orElse(emptyDefaultString),
            trailCoordinatesMapper.map(trailCoordinates.first()),
            trailCoordinatesMapper.map(trailCoordinates.last()),
            trailCoordinates.map { trailCoordinatesMapper.map(it) },
            FileDetailsDto(Date(), authHelper.username, authHelper.instance,
                    authHelper.realm, uniqueFileName, originalFilename)
        )
    }

    fun writeTrailToOfficialGpx(trail: Trail) {
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

    fun getGPXFilesTempPathList(uploadedFiles: List<MultipartFile>): Map<String, Optional<Path>> {
        // We shall not accept files missing the original file names as we may have issues
        val findUploadedFilesWithMissingNames = findUploadedFilesWithMissingNames(uploadedFiles)
        if (findUploadedFilesWithMissingNames.isNotEmpty()) return emptyMap()


        val skippedWrongNameFormats = uploadedFiles
                .filter { fileNameValidator.validate(it.originalFilename).isEmpty() }

        val result: MutableMap<String, Optional<Path>> = HashMap()

        skippedWrongNameFormats.forEach(Consumer { gpxFile: MultipartFile ->
            try {
                val tempFile = Files
                        .createTempFile(uploadDir.toPath(), "", "")
                gpxFile
                        .inputStream
                        .use { input ->
                            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING)
                            // TODO: no two files with same name shall exists!
                            result.put(gpxFile.originalFilename!!, Optional.of(tempFile))
                        }
            } catch (e: IOException) {
                result[gpxFile.originalFilename!!] = Optional.empty()
            }
        })
        return result
    }

    public fun deleteRawTrail(filename: String) {
       Files.delete(File(makePathToSavedFile(filename)).toPath())
    }

    private fun findUploadedFilesWithMissingNames(uploadedFiles: List<MultipartFile>)
            : List<MultipartFile> = uploadedFiles.filter { it.originalFilename == null }

    fun makeUniqueFileName(originalFilename: String) =
        originalFilename.split(".")[0].replace("[^a-zA-Z0-9._]+".toRegex(), "_") +
                "_" + Date().time.toString() + "." + IMPORT_FILE_EXTENSION

    private fun hasFileBeenSaved(saveFile: Long) = saveFile != 0L

    private fun saveFile(tempFile: Path, fileName: String) =
        Files.copy(tempFile, FileOutputStream(makePathToSavedFile(fileName)))

    private fun makePathToSavedFile(fileName: String) =
        fileManagementUtil.getRawTrailStoragePath() + fileName

}