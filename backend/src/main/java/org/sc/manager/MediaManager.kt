package org.sc.manager

import org.sc.common.rest.MediaDto
import org.sc.controller.MediaController
import org.sc.data.entity.Media
import org.sc.data.mapper.MediaMapper
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

@Component
class MediaManager @Autowired constructor(
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

    fun save(originalFileName: String, tempFile: Path): List<MediaDto> {

        val fileMimeType = mediaProbeUtil.getFileMimeType(tempFile.toFile())
        val fileExtension = mediaProbeUtil.getFileExtensionFromMimeType(fileMimeType)

        val fileName = makeFileName(fileExtension)
        val pathToSavedFile = makePathToSavedFile(fileName)
        val saveFile = saveFile(tempFile, fileName)

        if (hasFileBeenSaved(saveFile)) {
            val save = mediaDAO.save(
                Media(
                    null,
                    Date(),
                    originalFileName,
                    fileName,
                    pathToSavedFile,
                    fileMimeType,
                    Files.size(tempFile)
                )
            )
            return mediaDAO.getById(save.first()._id).map { mediaMapper.mediaToDto(it) }
        }
        return emptyList()
    }

    fun getById(id: String) = mediaDAO.getById(id).map { mediaMapper.mediaToDto(it) }

    fun deleteById(id: String): List<MediaDto> {
        poiDAO.unlinkMediaByAllPoi(id)
        trailDAO.unlinkMediaByAllTrails(id)
        return mediaDAO.deleteById(id).map { mediaMapper.mediaToDto(it) }
    }

    private fun hasFileBeenSaved(saveFile: Long) = saveFile != 0L

    private fun saveFile(tempFile: Path, fileName: String) =
        Files.copy(tempFile, FileOutputStream(getPathToFileOut(fileName)))

    private fun getPathToFileOut(fileName: String) =
        fileManagementUtil.getMediaStoragePath() + fileName

    private fun makePathToSavedFile(fileName: String) =
        MediaController.PREFIX + "/" + MEDIA_MID + "/" + fileName

    private fun makeFileName(fileExtension: String) =
        Date().time.toString() + "." + fileExtension
}

