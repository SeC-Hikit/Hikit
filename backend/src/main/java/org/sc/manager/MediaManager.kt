package org.sc.manager

import org.apache.commons.lang3.StringUtils
import org.apache.tika.io.FilenameUtils
import org.apache.tomcat.jni.File
import org.sc.common.rest.MediaDto
import org.sc.configuration.AppProperties
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
    fun save(fileName: String, tempFile: Path): List<MediaDto> {

        val fileMimeType = mediaProbeUtil.getFileMimeType(tempFile.toFile())
        val fileExtension = mediaProbeUtil.getFileExtensionFromName(fileName)

        val save = mediaDAO.save(
            Media(
                null,
                Date(),
                fileName,
                fileName,
                appProperties.trailStorage + separator + Date().time + fileExtension,
                fileMimeType,
                Files.size(tempFile)
            )
        )
        if(Files.copy(tempFile, FileOutputStream(appProperties.trailStorage)) != 0L) {
            return mediaDAO.getById(save.first()._id).map { mediaMapper.mediaToDto(it) }
        }

        return emptyList()
    }





}
