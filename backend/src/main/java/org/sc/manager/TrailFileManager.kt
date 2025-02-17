package org.sc.manager

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode
import de.micromata.opengis.kml.v_2_2_0.Kml
import de.micromata.opengis.kml.v_2_2_0.LineString
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Metadata
import org.sc.adapter.AltitudeServiceWrapper
import org.sc.common.rest.*
import org.sc.configuration.AppProperties
import org.sc.configuration.AppProperties.DISPLAYED_VERSION
import org.sc.data.mapper.TrailCoordinatesMapper
import org.sc.data.model.Coordinates
import org.sc.data.model.TrailCoordinates
import org.sc.data.validator.FileNameValidator
import org.sc.processor.GpxFileHandlerHelper
import org.sc.processor.TrailsStatsCalculator
import org.sc.processor.pdf.PdfFileHelper
import org.sc.configuration.auth.AuthData
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
import java.time.Instant.now
import java.util.*
import java.util.function.Consumer
import java.util.logging.Logger

@Component
class TrailFileManager @Autowired constructor(
    private val gpxFileHandlerHelper: GpxFileHandlerHelper,
    private val pdfFileHandlerHelper: PdfFileHelper,
    private val trailsStatsCalculator: TrailsStatsCalculator,
    private val altitudeService: AltitudeServiceWrapper,
    private val trailCoordinatesMapper: TrailCoordinatesMapper,
    private val fileManagementUtil: FileManagementUtil,
    private val fileNameValidator: FileNameValidator,
    appProps: AppProperties
) {

    companion object {
        private const val TRAIL_MID = "file"
        const val GPX_TRAIL_MID = "$TRAIL_MID/gpx"
        const val KML_TRAIL_MID = "$TRAIL_MID/kml"
        const val PDF_TRAIL_MID = "$TRAIL_MID/pdf"

        const val IMPORT_FILE_EXTENSION = "gpx"
    }

    private val logger = Logger.getLogger(TrailFileManager::class.java.name)

    private val customItineraryStoredFiles = File(fileManagementUtil.getCustomItineraryPath()).toPath()
    private val pathToGpxStoredFiles = File(fileManagementUtil.getTrailGpxStoragePath()).toPath()
    private val pathToKmlStoredFiles = File(fileManagementUtil.getTrailKmlStoragePath()).toPath()
    private val pathToPdfStoredFiles = File(fileManagementUtil.getTrailPdfStoragePath()).toPath()

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

    fun getTrailRawModel(
        uniqueFileName: String,
        originalFilename: String,
        tempFile: Path,
        authData: AuthData
    ): TrailRawDto {
        val gpx = gpxFileHandlerHelper.readFromFile(tempFile)
        val track = gpx.tracks.first()
        val segment = track.segments.first()

        val altitudeResultOrderedList = altitudeService.getElevationsByLongLat(segment.points.map { coord ->
            Pair(
                coord.latitude.toDegrees(),
                coord.longitude.toDegrees()
            )
        })

        val coordinatesWithAltitude = mutableListOf<Coordinates>()

        segment.points.forEachIndexed { index, coord ->
            coordinatesWithAltitude.add(
                CoordinatesDto(
                    coord.longitude.toDegrees(), coord.latitude.toDegrees(),
                    altitudeResultOrderedList[index]
                )
            )
        }

        val trailCoordinates = coordinatesWithAltitude.map {
            TrailCoordinates(
                it.longitude, it.latitude, it.altitude,
                trailsStatsCalculator.calculateLengthFromTo(coordinatesWithAltitude, it)
            )
        }

        return TrailRawDto(
            "",
            track.name.orElse(emptyDefaultString),
            track.description.orElse(emptyDefaultString),
            trailCoordinatesMapper.map(trailCoordinates.first()),
            trailCoordinatesMapper.map(trailCoordinates.last()),
            trailCoordinates.map { trailCoordinatesMapper.map(it) },
            FileDetailsDto(
                Date(), authData.username, authData.instance,
                authData.realm, uniqueFileName, originalFilename, authData.username
            )
        )
    }

    fun writeTrailToOfficialGpx(trail: TrailDto, fileName: String): String {
        logger.info("Writing GPX trail for trail with id '${trail.id}'")
        val creator = "S&C_$DISPLAYED_VERSION"
        val gpx = buildTrailGpx(creator, trail)
        val generatedFilename = "$fileName.gpx"
        gpxFileHandlerHelper.writeToFile(gpx, pathToGpxStoredFiles.resolve(generatedFilename))
        return generatedFilename
    }

    fun buildCustomGpx(coordinates: List<Coordinates>): ByteArray {
        val gpx = buildSegments("Custom user", coordinates)
            .metadata(
                Metadata.builder()
                    .author("S&C - public user")
                    .name("Custom Path")
                    .time(now()).build()
            ).build()
        val date = now()
        val generatedFilename = "custom-itineary-${date.toEpochMilli()}.gpx"
        val resolvedSavePath = customItineraryStoredFiles
            .resolve(generatedFilename)
        gpxFileHandlerHelper.writeToFile(gpx, resolvedSavePath)
        return Files.readAllBytes(resolvedSavePath)
    }

    private fun buildTrailGpx(creator: String, trail: TrailDto): GPX {
        return buildSegments(creator, trail.coordinates).metadata(
            Metadata.builder()
                .author("S&C - $creator")
                .name(trail.code)
                .time(trail.lastUpdate.toInstant()).build()
        ).build()
    }

    private fun buildSegments(creator: String, coordinates: List<Coordinates>): GPX.Builder =
        GPX.builder(creator)
            .addTrack { track ->
                track.addSegment { segment ->
                    coordinates.forEach {
                        segment.addPoint { p ->
                            p.lat(it.latitude).lon(it.longitude).ele(it.altitude)
                        }
                    }
                }
            }

    fun writeTrailToKml(trail: TrailDto, fileName: String): String {
        logger.info("Writing KML for trail with id '${trail.id}'")
        val kml = Kml()
        val lineString: LineString = LineString().withAltitudeMode(AltitudeMode.ABSOLUTE)
        trail.coordinates.forEach { lineString.addToCoordinates(it.longitude, it.latitude, it.altitude) }
        kml.createAndSetDocument().createAndAddPlacemark().withGeometry(lineString)
        val generatedFilename = "$fileName.kml"
        kml.marshal(pathToKmlStoredFiles.resolve(generatedFilename).toFile())
        return generatedFilename
    }

    fun writeTrailToPdf(
        trail: TrailDto, places: List<PlaceDto>, lastMaintenance: List<MaintenanceDto>,
        reportedOpenIssues: List<AccessibilityNotificationDto>,
        fileName: String
    ): String {
        val generatedFilename = "$fileName.pdf"
        val pathname = pathToPdfStoredFiles.resolve(generatedFilename)
        pdfFileHandlerHelper.exportPdf(trail, places, lastMaintenance, reportedOpenIssues, pathname)
        logger.info("Exported pdf for trail with 'id' ${trail.id} in path: $pathname")
        return generatedFilename
    }

    fun getGPXFilesTempPathList(uploadedFiles: List<MultipartFile>): Map<String, Optional<Path>> {
        // We shall not accept files missing the original file names
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
                logger.severe("Exception caught in getGPXFilesTempPathList for uploadedFiles $uploadedFiles! $e")
                result[gpxFile.originalFilename!!] = Optional.empty()
            }
        })
        return result
    }

    fun deleteRawTrail(filename: String) {
        Files.delete(File(makePathToSavedFile(filename)).toPath())
    }

    fun getFilename(trail: TrailDto) = trail.code.replace("/", "_") + "_" + trail.id

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