package org.sc

import io.jenetics.jpx.GPX
import java.io.IOException
import java.nio.file.Path

class GpxHelper {
    @Throws(IOException::class)
    fun readFromFile(path: Path?): GPX {
        return GPX.read(path)
    }
}