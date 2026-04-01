package `in`.procyk.slides

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import `in`.procyk.slides.ui.App
import `in`.procyk.slides.vm.viewModelModule
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(
            viewModelModule,
        )
    }
    ComposeViewport {
        App()
    }
}