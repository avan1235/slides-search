package `in`.procyk.slides

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
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
import kotlinx.serialization.json.decodeFromStream
import java.awt.FileDialog
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.FilenameFilter
import java.io.InputStream
import java.util.Locale


fun main(args: Array<String>) {
    val presentation = loadPresentation(args.getOrNull(0))

    val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices

    val control = screens.firstOrNull() ?: error("no screen to control slides")
    val slides = screens.drop(1).firstOrNull {
        it.isFullScreenSupported
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
                    Item(
                        "Open .json Presentation",
                        onClick = {
                            openPresentation("json", ::loadJsonPresentation)?.let { vm = it }
                        })
                    Item(
                        "Open .pptx Presentation",
                        onClick = {
                            openPresentation("pptx", ::parsePptxPresentation)?.let { vm = it }
                        })
                    Separator()
                    Item(
                        "Save Presentation to .json",
                        onClick = { vm.savePresentation() }
                    )
                }
                Menu("Slide") {
                    Item("Next", onClick = { vm.navigateNext() })
                    Item("Previous", onClick = { vm.navigatePrev() })
                    Separator()
                    Item("First", onClick = { vm.navigateFirst() })
                    Item("Last", onClick = { vm.navigateLast() })
                }
                Menu("View") {
                    Item("Increase Font Size", onClick = { vm.increaseFontSize() })
                    Item("Decrease Font Size", onClick = { vm.decreaseFontSize() })
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
    val file =
        path.let(::takeExistingFile)
            ?: takeExistingFile("./presentation.json")
            ?: takeExistingFile("../presentation.json")
    if (file == null) {
        return Presentation.EMPTY
    }
    return file.inputStream().use(::loadJsonPresentation)
}

@OptIn(ExperimentalSerializationApi::class)
private fun loadJsonPresentation(input: InputStream): Presentation = try {
    ReadPresentationJson.decodeFromStream<Presentation>(input)
} catch (_: Exception) {
    Presentation.EMPTY
}

private fun FrameWindowScope.openPresentation(
    ext: String,
    load: (InputStream) -> Presentation
): SlidesViewModel? {
    val dialog =
        FileDialog(window, "Select presentation ${ext.uppercase()} file", FileDialog.LOAD).apply {
            filenameFilter = FilenameFilter { _: File?, name: String? ->
                name?.lowercase(Locale.getDefault())?.endsWith(".$ext") == true
            }
            isVisible = true
        }

    val fileName = dialog.file
    val directory = dialog.directory

    return if (fileName != null && directory != null) {
        val file = File(directory, fileName)
        SlidesViewModel(
            searchEngine = LuceneSearchEngine(),
            presentation = file.inputStream().use(load),
        )
    } else null
}

private fun takeExistingFile(path: String?): File? = path?.let(::File)?.takeIf { it.exists() }

private inline val GraphicsDevice.windowPosition: WindowPosition
    get() = defaultConfiguration.bounds.run {
        WindowPosition(x = x.dp, y = y.dp)
    }
