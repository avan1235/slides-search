package `in`.procyk.slides.model

import kotlinx.serialization.Serializable

@Serializable
data class Slide(
    val content: String,
    val title: String? = null,
    val notes: String? = null,
)

@Serializable
data class Presentation(
    val slides: List<Slide>,
) {
    companion object {
        val SAMPLE: Presentation = Presentation(
            slides = listOf(
                Slide(
                    title = "Kotlin Multiplatform",
                    content = "Write once, run anywhere.\nTargets JVM, JS, Wasm, iOS, Android.",
                    notes = "Emphasize that shared business logic reduces duplication.",
                ),
                Slide(
                    title = "Compose Multiplatform",
                    content = "Declarative UI framework based on Jetpack Compose.\nSupports Desktop, Web, and Mobile.",
                    notes = "Demo the live reload feature.",
                ),
                Slide(
                    title = "State Management",
                    content = "AndroidX ViewModel with StateFlow.\nSingle source of truth across all views.",
                ),
                Slide(
                    content = "kotlinx.serialization provides cross-platform JSON support.\nAnnotate data classes with @Serializable.",
                    notes = "Show JSON output of a serialized Slide.",
                ),
                Slide(
                    title = "Search Architecture",
                    content = "Interface-based search engine.\nDesktop uses Apache Lucene, Web uses naive substring match.",
                ),
                Slide(
                    content = "ą→a  ź→z  ż→z  ć→c  ń→n\nó→o  ś→s  ę→e  ł→l",
                    notes = "The Lucene ASCIIFoldingFilter handles this transparently.",
                ),
                Slide(
                    title = "Thank You",
                    content = "Questions?\nContributions welcome on GitHub.",
                ),
            )
        )
    }
}
