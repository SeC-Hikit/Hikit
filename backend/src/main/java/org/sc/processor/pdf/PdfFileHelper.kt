package org.sc.processor.pdf

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.sc.configuration.AppProperties
import org.sc.data.model.*
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Component
class PdfFileHelper {

    companion object {
        const val MEDIA_PATH = "media/pdf"

        const val DATE_FORMAT = "dd-MM-yyyy"

        const val DOC_NAME_TITLE = "Relazione del sentiero n."
        const val DATA_TITLE = "Dati di percorrenza"
        const val DESCRIPTION_TITLE = "Descrizione"
        const val WAY_TITLE = "Percorso"

        const val COMMON_MARGIN = 10f
        const val TRIPLE_MARGIN = COMMON_MARGIN * 3
        const val DOUBLE_MARGIN = COMMON_MARGIN * 2
    }

    private val hugeBold: Font = Font(
            Font.FontFamily.HELVETICA, 15f,
            Font.BOLD, BaseColor.BLACK
    )

    private val bigBold: Font = Font(
            Font.FontFamily.HELVETICA, 13f,
            Font.BOLD, BaseColor.BLACK
    )

    private val smallBold: Font = Font(
            Font.FontFamily.HELVETICA, 12f,
            Font.BOLD, BaseColor.BLACK
    )

    private val paragraphFont: Font = Font(
            Font.FontFamily.HELVETICA, 10f,
            Font.NORMAL, BaseColor.BLACK
    )

    private val summaryFont: Font = Font(
            Font.FontFamily.HELVETICA, 11f,
            Font.NORMAL, BaseColor.BLACK
    )

    private val caiFont: Font = Font(
            Font.FontFamily.HELVETICA, 12f,
            Font.NORMAL, BaseColor.BLACK
    )

    fun exportPdf(trail: Trail,
                  places: List<Place>,
                  lastMaintenance: Maintenance,
                  reportedStillOpenIssue: List<AccessibilityNotification>, filePath: Path) {
        val document = Document()
        val elements = mutableListOf<Element>()

        PdfWriter.getInstance(document, FileOutputStream(filePath.toFile()))
        addMetadata(trail, document)

        document.open()

        //Adding date
        val date = Paragraph(getGeneratedOnString(), summaryFont)
        date.alignment = Element.ALIGN_RIGHT;
        elements.add(date)
        // Adding section name (ex chunk)
        val section = Paragraph(trail.maintainingSection, caiFont)
        elements.add(section)
        // Adding title
        val title = Paragraph(getTitle(trail.code), hugeBold)
        elements.add(title)
        // Adding trail summary
        val summaryTitle = makeAndGetData()
        summaryTitle.spacingAfter = COMMON_MARGIN
        summaryTitle.spacingBefore = DOUBLE_MARGIN
        elements.add(summaryTitle)

        val summary = makeAndGetSummary(trail)
        summary.spacingAfter = COMMON_MARGIN
        elements.add(summary)

        // Adding Description
        val paragraphDesTitle = Paragraph(DESCRIPTION_TITLE, bigBold)
        paragraphDesTitle.spacingAfter = COMMON_MARGIN
        elements.add(paragraphDesTitle)

        val paragraphDes = Paragraph(trail.description, paragraphFont)
        paragraphDes.spacingAfter = COMMON_MARGIN
        elements.add(paragraphDes)
        // Adding Route
        val routeTitle = Paragraph(WAY_TITLE, bigBold)
        routeTitle.spacingAfter = COMMON_MARGIN

        places.forEach {
            val paragraphName = Paragraph(it.name, smallBold)
            paragraphName.spacingAfter = COMMON_MARGIN
            val paragraph = Paragraph(it.description, paragraphFont)
            paragraph.spacingAfter = COMMON_MARGIN
            elements.add(paragraph)
        }

        if (reportedStillOpenIssue.isNotEmpty()) {
            // Adding issues (if any)  --- list
            val issuesTitle = Paragraph("Problemi riscontrati sul percorso", hugeBold)
            issuesTitle.spacingAfter = COMMON_MARGIN
            elements.add(issuesTitle)

            reportedStillOpenIssue.forEach {
                val issuesList = List(false)
                issuesList.add(it.description)
            }
        }

        if (trail.cycloDetails.cycloClassification != CycloClassification.UNCLASSIFIED) {

            val cycloDetails = trail.cycloDetails

            val cicloRouteTitle = Paragraph("Ciclo Escursionismo", hugeBold)
            cicloRouteTitle.spacingBefore = TRIPLE_MARGIN
            cicloRouteTitle.spacingAfter = COMMON_MARGIN

            elements.add(cicloRouteTitle)

            val cicloSummaryTitle = Paragraph("Dati di percorrenza", bigBold)
            cicloSummaryTitle.spacingAfter = COMMON_MARGIN
            val cicloSummary = Paragraph(
                    cycloDetails.cycloClassification.classification + ", tempo di percorrenza: " + cycloDetails.officialEta + "m.\n" +
                            makeFeasibleCyclo(trail, trail.startLocation, trail.endLocation)  + "\n" +
                    makeFeasibleCyclo(trail, trail.endLocation, trail.startLocation) + "\n" , summaryFont)
            cicloSummary.spacingAfter = COMMON_MARGIN

            val cicloDescTitle = Paragraph("Descrizione", bigBold)
            cicloDescTitle.spacingAfter = COMMON_MARGIN
            elements.add(cicloDescTitle)

            val cicloDes = Paragraph("Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", paragraphFont)
            cicloDes.spacingAfter = COMMON_MARGIN
            elements.add(cicloDes)
        }


        val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/sec_logo.png").toURI())
        val imgSec = Image.getInstance(path.toAbsolutePath().toString())
        imgSec.scaleAbsolute(60F, 10F)
        imgSec.spacingAfter = 5f

        val path2: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/cai_logo_slim.png").toURI())
        val imgCAI = Image.getInstance(path2.toAbsolutePath().toString())
        imgCAI.scaleAbsolute(70F, 70F);

        val table = PdfPTable(3)
        table.widthPercentage = 100F
        table.setWidths(floatArrayOf(1f, 3f, 2f))
        val cell = PdfPCell()
        //cell.add(imgSec)
        cell.addElement(imgSec)
        cell.addElement(imgCAI)
        cell.border = Rectangle.NO_BORDER
        cell.horizontalAlignment = Element.ALIGN_LEFT

        table.addCell(cell)

        val cell3 = PdfPCell()
        cell3.addElement(section)
        cell3.addElement(title)
        cell3.border = Rectangle.NO_BORDER
        cell3.verticalAlignment = Element.ALIGN_BOTTOM
        cell3.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(cell3)

        val cell4 = PdfPCell()
        cell4.addElement(date)
        cell4.border = Rectangle.NO_BORDER
        cell4.horizontalAlignment = Element.ALIGN_RIGHT
        table.addCell(cell4)

        document.add(table)


        document.close()
    }

    private fun makeFeasibleCyclo(trail: Trail, fromPlaceRef: PlaceRef, toPlaceRef: PlaceRef): String {
        return if (trail.cycloDetails.wayBack.isFeasible) ("Tratto '" +
                fromPlaceRef.name + "' - " + toPlaceRef.name + " percorribile.") else ""
    }

    private fun makeAndGetData(): Paragraph =
            Paragraph(DATA_TITLE, bigBold)

    private fun makeAndGetSummary(trail: Trail): Paragraph {
        val stats = trail.statsTrailMetadata
        return Paragraph(trail.classification.name +
                " - tempo " + getEtaString(trail) + " di percorrenza: " + stats.eta + ",\n" +
                "Dislivello Positivo: " + stats.totalRise + "m,\n" +
                "Dislivello Negativo: " + stats.totalFall + "m,\n" +
                "Distanza Totale: " + stats.length + "m,\n" +
                "Ultima manutenzione: 06/07/2021, CAI di Bologna", summaryFont)
    }

    private fun getEtaString(trail: Trail): String =
            if (trail.officialEta == -1) "stimato" else "ufficiale"


    private fun getGeneratedOnString() = "Generata il " +
            LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT))

    private fun getTitle(code: String) = """$DOC_NAME_TITLE${code}"""

    private fun addImage(document: Document) {
        val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/cai_logo_slim.png").toURI())
        val img = Image.getInstance(path.toAbsolutePath().toString())
        document.add(img)
    }

    private fun addMetadata(trail: Trail,
                            document: Document) {
        document.addTitle(getTitle(trail.code));
        document.addAuthor("""${AppProperties.APP_NAME}-${AppProperties.VERSION}""");
        document.addCreator("""${AppProperties.APP_NAME}-${AppProperties.VERSION}""");
    }


}