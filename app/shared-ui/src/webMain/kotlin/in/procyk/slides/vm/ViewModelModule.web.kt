package `in`.procyk.slides.vm

import `in`.procyk.slides.search.NaiveSearchEngine
import `in`.procyk.slides.search.SlideSearchEngine
import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.storage.StorageCodec

internal actual fun createSearchEngine(): SlideSearchEngine =
    NaiveSearchEngine

internal actual fun createCodec(): Codec<SlidesStore> =
    StorageCodec(".slides")