package `in`.procyk.slides.vm

import `in`.procyk.slides.search.SlideSearchEngine
import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.storeOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val viewModelModule = module {
    single {
        storeOf<SlidesStore>(
            codec = createCodec(),
            default = SlidesStore()
        )
    }
    single<SlideSearchEngine> { createSearchEngine() }
    viewModel<SlidesViewModel> {
        SlidesViewModel(get(), get())
    }
}

internal expect fun createCodec(): Codec<SlidesStore>

internal expect fun createSearchEngine(): SlideSearchEngine