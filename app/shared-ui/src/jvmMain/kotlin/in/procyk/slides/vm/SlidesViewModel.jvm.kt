package `in`.procyk.slides.vm

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.writeText

internal actual fun savePresentation(json: String) {
    val destination = Path.of(System.getProperty("user.home"), "presentation.json")
    if (destination.exists()) return
    if (!destination.parent.exists()) {
        Files.createDirectories(destination.parent)
    }
    destination.writeText(json)
}