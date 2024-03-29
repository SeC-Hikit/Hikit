package org.sc.processor

import com.opencsv.CSVWriter
import io.mockk.InternalPlatformDsl.toStr
import org.sc.data.model.TrailPreview
import org.sc.util.FileManagementUtil
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt


@Component
class TrailExporter constructor(private val fileManagementUtil: FileManagementUtil) {
    fun exportToCsv(list: List<TrailPreview>): ByteArray {

        val filename = "export" + "_" + Date().time.toString() + ".csv"
        val pathToFile = fileManagementUtil.getTrailCsvStoragePath() +
                File.separator + filename
        val fw = FileWriter(pathToFile)
        val writer = CSVWriter(fw)

        writer.writeNext(
            arrayOf(
                "CODICE",
                "CLASSIFICAZIONE",
                "LOCALITA PARTENZA",
                "LOCALITA ARRIVO",
                "LOCALITA",
                "DATI CICLISTICI PRESENTI?",
                "DISTANZA IN METRI",
                "DISL POSITIVO",
                "DISL NEGATIVO",
                "ULTIMA MODIFICA DA",
                "CARICATO DA",
                "CARICATO IL"
            )
        )

        list.forEach {
            writer.writeNext(
                arrayOf(
                    it.code,
                    it.classification.name,
                    it.startPos.name,
                    it.finalPos.name,
                    it.locations.joinToString("-") { it.name },
                    getStringForBoolean(it.isBikeData),
                    it.statsTrailMetadata.length.roundToInt().toStr(),
                    it.statsTrailMetadata.totalRise.toStr(),
                    it.statsTrailMetadata.totalFall.toStr(),
                    it.fileDetails.lastModifiedBy,
                    it.fileDetails.uploadedBy,
                    it.fileDetails.uploadedOn.toLocaleString(),
                )
            )
        }

        writer.flushQuietly()

        val path = Paths.get(pathToFile)
        return Files.readAllBytes(path);
    }

    private fun getStringForBoolean(booleanValue: Boolean) = if (booleanValue) "SI" else "NO"

}