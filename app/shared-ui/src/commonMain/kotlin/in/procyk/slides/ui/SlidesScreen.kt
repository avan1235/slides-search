package `in`.procyk.slides.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.procyk.slides.vm.SlidesViewModel

@Composable
fun SlidesScreen(vm: SlidesViewModel) {
    val slides by vm.slides.collectAsState()
    val slideIndex by vm.slideIndex.collectAsState()
    val fontScale by vm.fontScale.collectAsState()
    val currentSlide = slides.getOrNull(slideIndex)

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (currentSlide != null) {
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