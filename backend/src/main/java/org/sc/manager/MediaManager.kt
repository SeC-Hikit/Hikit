package org.sc.manager

import com.mongodb.client.FindIterable
import org.bson.Document
import org.sc.common.rest.MediaDto
import org.sc.configuration.auth.AuthFacade
import org.sc.controller.MediaController
import org.sc.data.mapper.MediaMapper
import org.sc.data.model.FileDetails
import org.sc.data.model.Media
import org.sc.data.repository.MediaDAO
import org.sc.data.repository.PoiDAO
import org.sc.data.repository.TrailDAO
import org.sc.util.FileManagementUtil
import org.sc.util.FileProbeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.logging.Logger

@Component
class MediaManager @Autowired constructor(
        private val realmHelper: AuthFacade,
        private val trailDAO: TrailDAO,
        private val poiDAO: PoiDAO,
        private val mediaDAO: MediaDAO,
        private val mediaMapper: MediaMapper,
        private val mediaProbeUtil: FileProbeUtil,
        private val fileManagementUtil: FileManagementUtil
) {
    companion object {
        const val MEDIA_MID = "file"
    }

    private val logger: Logger = Logger.getLogger(MediaManager::class.java.name)

    fun save(originalFileName: String, tempFile: Path): List<MediaDto> {

        val fileMimeType = mediaProbeUtil.getFileMimeType(tempFile.toFile(), originalFileName)
        val fileExtension = mediaProbeUtil.getFileExtensionFromMimeType(fileMimeType)

        val fileName = makeFileName(fileExtension)
        val pathToSavedFile = makePathToSavedFile(fileName)
        val saveFile = saveFile(tempFile, fileName)

        if (hasFileBeenSaved(saveFile)) {
            val authHelper = realmHelper.authHelper
            val save = mediaDAO.save(
                    Media(
                            null,
                            Date(),
                            originalFileName,
                            fileName,
                            pathToSavedFile,
                            fileMimeType,
                            Files.size(tempFile),
                            FileDetails(Date(),
                                    authHelper.username,
                                    authHelper.instance,
                                    authHelper.realm,
                                    fileName,
                                    originalFileName
                            ),
                            false
                    )
            )
            logger.info("save Media originalFileName: $originalFileName to $pathToSavedFile in instance: ${authHelper.instance}, realm: ${authHelper.realm}")
            return mediaDAO.getById(save.first().id).map { mediaMapper.mediaToDto(it) }
        }
        return emptyList()
    }

    fun getExtensionFromName(name: String): String {
        val parts = name.split(".")
        if (parts.isNotEmpty()) {
            return "." + parts[1]
        }
        return ""
    }

    fun getById(id: String) = mediaDAO.getById(id).map { mediaMapper.mediaToDto(it) }

    fun doesMediaExist(id: String) = getById(id).isNotEmpty()

    fun deleteById(id: String): List<MediaDto> {
        poiDAO.unlinkMediaByAllPoi(id)
        trailDAO.unlinkMediaByAllTrails(id)
        return mediaDAO.deleteById(id).map { mediaMapper.mediaToDto(it) }
    }

    fun count(): Long = mediaDAO.count()

    private fun hasFileBeenSaved(saveFile: Long) = saveFile != 0L

    private fun saveFile(tempFile: Path, fileName: String) =
            Files.copy(tempFile, FileOutputStream(getPathToFileOut(fileName)))

    private fun getPathToFileOut(fileName: String) =
            fileManagementUtil.getMediaStoragePath() + fileName

    private fun makePathToSavedFile(fileName: String) =
            MediaController.PREFIX + "/" + MEDIA_MID + "/" + fileName

    private fun makeFileName(fileExtension: String) =
            Date().time.toString() + "." + fileExtension

    fun getMediaNotGenerated(): FindIterable<Document> = mediaDAO.mediaNotGenerated

    fun updateCompressed(media: Media) {
        val updateCompressed = mediaDAO.updateCompressed(media)

        if (updateCompressed.modifiedCount == 0L) {
            logger.info("No image found")
        }

    }

}


