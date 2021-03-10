package org.sc.util

import org.sc.configuration.AppProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileManagementUtil @Autowired constructor(val appProperties: AppProperties) {
    companion object {
        const val MEDIA_FOLDER_NAME = "media"
    }

    fun getMediaStoragePath() = appProperties.trailStorage + File.separator + MEDIA_FOLDER_NAME + File.separator
}