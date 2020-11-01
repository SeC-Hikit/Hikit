package org.sc

import io.jenetics.jpx.GPX
import java.io.IOException
import java.nio.file.Path

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