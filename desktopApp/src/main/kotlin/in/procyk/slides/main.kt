package `in`.procyk.slides

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import `in`.procyk.slides.model.Presentation
import `in`.procyk.slides.search.LuceneSearchEngine
import `in`.procyk.slides.ui.ControlScreen
import `in`.procyk.slides.ui.SlidesScreen
import `in`.procyk.slides.vm.SlidesViewModel
import kotlinx.serialization.json.Json
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.io.File

fun main(args: Array<String>) {
    val presentation = loadPresentation(args)

    val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices

    val control = screens.firstOrNull() ?: error("no screen to control slides")
    val slides = screens.drop(1).firstOrNull {
        it.isFullScreenSupported && it.defaultConfiguration.bounds.run { width > height }
    } ?: error("no screen to display slides on")

    val controlPosition = control.windowPosition
    val slidesPosition = slides.windowPosition

    application {
        val vm = remember {
            SlidesViewModel(
                searchEngine = LuceneSearchEngine(),
                presentation = presentation,
            )
        }
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = controlPosition,
                width = Dp.Unspecified,
                height = Dp.Unspecified,
            ),
            onKeyEvent = vm::onKeyEvent,
        ) {
            ControlScreen(vm)
        }

        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = slidesPosition,
                placement = WindowPlacement.Fullscreen,
            ),
            onKeyEvent = vm::onKeyEvent,
        ) {
            SlidesScreen(vm)
        }
    }
}

private fun loadPresentation(args: Array<String>): Presentation {
    val file = args.getOrNull(0).let(::takeExistingFile)
        ?: takeExistingFile("./presentation.json")
        ?: takeExistingFile("../presentation.json")
    if (file == null) {
        println("No presentation JSON, using sample.")
        return Presentation.SAMPLE
    }
    return try {
        Json { ignoreUnknownKeys = true }
            .decodeFromString<Presentation>(file.readText(Charsets.UTF_8))
            .also { println("Loaded ${it.slides.size} slides from ${file.absolutePath}") }
    } catch (e: Exception) {
        println("Failed to parse input file: ${e.message}. Using sample.")
        Presentation.SAMPLE
    }
}

private fun takeExistingFile(path: String?): File? =
    path?.let(::File)?.takeIf { it.exists() }

private inline val GraphicsDevice.windowPosition: WindowPosition
    get() = defaultConfiguration.bounds.run {
        WindowPosition(x = x.dp, y = y.dp)
    }
