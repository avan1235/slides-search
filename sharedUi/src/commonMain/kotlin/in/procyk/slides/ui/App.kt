package `in`.procyk.slides.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import `in`.procyk.slides.search.NaiveSearchEngine
import `in`.procyk.slides.vm.SlidesViewModel

@Composable
fun App() {
    val factory = viewModelFactory {
        initializer { SlidesViewModel(searchEngine = NaiveSearchEngine) }
    }
    val vm: SlidesViewModel = viewModel(factory = factory)

    MaterialTheme {
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
