package `in`.procyk.slides

import `in`.procyk.slides.model.Presentation
import `in`.procyk.slides.model.Slide
import org.apache.poi.openxml4j.util.ZipSecureFile
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFTextShape
import java.io.InputStream

fun parsePptx(fis: InputStream): Presentation {
    ZipSecureFile.setMaxFileCount(100_000)
    val slides = XMLSlideShow(fis).use { slideShow ->
        slideShow.slides.map { xslfSlide ->
            val titleShape = xslfSlide.shapes
                .filterIsInstance<XSLFTextShape>()
                .firstOrNull { it.shapeName?.contains("title", ignoreCase = true) == true }

            val title = titleShape
                ?.textParagraphs
                ?.joinToString("\n") { para -> para.textRuns.joinToString("") { it.rawText ?: "" } }
                ?.trim()
                ?.takeIf { it.isNotBlank() }

            val rawContent = xslfSlide.shapes
                .filterIsInstance<XSLFTextShape>()
                .filter { it != titleShape }
                .joinToString("\n") { shape ->
                    shape.textParagraphs.joinToString("\n") { para ->
                        para.textRuns.joinToString("") { run -> run.rawText ?: "" }
                    }
                }
                .trim()

            val content = cleanContent(rawContent)

            val rawNotes = xslfSlide.notes?.shapes
                ?.filterIsInstance<XSLFTextShape>()
                ?.drop(1) // skip slide-number/header shape
                ?.flatMap { it.textParagraphs }
                ?.joinToString("\n") { para ->
                    para.textRuns.joinToString("") { run -> run.rawText ?: "" }
                }
                ?.trim()
                ?.takeIf { it.isNotBlank() }

            Slide(content = content, title = title, notes = rawNotes?.let(::cleanContent))
        }
    }
    return Presentation(slides = slides)
}

/**
 * Collapses 3+ consecutive newlines to exactly 2 (one blank line between sections).
 * A single blank line (\n\n) is preserved as-is.
 */
private fun cleanContent(text: String): String = text.replace(Regex("\n{3,}"), "\n\n")
