package `in`.procyk.slides.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.procyk.slides.vm.SlidesViewModel

@Composable
fun SlidesScreen(vm: SlidesViewModel) {
    val isDarkTheme by vm.isDarkTheme.collectAsState()
    SlidesSearchTheme(isDarkTheme = isDarkTheme) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val slides by vm.slides.collectAsState()
            val slideIndex by vm.slideIndex.collectAsState()
            val showSlide by vm.showSlide.collectAsState()
            val fontScale by vm.fontScale.collectAsState()
            val currentSlide = slides.getOrNull(slideIndex)

            if (currentSlide != null && showSlide) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val title = currentSlide.title
                    if (title != null) {
                        Text(
                            text = title,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    Text(
                        text = currentSlide.content,
                        fontSize = (fontScale * FONT_SIZE_SP).sp,
                        lineHeight = (fontScale * 1.2 * FONT_SIZE_SP).sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private const val FONT_SIZE_SP = 64