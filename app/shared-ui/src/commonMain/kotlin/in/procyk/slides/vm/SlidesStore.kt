package `in`.procyk.slides.vm

import `in`.procyk.slides.model.Presentation
import kotlinx.serialization.Serializable

@Serializable
data class SlidesStore(
    val presentation: Presentation = Presentation.EMPTY
)