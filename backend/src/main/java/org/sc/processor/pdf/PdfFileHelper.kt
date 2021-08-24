package org.sc.processor.pdf

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.sc.common.rest.*
import org.sc.configuration.AppProperties
import org.sc.data.model.*
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
class PdfFileHelper {

    companion object {
        const val MEDIA_PATH = "media/pdf"
        const val CAI_LOGO = "cai_logo_slim.png"
        const val SEC_LOGO = "sec_logo.png"

        const val DATE_FORMAT = "dd-MM-yyyy"

        const val DOC_NAME_TITLE = "Relazione del sentiero n."
        const val DATA_TITLE = "Dati di percorrenza"
        const val DESCRIPTION_TITLE = "Descrizione"
        const val WAY_TITLE = "Percorso"
        const val PROBLEMS_ON_TRAIL = "Problemi riscontrati sul percorso"
        const val CYCLO_ESCURSIONISM_TITLE = "Ciclo Escursionismo"
        const val FEASABILITY_DATA_TITLE = "Dati di percorrenza"

        const val ETA_PART = ", tempo di percorrenza: "

        private const val SMALL_MARGIN = 5f
        private const val COMMON_MARGIN = SMALL_MARGIN * 2
        private const val TRIPLE_MARGIN = COMMON_MARGIN * 3
        private const val DOUBLE_MARGIN = COMMON_MARGIN * 2
        private const val SEC_LOGO_WIDTH = 60f
        private const val CAI_LOGO_SQUARE_SIZE = 70f
        private const val FULL_WIDTH = 100f

        private const val COLUMN_NUMBER = 3

        private val TABLE_COLUMN_WIDTHS = floatArrayOf(1f, 3f, 2f)
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

    fun exportPdf(trail: TrailDto,
                  places: List<PlaceDto>,
                  lastMaintenance: MaintenanceDto,
                  reportedStillOpenIssue: List<AccessibilityNotificationDto>,
                  filePath: Path) {
        val document = Document()
        val elements = mutableListOf<Element>()

        PdfWriter.getInstance(document, FileOutputStream(filePath.toFile()))
        addMetadata(trail, document)

        document.open()

        //Adding date
        val date = Paragraph(getGeneratedOnString(), summaryFont)
        date.alignment = Element.ALIGN_RIGHT
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

        val summary = makeAndGetSummary(trail, lastMaintenance)
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
            val issuesTitle = Paragraph(PROBLEMS_ON_TRAIL, hugeBold)
            issuesTitle.spacingAfter = COMMON_MARGIN
            elements.add(issuesTitle)

            reportedStillOpenIssue.forEach {
                val issuesList = List(false)
                issuesList.add(it.description)
            }
        }

        if (trail.cycloDetails.cycloClassification != CycloClassification.UNCLASSIFIED) {

            val cycloDetails = trail.cycloDetails

            val cicloRouteTitle = Paragraph(CYCLO_ESCURSIONISM_TITLE, hugeBold)
            cicloRouteTitle.spacingBefore = TRIPLE_MARGIN
            cicloRouteTitle.spacingAfter = COMMON_MARGIN

            elements.add(cicloRouteTitle)

            val cicloSummaryTitle = Paragraph(FEASABILITY_DATA_TITLE, bigBold)
            cicloSummaryTitle.spacingAfter = COMMON_MARGIN
            val cicloSummary = Paragraph(
                    cycloDetails.cycloClassification.classification + ETA_PART + cycloDetails.officialEta + "m.\n" +
                            makeFeasibleWayForwardCyclo(trail, trail.startLocation, trail.endLocation) + "\n" +
                            makeFeasibleWayBackCyclo(trail, trail.endLocation, trail.startLocation) + "\n", summaryFont)

            cicloSummary.spacingAfter = COMMON_MARGIN

            val cicloDescTitle = Paragraph(DESCRIPTION_TITLE, bigBold)
            cicloDescTitle.spacingAfter = COMMON_MARGIN
            elements.add(cicloDescTitle)

            val cicloDes = Paragraph(cycloDetails.description, paragraphFont)
            cicloDes.spacingAfter = COMMON_MARGIN
            elements.add(cicloDes)
        }

        val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/$SEC_LOGO").toURI())
        val imgSec = Image.getInstance(path.toAbsolutePath().toString())
        imgSec.scaleAbsolute(SEC_LOGO_WIDTH, COMMON_MARGIN)
        imgSec.spacingAfter = COMMON_MARGIN

        val pathToCaiLogoSlim: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/$CAI_LOGO").toURI())
        val imgCAI = Image.getInstance(pathToCaiLogoSlim.toAbsolutePath().toString())
        imgCAI.scaleAbsolute(CAI_LOGO_SQUARE_SIZE, CAI_LOGO_SQUARE_SIZE)

        val topTable = PdfPTable(COLUMN_NUMBER)
        topTable.widthPercentage = FULL_WIDTH
        topTable.setWidths(TABLE_COLUMN_WIDTHS)

        val imageCell = PdfPCell()
        imageCell.addElement(imgSec)
        imageCell.addElement(imgCAI)
        imageCell.border = Rectangle.NO_BORDER
        imageCell.horizontalAlignment = Element.ALIGN_LEFT

        topTable.addCell(imageCell)

        val titleCell = makeTitleCell(section, title)
        topTable.addCell(titleCell)

        val dateCell = makeDateCell(date)
        topTable.addCell(dateCell)

        elements.add(topTable)

        elements.forEach {
            document.add(it)
        }

        document.close()
    }

    private fun makeTitleCell(section: Paragraph, title: Paragraph): PdfPCell {
        val titleCell = PdfPCell()
        titleCell.addElement(section)
        titleCell.addElement(title)
        titleCell.border = Rectangle.NO_BORDER
        titleCell.verticalAlignment = Element.ALIGN_BOTTOM
        titleCell.horizontalAlignment = Element.ALIGN_LEFT
        return titleCell
    }

    private fun makeDateCell(date: Paragraph): PdfPCell {
        val dateCell = PdfPCell()
        dateCell.addElement(date)
        dateCell.border = Rectangle.NO_BORDER
        dateCell.horizontalAlignment = Element.ALIGN_RIGHT
        return dateCell
    }

    private fun makeFeasibleWayForwardCyclo(trail: TrailDto,
                                            fromPlaceRef: PlaceRefDto,
                                            toPlaceRef: PlaceRefDto): String {
        return if (trail.cycloDetails.wayForward.isFeasible) ("Tratto '" +
                fromPlaceRef.name + "' - " + toPlaceRef.name + " percorribile.") else ""
    }

    private fun makeFeasibleWayBackCyclo(trail: TrailDto,
                                         fromPlaceRef: PlaceRefDto,
                                         toPlaceRef: PlaceRefDto): String {
        return if (trail.cycloDetails.wayBack.isFeasible) ("Tratto di ritorno '" +
                fromPlaceRef.name + "' - " + toPlaceRef.name + " percorribile.") else ""
    }

    private fun makeAndGetData(): Paragraph =
            Paragraph(DATA_TITLE, bigBold)

    private fun makeAndGetSummary(trail: TrailDto, maintenanceDto: MaintenanceDto): Paragraph {
        val stats = trail.statsTrailMetadata
        return Paragraph(trail.classification.name +
                " - tempo " + getEtaString(trail) + " di percorrenza: " + stats.eta + ",\n" +
                "Dislivello Positivo: " + stats.totalRise + "m,\n" +
                "Dislivello Negativo: " + stats.totalFall + "m,\n" +
                "Distanza Totale: " + stats.length + "m,\n" +
                "Ultima manutenzione: " + maintenanceDto.date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), summaryFont)
    }

    private fun getEtaString(trail: TrailDto): String =
            if (trail.officialEta == -1) "stimato" else "ufficiale"

    private fun getGeneratedOnString() = "Generata il " +
            LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT))

    private fun getTitle(code: String) = """$DOC_NAME_TITLE${code}"""

    private fun addMetadata(trail: TrailDto,
                            document: Document) {
        val authorAndCreator = """${AppProperties.APP_NAME}-${AppProperties.VERSION}"""
        document.addTitle(getTitle(trail.code))
        document.addAuthor(authorAndCreator)
        document.addCreator(authorAndCreator)
    }
}