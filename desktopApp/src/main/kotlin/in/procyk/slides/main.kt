package `in`.procyk.slides

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import `in`.procyk.slides.model.Presentation
import `in`.procyk.slides.search.LuceneSearchEngine
import `in`.procyk.slides.ui.ControlScreen
import `in`.procyk.slides.ui.SlidesScreen
import `in`.procyk.slides.vm.SlidesViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.awt.FileDialog
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.FilenameFilter
import java.util.Locale
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


fun main(args: Array<String>) {
    val presentation = loadPresentation(args.getOrNull(0))

    val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices

    val control = screens.firstOrNull() ?: error("no screen to control slides")
    val slides = screens.drop(1).firstOrNull {
        it.isFullScreenSupported && it.defaultConfiguration.bounds.run { width > height }
    } ?: error("no screen to display slides on")

    val controlPosition = control.windowPosition
    val slidesPosition = slides.windowPosition

    application {
        var vm by remember {
            mutableStateOf(
                SlidesViewModel(
                    searchEngine = LuceneSearchEngine(),
                    presentation = presentation,
                )
            )
        }
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = controlPosition,
                width = Dp.Unspecified,
                height = Dp.Unspecified,
            ),
            onKeyEvent = { vm.onKeyEvent(it) },
        ) {
            MenuBar {
                Menu("File") {
                    Item("Open", onClick = {
                        val dialog =
                            FileDialog(window, "Select presentation JSON file", FileDialog.LOAD)
                        dialog.filenameFilter = FilenameFilter { _: File?, name: String? ->
                            name?.lowercase(Locale.getDefault())?.endsWith(".json") == true
                        }
                        dialog.isVisible = true

                        val fileName = dialog.file
                        val directory = dialog.directory

                        if (fileName != null && directory != null) {
                            val file = File(directory, fileName)
                            vm = SlidesViewModel(
                                searchEngine = LuceneSearchEngine(),
                                presentation = loadPresentation(file.absolutePath),
                            )
                        }
                    })
                }
                Menu("Slide") {
                    Item("Next", onClick = { vm.navigateNext() })
                    Item("Previous", onClick = { vm.navigatePrev() })
                    Separator()
                    Item("First", onClick = { vm.navigateFirst() })
                    Item("Last", onClick = { vm.navigateLast() })
                }
            }
            ControlScreen(vm)
        }

        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = slidesPosition,
                placement = WindowPlacement.Fullscreen,
            ),
            onKeyEvent = { vm.onKeyEvent(it) },
        ) {
            SlidesScreen(vm)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private val ReadPresentationJson = Json {
    ignoreUnknownKeys = true
    allowTrailingComma = true
}

private fun loadPresentation(path: String?): Presentation {
    val file = path.let(::takeExistingFile)
        ?: takeExistingFile("./presentation.json")
        ?: takeExistingFile("../presentation.json")
    if (file == null) {
        println("No presentation JSON, using sample.")
        return Presentation.SAMPLE
    }
    return try {
        ReadPresentationJson
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
