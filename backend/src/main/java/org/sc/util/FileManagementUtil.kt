package org.sc.util

import org.sc.configuration.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileManagementUtil @Autowired constructor(val appProperties: AppProperties) {

    companion object {
        const val MEDIA_FOLDER_NAME = "media"
        const val CUSTOM_ITINERARY_FOLDER_NAME = "custom-itinerary"
        const val TRAIL_FOLDER_NAME = "trail"
        const val TRAIL_GPX_FOLDER_NAME = "gpx"
        const val TRAIL_KML_FOLDER_NAME = "kml"
        const val TRAIL_PDF_FOLDER_NAME = "pdf"
        const val RAW_TRAIL_FOLDER_NAME = "raw"
        const val CSV_FOLDER_NAME = "csv"
    }

    /*
     * Data structure:
     * /media
     * /custom-itinerary
     * /trail <- contains outputted trails
     * /trail/gpx <- contains .gpx
     * /trail/kml <- contains .kml
     * /trail/pdf <- contains .pdf
     * /trail/csv <- contains .csv
     * /raw <- contains the raw trails uploaded by management
     */
    fun getMediaStoragePath() = appProperties.storage + File.separator + MEDIA_FOLDER_NAME + File.separator
    fun getCustomItineraryPath() = appProperties.storage + File.separator + CUSTOM_ITINERARY_FOLDER_NAME + File.separator
    fun getTrailStoragePath() = appProperties.storage + File.separator + TRAIL_FOLDER_NAME + File.separator
    fun getRawTrailStoragePath() = appProperties.storage + File.separator + RAW_TRAIL_FOLDER_NAME + File.separator
    fun getTrailGpxStoragePath() = getTrailStoragePath() + TRAIL_GPX_FOLDER_NAME + File.separator
    fun getTrailKmlStoragePath() = getTrailStoragePath() + TRAIL_KML_FOLDER_NAME + File.separator
    fun getTrailPdfStoragePath() = getTrailStoragePath() + TRAIL_PDF_FOLDER_NAME + File.separator
    fun getTrailCsvStoragePath() = getTrailStoragePath() + CSV_FOLDER_NAME + File.separator
}