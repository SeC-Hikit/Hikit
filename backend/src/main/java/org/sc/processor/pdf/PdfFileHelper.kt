package org.sc.processor.pdf

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import com.itextpdf.text.pdf.PdfPCell


@Component
class PdfFileHelper {

    companion object{
    const val MEDIA_PATH = "media/pdf"
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

    fun test(filePath : String) {
        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(filePath))

        addMetadata(document)
        document.open()

        //Adding date
        val date = Paragraph("Generata il 13/07/2021", summaryFont)
        date.alignment = com.itextpdf.text.Element.ALIGN_RIGHT;

        // Adding section name (ex chunk)
        val section = Paragraph("CAI Bologna", caiFont)

        // Adding title
        val title = Paragraph("Relazione per sentiero n.101", hugeBold)

        // Adding trail summary
        val summaryTitle = Paragraph("Dati di percorrenza", bigBold)
        summaryTitle.spacingAfter = 10f
        summaryTitle.spacingBefore = 20f
        val summary = Paragraph("EE - tempo stimato di percorrenza: 00:45,\n" +
                "Dislivello Positivo: 350m,\n" +
                "Dislivello Negativo: 250m,\n" +
                "Distanza Totale: 2005m\n" +
                "Ultima manutenzione: 06/07/2021, CAI di Bologna", summaryFont)
        summary.spacingAfter = 10f

        // Adding Description
        val paragraphDesTitle = Paragraph("Descrizione", bigBold)
        paragraphDesTitle.spacingAfter = 10f
        val paragraphDes = Paragraph("Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", paragraphFont)
        paragraphDes.spacingAfter = 10f

        // Adding Route
        val routeTitle = Paragraph("Percorso", bigBold)
        routeTitle.spacingAfter = 10f
        val place1Title = Paragraph("Partenza: Casa di Pippo", smallBold)
        place1Title.spacingAfter = 10f
        val place1 = Paragraph("Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur.", paragraphFont)
        place1.spacingAfter = 10f
        val place2Title = Paragraph("Crocevia di Monte Salomone", smallBold)
        place2Title.spacingAfter = 10f
        val place2 = Paragraph("LLorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur.", paragraphFont)
        place2.spacingAfter = 10f
        val place3Title = Paragraph("Rifugio Brunotti", smallBold)
        place3Title.spacingAfter = 10f
        val place3 = Paragraph("Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur.", paragraphFont)
        place3.spacingAfter = 10f
        val place4Title = Paragraph("Arrivo: Prati di Alessandro", smallBold)
        place4Title.spacingAfter = 10f
        val place4 = Paragraph("Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur.", paragraphFont)
        place4.spacingAfter = 10f

        // Adding issues (if any)  --- list
        val issuesTitle = Paragraph("Problemi riscontrati sul percorso", hugeBold)
        issuesTitle.spacingAfter = 10f

        val issuesList = com.itextpdf.text.List(false)
        issuesList.add("(Minore) Sentiero smottato in vicinanza del Rifugio Brunotti")

        // Adding Ciclo (should be printed only if asked!)
        val cicloRouteTitle = Paragraph("Ciclo Escursionismo", hugeBold)
        cicloRouteTitle.spacingBefore = 30f
        cicloRouteTitle.spacingAfter = 10f
        val cicloSummaryTitle = Paragraph("Dati di percorrenza", bigBold)
        cicloSummaryTitle.spacingAfter = 10f
        val cicloSummary = Paragraph("BC+, tempo stimato di percorrenza: 15m.\n" +
                "Tratto “Casa di Pippo” - “Prati di Alessandro” percorribile con tempo di ‘portage’ ±2minuti\n" +
                "Tratto “Prati di Alessandro” - “Casa di Pippo” non fattibile/consigliato.", summaryFont)
        cicloSummary.spacingAfter = 10f

        //Title here: already done
        val cicloDes = Paragraph("Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", paragraphFont)
        cicloDes.spacingAfter = 10f

        val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/sec_logo.png").toURI())
        val imgSec = Image.getInstance(path.toAbsolutePath().toString())
        imgSec.scaleAbsolute(60F, 10F)
        imgSec.spacingAfter = 5f

        val path2: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/cai_logo_slim.png").toURI())
        val imgCAI = Image.getInstance(path2.toAbsolutePath().toString())
        imgCAI.scaleAbsolute(70F, 70F);

        val table = PdfPTable(3)
        table.widthPercentage = 100F
        table.setWidths(floatArrayOf(1f, 3f,2f))
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
        document.add(summaryTitle)
        document.add(summary)
        document.add(paragraphDesTitle)
        document.add(paragraphDes)
        document.add(routeTitle)
        document.add(place1Title)
        document.add(place1)
        document.add(place2Title)
        document.add(place2)
        document.add(place3Title)
        document.add(place3)
        document.add(place4Title)
        document.add(place4)
        document.add(issuesTitle)
        document.add(issuesList)
        document.add(cicloRouteTitle)
        document.add(cicloSummaryTitle)
        document.add(cicloSummary)
        document.add(paragraphDesTitle)
        document.add(cicloDes)

        document.close()

    }

//    private fun addImage1(document: Document) {
//             val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/sec_logo.png").toURI())
//             val img = Image.getInstance(path.toAbsolutePath().toString())
//             document.add(img)
//        }
//     private fun addImage2(document: Document) {
//             val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/cai_logo_slim.png").toURI())
//             val img = Image.getInstance(path.toAbsolutePath().toString())
//             document.add(img)
//         }


    private fun addMetadata(document: Document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
        //how to add this t the bottom of the page
        document.addCreationDate();

    }
}


