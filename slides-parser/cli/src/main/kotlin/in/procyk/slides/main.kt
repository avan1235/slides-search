package `in`.procyk.slides

import `in`.procyk.slides.model.Presentation
import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>) {
    val inputPath =
        args.getOrNull(0) ?: error("Usage: slidesParser <input.pptx> [presentation.json]")
    val outputPath = args.getOrNull(1) ?: "presentation.json"

    val inputFile = File(inputPath)
    require(inputFile.exists()) { "Input file not found: $inputPath" }

    val presentation = parsePptx(inputFile)
    val json = Json { prettyPrint = true }
    File(outputPath).writeText(json.encodeToString(presentation), Charsets.UTF_8)
    println("Parsed ${presentation.slides.size} slides -> $outputPath")
}

private fun parsePptx(file: File): Presentation =
    file.inputStream().use { parsePptxPresentation(it) }
