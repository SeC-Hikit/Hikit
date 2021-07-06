package org.sc.processor.pdf

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths


@Component
class PdfFileHelper {

    companion object{
        const val MEDIA_PATH = "media/pdf"
    }

    private val smallBold: Font = Font(
        Font.FontFamily.HELVETICA, 12f,
        Font.BOLD, BaseColor.BLACK
    )

    fun test(filePath : String) {
        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(filePath))

        addMetadata(document)

        document.open()

        // Adding section name
        val chunk = Paragraph("CAI Bologna", smallBold)
        chunk.paddingTop = 10f
        addImage(document)
        document.add(chunk)
        document.close()
    }

    private fun addImage(document: Document) {
        val path: Path = Paths.get(ClassLoader.getSystemResource("$MEDIA_PATH/cai_logo_slim.png").toURI())
        val img = Image.getInstance(path.toAbsolutePath().toString())
        document.add(img)
    }

    private fun addMetadata(document: Document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }


}