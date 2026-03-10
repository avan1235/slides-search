package `in`.procyk.slides.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import `in`.procyk.slides.model.Slide
import `in`.procyk.slides.ui.ResultPosition.*
import `in`.procyk.slides.vm.SearchState
import `in`.procyk.slides.vm.SlidesViewModel

private const val NEIGHBOR_COUNT = 5
private const val SLIDE_ASPECT = 3f / 4f
private const val GAP_DP = 8
private const val OUTER_PADDING_DP = 16

private const val PREVIEW_FONT_REF_WIDTH_DP = 160
private const val PREVIEW_TITLE_FONT_SP = 6

@Composable
fun ControlScreen(vm: SlidesViewModel) {
    val slides by vm.slides.collectAsState()
    val slideIndex by vm.slideIndex.collectAsState()
    val searchState by vm.searchState.collectAsState()

    MaterialTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalItems = 2 * NEIGHBOR_COUNT + 1
            val totalGaps = totalItems - 1
            val usableWidth: Dp = maxWidth - (2 * OUTER_PADDING_DP).dp
            val previewWidthFromWidth: Dp = (usableWidth - (totalGaps * GAP_DP).dp) / NEIGHBOR_COUNT

            val totalRowGaps = 3
            val counterTextHeight = 24f
            val usableHeight: Dp =
                maxHeight - (2 * OUTER_PADDING_DP + counterTextHeight).dp - (totalRowGaps * GAP_DP).dp
            val previewWidthFromHeight: Dp = usableHeight / (SLIDE_ASPECT * (2 + 3))

            val previewWidth: Dp = minOf(previewWidthFromWidth, previewWidthFromHeight)
            val previewHeight: Dp = previewWidth * SLIDE_ASPECT
            val currentWidth: Dp = previewWidth * 3
            val currentHeight: Dp = currentWidth * SLIDE_ASPECT

            val scale = previewWidth.value / PREVIEW_FONT_REF_WIDTH_DP
            val previewTitleFontSize: TextUnit = (PREVIEW_TITLE_FONT_SP * scale).sp
            val currentScale = currentWidth.value / PREVIEW_FONT_REF_WIDTH_DP
            val fontSize = (PREVIEW_TITLE_FONT_SP * currentScale).sp

            Column(
                modifier = Modifier.align(Alignment.Center).wrapContentSize()
                    .padding(OUTER_PADDING_DP.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(GAP_DP.dp),
            ) {
                SlideNeighborRow(
                    slides = slides,
                    center = slideIndex,
                    from = slideIndex - NEIGHBOR_COUNT,
                    until = slideIndex,
                    currentIndex = slideIndex,
                    previewWidth = previewWidth,
                    previewHeight = previewHeight,
                    gap = GAP_DP.dp,
                    titleFontSize = previewTitleFontSize,
                    onSlideClick = vm::navigateTo,
                )

                SlideBox(
                    slide = slides.getOrNull(slideIndex),
                    index = slideIndex,
                    isCurrentSlide = true,
                    width = currentWidth,
                    height = currentHeight,
                    fontSize = fontSize,
                    onSlideClick = vm::navigateTo,
                )

                SlideNeighborRow(
                    slides = slides,
                    center = slideIndex,
                    from = slideIndex + 1,
                    until = slideIndex + 1 + NEIGHBOR_COUNT,
                    currentIndex = slideIndex,
                    previewWidth = previewWidth,
                    previewHeight = previewHeight,
                    gap = GAP_DP.dp,
                    titleFontSize = previewTitleFontSize,
                    onSlideClick = vm::navigateTo,
                )

                Text(
                    "${slideIndex + 1} / ${slides.size}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            when (val searchState = searchState) {
                is SearchState.Typing -> SearchQueryOverlay(
                    query = searchState.query,
                    resultPosition = Typing,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )

                is SearchState.Results -> SearchQueryOverlay(
                    query = searchState.query,
                    resultPosition = searchState.resultIndex?.let {
                        Found(it + 1, searchState.indices.size)
                    } ?: NoResults,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )

                is SearchState.Idle -> {}
            }
        }
    }
}

@Composable
private fun SlideNeighborRow(
    slides: List<Slide>,
    center: Int,
    from: Int,
    until: Int,
    currentIndex: Int,
    previewWidth: Dp,
    previewHeight: Dp,
    gap: Dp,
    titleFontSize: TextUnit,
    onSlideClick: (Int) -> Unit,
) {
    // Always render exactly NEIGHBOR_COUNT slots so the row is a fixed width,
    // keeping the center slide aligned regardless of how many real slides exist.
    Row(
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in from until until) {
            if (i in slides.indices) {
                SlideBox(
                    slide = slides[i],
                    index = i,
                    isCurrentSlide = i == currentIndex,
                    width = previewWidth,
                    height = previewHeight,
                    fontSize = titleFontSize,
                    onSlideClick = onSlideClick,
                )
            } else {
                Spacer(modifier = Modifier.size(previewWidth, previewHeight))
            }
        }
    }
}

@Composable
private fun SlideBox(
    slide: Slide?,
    index: Int,
    isCurrentSlide: Boolean,
    width: Dp,
    height: Dp,
    fontSize: TextUnit,
    onSlideClick: (Int) -> Unit,
) {
    val containerColor = if (isCurrentSlide) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (isCurrentSlide) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = Modifier.size(width, height).background(containerColor).then(
            if (isCurrentSlide) Modifier.border(2.dp, MaterialTheme.colorScheme.primary)
            else Modifier
        ).clickable { onSlideClick(index) }.padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (slide != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = slide.content,
                    fontSize = fontSize,
                    lineHeight = 1.2 * fontSize,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                )
            }
        }
    }
}

private sealed class ResultPosition {
    data class Found(val current: Int, val count: Int) : ResultPosition()
    data object Typing : ResultPosition()
    data object NoResults : ResultPosition()
}

@Composable
private fun SearchQueryOverlay(
    query: String,
    resultPosition: ResultPosition,
    modifier: Modifier,
) {

    TextField(
        value = TextFieldValue(buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(query)
            }
        }),
        textStyle = MaterialTheme.typography.bodyLarge,
        onValueChange = {},
        singleLine = true,
        readOnly = true,
        trailingIcon = when (resultPosition) {
            NoResults -> "0/0"
            is Found -> "${resultPosition.current}/${resultPosition.count}"
            else -> null
        }?.let {
            {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        },
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors().copy(errorTextColor = MaterialTheme.colorScheme.error),
        isError = resultPosition == NoResults,
    )
}
