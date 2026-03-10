package `in`.procyk.slides.search

import `in`.procyk.slides.model.Slide

data object NaiveSearchEngine : SlideSearchEngine {
    override fun search(query: String, slides: List<Slide>): List<Int> {
        if (query.isBlank()) return emptyList()
        val normalizedQuery = query.lowercase()
        val titleMatches = slides.mapIndexedNotNull { index, slide ->
            val title = slide.title?.lowercase() ?: return@mapIndexedNotNull null
            if (title.contains(normalizedQuery)) index else null
        }
        val contentMatches = slides.mapIndexedNotNull { index, slide ->
            if (index in titleMatches) return@mapIndexedNotNull null
            if (slide.content.lowercase().contains(normalizedQuery)) index else null
        }
        return titleMatches + contentMatches
    }
}
