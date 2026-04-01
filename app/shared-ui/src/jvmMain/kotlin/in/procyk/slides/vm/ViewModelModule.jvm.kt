package `in`.procyk.slides.vm

import `in`.procyk.slides.search.LuceneSearchEngine
import `in`.procyk.slides.search.SlideSearchEngine
import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.file.FileCodec
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import net.harawata.appdirs.AppDirsFactory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal actual fun createSearchEngine(): SlideSearchEngine =
    LuceneSearchEngine()

@OptIn(ExperimentalUuidApi::class)
internal actual fun createCodec(): Codec<SlidesStore> {
    val filesDir = AppDirsFactory.getInstance().getUserDataDir(
        "in.procyk.slides",
        "1.0",
        "Maciej Procyk",
    )
    val file = Path(filesDir)

    with(SystemFileSystem) { if (!exists(file)) createDirectories(file) }


    return FileCodec(
        file = Path(file, ".slides"),
        tempFile = Path(file, Uuid.random().toHexDashString())
    )
}