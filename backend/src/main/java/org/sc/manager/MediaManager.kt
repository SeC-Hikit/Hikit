package org.sc.manager

import org.sc.common.rest.MediaDto
import org.sc.configuration.AppProperties
import org.sc.controller.MediaController
import org.sc.data.entity.Media
import org.sc.data.mapper.MediaMapper
import org.sc.data.repository.MediaDAO
import org.sc.util.FileProbeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File.separator
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Component
class MediaManager @Autowired constructor(private val mediaDAO: MediaDAO,
                                          private val mediaMapper: MediaMapper,
                                          private val mediaProbeUtil: FileProbeUtil,
                                          private val appProperties: AppProperties){
    fun save(originalFileName: String, tempFile: Path): List<MediaDto> {

        val fileMimeType = mediaProbeUtil.getFileMimeType(tempFile.toFile())
        val fileExtension = mediaProbeUtil.getFileExtensionFromMimeType(fileMimeType)

        val fileName = makeFileName(fileExtension)
        val pathToSavedFile = makePathToSavedFile(fileName)
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
        if(Files.copy(tempFile, FileOutputStream(getPathToFileOut(fileName))) != 0L) {
            return mediaDAO.getById(save.first()._id).map { mediaMapper.mediaToDto(it) }
        }
        return emptyList()
    }

    private fun getPathToFileOut(fileName: String) =
        appProperties.trailStorage + separator + fileName

    private fun makePathToSavedFile(fileName: String) =
        MediaController.PREFIX + separator + fileName

    private fun makeFileName(fileExtension: String) =
        Date().time.toString() + "." + fileExtension


}
