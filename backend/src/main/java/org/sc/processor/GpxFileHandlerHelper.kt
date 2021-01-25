package org.sc.processor

import io.jenetics.jpx.GPX
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Path
import kotlin.jvm.Throws

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
}