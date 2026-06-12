package `in`.procyk.slides.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import `in`.procyk.slides.vm.SlidesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val vm = koinViewModel<SlidesViewModel>()
    val isDarkTheme by vm.isDarkTheme.collectAsState()
    SlidesSearchTheme(isDarkTheme = isDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            if (maxWidth > maxHeight) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(2f).fillMaxHeight()) {
                        SlidesScreen(vm)
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        ControlScreen(vm)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        SlidesScreen(vm)
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        ControlScreen(vm)
                    }
                }
            }
        }
    }
}
