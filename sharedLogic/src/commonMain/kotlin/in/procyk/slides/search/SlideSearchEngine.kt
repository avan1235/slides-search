package `in`.procyk.slides.search

import `in`.procyk.slides.model.Slide

interface SlideSearchEngine {
    /**
     * Search through [slides] for the given [query].
     * Returns a list of indices into [slides], ordered by relevance (best match first).
     * Returns an empty list if no matches are found.
     */
    fun search(query: String, slides: List<Slide>): List<Int>
}
