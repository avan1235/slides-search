package `in`.procyk.slides.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF111111),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFECECEC),
    onPrimaryContainer = Color(0xFF111111),
    inversePrimary = Color(0xFFD6D6D6),

    secondary = Color(0xFF424242),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color(0xFF1A1A1A),

    tertiary = Color(0xFF616161),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8E8E8),
    onTertiaryContainer = Color(0xFF1A1A1A),

    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF111111),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),

    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF444444),

    surfaceTint = Color(0xFF111111),

    inverseSurface = Color(0xFF121212),
    inverseOnSurface = Color(0xFFF5F5F5),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),

    scrim = Color(0x66000000),

    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFEDEDED),

    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F5F5),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFEBEBEB),
    surfaceContainerHighest = Color(0xFFE5E5E5),

    primaryFixed = Color(0xFFECECEC),
    primaryFixedDim = Color(0xFFD6D6D6),
    onPrimaryFixed = Color(0xFF111111),
    onPrimaryFixedVariant = Color(0xFF424242),

    secondaryFixed = Color(0xFFE0E0E0),
    secondaryFixedDim = Color(0xFFC6C6C6),
    onSecondaryFixed = Color(0xFF1A1A1A),
    onSecondaryFixedVariant = Color(0xFF424242),

    tertiaryFixed = Color(0xFFE8E8E8),
    tertiaryFixedDim = Color(0xFFD0D0D0),
    onTertiaryFixed = Color(0xFF1A1A1A),
    onTertiaryFixedVariant = Color(0xFF555555),
)

@Composable
fun SlidesSearchTheme(
    content: @Composable () -> Unit,
) = MaterialTheme(
    colorScheme = LightColorScheme,
    content = content
)