@file:OptIn(ExperimentalAtomicApi::class)

package `in`.procyk.slides.vm

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyDown
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.procyk.slides.model.Presentation
import `in`.procyk.slides.model.Slide
import `in`.procyk.slides.search.SlideSearchEngine
import `in`.procyk.slides.vm.SearchState.HideSlides
import `in`.procyk.slides.vm.SearchState.ShowSlides
import `in`.procyk.slides.vm.SearchState.Results
import `in`.procyk.slides.vm.SearchState.Typing
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.serialization.json.Json
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.seconds

sealed class SearchState {
    data object HideSlides : SearchState()
    data object ShowSlides : SearchState()
    data class Typing(val query: String) : SearchState()
    data class Results(val query: String, val indices: List<Int>, val resultIndex: Int?) :
        SearchState()
}

@OptIn(ExperimentalCoroutinesApi::class)
class SlidesViewModel(
    private val searchEngine: SlideSearchEngine,
    private val store: KStore<SlidesStore>,
) : ViewModel() {

    val fontScale: StateFlow<Float>
        field = MutableStateFlow(1f)

    private val presentation: StateFlow<Presentation> =
        store.updates.mapNotNull { it?.presentation }
            .stateIn(viewModelScope, SharingStarted.Lazily, Presentation.EMPTY)

    val slides: StateFlow<List<Slide>> =
        presentation.map { it.slides }
            .stateIn(viewModelScope, SharingStarted.Lazily, Presentation.EMPTY.slides)

    val slideIndex: StateFlow<Int>
        field = MutableStateFlow(0)

    val searchState: StateFlow<SearchState>
        field = MutableStateFlow<SearchState>(ShowSlides)

    val showSlide: StateFlow<Boolean> =
        searchState.map { it is ShowSlides }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val inactivityJob = AtomicReference<Job?>(null)

    init {
        val trigger = Channel<Unit>()

        viewModelScope.launch {
            while (currentCoroutineContext().isActive) select {
                onTimeout(5.seconds) {
                    searchState.update {
                        when (it) {
                            HideSlides -> HideSlides
                            is Results, ShowSlides, is Typing -> ShowSlides
                        }
                    }
                }
                trigger.onReceiveCatching { }
            }
        }
        viewModelScope.launch {
            searchState.filter { it is Typing || it is Results }.collectLatest {
                trigger.send(Unit)
            }
        }
    }

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyDown) return true

        when (keyEvent.key) {
            Key.Plus, Key.Equals -> increaseFontSize()

            Key.Minus -> decreaseFontSize()

            Key.Escape -> searchState.update {
                when (it) {
                    ShowSlides -> HideSlides
                    is Results, is Typing, HideSlides -> ShowSlides
                }
            }

            Key.Backspace -> handleBackspace(removeAll = keyEvent.isAltPressed || keyEvent.isMetaPressed)

            Key.Enter -> navigateSearchResult(forward = !keyEvent.isShiftPressed)

            Key.DirectionRight, Key.DirectionDown -> when (searchState.value) {
                is ShowSlides -> navigateNext()
                is Results, is Typing, is HideSlides -> {}
            }

            Key.DirectionLeft, Key.DirectionUp -> when (searchState.value) {
                is ShowSlides -> navigatePrev()
                is Results, is Typing, is HideSlides -> {}
            }

            else -> {
                val char = keyEvent.utf16CodePoint.toChar()
                if (keyEvent.utf16CodePoint > 0 && (char.isLetterOrDigit() || char == ' ')) {
                    appendSearchChar(char)
                }
            }
        }
        return true
    }

    fun navigateTo(index: Int) {
        slideIndex.update { index.coerceIn(0, slides.value.lastIndex) }
    }

    private fun appendSearchChar(char: Char) {
        searchState.update { current ->
            val newQuery = when (current) {
                is HideSlides -> return@update current
                is ShowSlides -> char.toString()
                is Typing -> current.query + char
                is Results -> current.query + char
            }
            Typing(newQuery)
        }
    }

    private fun handleBackspace(removeAll: Boolean) {
        searchState.update { current ->
            when (current) {
                is Typing if current.query.length > 1 -> Typing(query = current.query.dropLast(if (removeAll) current.query.length else 1))

                is Typing -> ShowSlides
                is Results if current.query.length > 1 -> Typing(query = current.query.dropLast(if (removeAll) current.query.length else 1))

                is Results -> ShowSlides
                else -> current
            }
        }
    }

    private fun navigateSearchResult(forward: Boolean) {
        when (val current = searchState.value) {
            is Typing -> {
                val results = searchEngine.search(current.query.trim(), slides.value)
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

            is ShowSlides -> {}
            is HideSlides -> {}
        }
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

    fun increaseFontSize() {
        fontScale.update { it + 0.1f }
    }

    fun decreaseFontSize() {
        fontScale.update { (it - 0.1f).coerceAtLeast(0.1f) }
    }

    fun savePresentation() {
        val presentation = Json.encodeToString(presentation.value)
        savePresentation(presentation)
    }

    fun loadPresentation(presentation: Presentation) {
        viewModelScope.launch {
            store.update { it?.copy(presentation = presentation) }
        }
    }
}

internal expect fun savePresentation(json: String)
