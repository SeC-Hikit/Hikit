package org.sc.util

import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileProbeUtil {

    private val tika = Tika()

    fun getFileMimeType(file : File) : String = tika.detect(file)
}