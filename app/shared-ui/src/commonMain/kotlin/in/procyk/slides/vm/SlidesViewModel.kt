@file:OptIn(ExperimentalAtomicApi::class)

package `in`.procyk.slides.vm

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.procyk.slides.model.Presentation
import `in`.procyk.slides.model.Slide
import `in`.procyk.slides.search.SlideSearchEngine
import `in`.procyk.slides.vm.SearchState.Idle
import `in`.procyk.slides.vm.SearchState.Results
import `in`.procyk.slides.vm.SearchState.Typing
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.seconds

sealed class SearchState {
    data object Idle : SearchState()
    data class Typing(val query: String) : SearchState()
    data class Results(val query: String, val indices: List<Int>, val resultIndex: Int?) :
        SearchState()
}

class SlidesViewModel(
    private val searchEngine: SlideSearchEngine,
    private val presentation: Presentation = Presentation.EMPTY,
) : ViewModel() {

    val fontScale: StateFlow<Float>
        field = MutableStateFlow(1f)

    val slides: StateFlow<List<Slide>>
        field = MutableStateFlow(presentation.slides)

    val slideIndex: StateFlow<Int>
        field = MutableStateFlow(0)

    val searchState: StateFlow<SearchState>
        field = MutableStateFlow<SearchState>(Idle)

    private val inactivityJob = AtomicReference<Job?>(null)

    private fun resetInactivityTimer() {
        inactivityJob.exchange(viewModelScope.launch {
            delay(10.seconds)
            clearSearch()
        })?.cancel()
    }

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyUp) return false

        return when (keyEvent.key) {
            Key.Plus, Key.Equals -> {
                increaseFontSize()
                true
            }
            Key.Minus -> {
                decreaseFontSize()
                true
            }
            Key.Escape -> {
                clearSearch()
                true
            }

            Key.Enter -> {
                navigateSearchResult(forward = !keyEvent.isShiftPressed)
                true
            }

            Key.DirectionRight, Key.DirectionDown -> when (searchState.value) {
                is Idle -> {
                    navigateNext(); true
                }

                else -> false
            }

            Key.DirectionLeft, Key.DirectionUp -> when (searchState.value) {
                is Idle -> {
                    navigatePrev(); true
                }

                else -> false
            }

            Key.Backspace -> {
                handleBackspace()
                true
            }

            else -> {
                val char = keyEvent.utf16CodePoint.toChar()
                if (keyEvent.utf16CodePoint > 0 && (char.isLetterOrDigit() || char == ' ')) {
                    appendSearchChar(char)
                    true
                } else false
            }
        }
    }

    fun navigateTo(index: Int) {
        slideIndex.update { index.coerceIn(0, slides.value.lastIndex) }
    }

    private fun appendSearchChar(char: Char) {
        searchState.update { current ->
            val newQuery = when (current) {
                is Idle -> char.toString()
                is Typing -> current.query + char
                is Results -> current.query + char
            }
            Typing(newQuery)
        }
        resetInactivityTimer()
    }

    private fun handleBackspace() {
        searchState.update { current ->
            when (current) {
                is Typing if current.query.length > 1 -> Typing(current.query.dropLast(1))

                is Typing -> Idle
                is Results if current.query.length > 1 -> Typing(current.query.dropLast(1))

                is Results -> Idle
                else -> current
            }
        }
        resetInactivityTimer()
    }

    private fun navigateSearchResult(forward: Boolean) {
        when (val current = searchState.value) {
            is Typing -> {
                val results = searchEngine.search(current.query, slides.value)
                val resultIndex = 0.takeUnless { results.isEmpty() }
                if (resultIndex != null) {
                    slideIndex.update { results[resultIndex] }
                }
                searchState.update { Results(current.query, results, resultIndex) }
            }

            is Results -> {
                val next = when {
                    forward -> current.resultIndex?.let { (it + 1) % current.indices.size }
                    else -> current.resultIndex?.let { (it - 1 + current.indices.size) % current.indices.size }
                }
                if (next != null) {
                    slideIndex.update { current.indices[next] }
                }
                searchState.update { current.copy(resultIndex = next) }
            }

            is Idle -> {}
        }
    }

    private fun clearSearch() {
        inactivityJob.exchange(null)?.cancel()
        searchState.update { Idle }
    }

    fun navigateNext() {
        val count = slides.value.size
        if (count == 0) return
        slideIndex.update { (it + 1).coerceAtMost(count - 1) }
    }

    fun navigatePrev() {
        slideIndex.update { (it - 1).coerceAtLeast(0) }
    }

    fun navigateFirst() {
        slideIndex.update { 0 }
    }

    fun navigateLast() {
        slideIndex.update { slides.value.size - 1 }
    }

    private fun increaseFontSize() {
        fontScale.update { it + 0.1f }
    }

    private fun decreaseFontSize() {
        fontScale.update { (it - 0.1f).coerceAtLeast(0.1f) }
    }
}
