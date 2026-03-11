package `in`.procyk.slides.model

import kotlinx.serialization.Serializable

@Serializable
data class Slide(
    val content: String,
    val title: String? = null,
    val notes: String? = null,
) {
    companion object {
        val EMPTY: Slide = Slide(content = "")
    }
}

@Serializable
data class Presentation(
    val slides: List<Slide>,
) {
    companion object {
        val EMPTY: Presentation = Presentation(slides = listOf(Slide.EMPTY))
    }
}
