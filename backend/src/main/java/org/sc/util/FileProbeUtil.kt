package org.sc.util

import org.apache.commons.lang3.StringUtils
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileProbeUtil {

    private val tika = Tika()

    fun isFileNameInCorrectFormat(fileName : String) : Boolean = StringUtils.countMatches(fileName, ".") > 0
    fun getFileMimeType(file : File, name : String) : String = tika.detect(file.inputStream(), name)
    fun getFileExtensionFromMimeType(mime : String) : String = mime.split("/").last()
}