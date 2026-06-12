package `in`.procyk.slides.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2E2E2E),
    onPrimaryContainer = Color(0xFFE5E5E5),
    inversePrimary = Color(0xFF303030),

    secondary = Color(0xFFC6C6C6),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color(0xFFCCCCCC),

    tertiary = Color(0xFFAFAFAF),
    onTertiary = Color(0xFF1A1A1A),
    tertiaryContainer = Color(0xFF292929),
    onTertiaryContainer = Color(0xFFE8E8E8),

    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFF444444),
    onSurfaceVariant = Color(0xFFC4C4C4),

    surfaceTint = Color(0xFFFFFFFF),

    inverseSurface = Color(0xFFE5E5E5),
    inverseOnSurface = Color(0xFF121212),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF8E8E8E),
    outlineVariant = Color(0xFF444444),

    scrim = Color(0xFF000000),

    surfaceBright = Color(0xFF3A3A3A),
    surfaceDim = Color(0xFF000000),

    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF111111),
    surfaceContainer = Color(0xFF1A1A1A),
    surfaceContainerHigh = Color(0xFF222222),
    surfaceContainerHighest = Color(0xFF2E2E2E),

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
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
) = MaterialTheme(
    colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme,
    content = content,
)