package org.sc.processor

import io.jenetics.jpx.GPX
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Path

@Component
class GpxFileHandlerHelper {

    @Throws(IOException::class)
    fun readFromFile(path: Path?): GPX {
        return GPX.read(path)
    }

    @Throws(IOException::class)
    fun writeToFile(gpx: GPX, path: Path) {
        return GPX.write(gpx, path)
    }

    fun canRead(path: Path?): Boolean {
        return try {
            val readFromFile = readFromFile(path)
            readFromFile.version.isNotBlank()
        } catch (e : Exception) {
            false
        }
    }
}