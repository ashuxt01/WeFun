package com.example.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Immutable
data class WefunExtraColors(
    val gold: Color,
    val pink: Color,
    val cyan: Color,
    val rose: Color,
    val emerald: Color,
    val warning: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val elevatedCard: Color,
    val mutedText: Color,
    val purpleGradientStart: Color,
    val purpleGradientEnd: Color,
    val isDark: Boolean,
) {
    val purpleGradientBrush: Brush
        get() = Brush.linearGradient(listOf(purpleGradientStart, purpleGradientEnd))

    val brandGradientBrush: Brush
        get() = Brush.horizontalGradient(listOf(purpleGradientStart, pink))

    val goldGradientBrush: Brush
        get() = Brush.horizontalGradient(listOf(gold, GoldAmber))
}

val LocalWefunColors = staticCompositionLocalOf {
    WefunExtraColors(
        gold = PremiumGold,
        pink = PinkAccent,
        cyan = CyanPulse,
        rose = ErrorRed,
        emerald = SuccessGreen,
        warning = WarningAmber,
        glassSurface = CardBackground,
        glassBorder = BorderColor,
        elevatedCard = SecondaryBackground,
        mutedText = SecondaryText,
        purpleGradientStart = PrimaryPurple,
        purpleGradientEnd = DeepPurple,
        isDark = false,
    )
}

private val WefunLightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1E8FF),
    onPrimaryContainer = DeepPurple,
    secondary = PinkAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEAF4),
    onSecondaryContainer = Color(0xFFB31263),
    tertiary = PremiumGold,
    onTertiary = PrimaryText,
    tertiaryContainer = Color(0xFFFFF8DC),
    onTertiaryContainer = Color(0xFF7A5900),
    background = PrimaryBackground,
    onBackground = PrimaryText,
    surface = SecondaryBackground,
    onSurface = PrimaryText,
    surfaceVariant = CardBackground,
    onSurfaceVariant = SecondaryText,
    surfaceContainerLowest = PrimaryBackground,
    surfaceContainerLow = SecondaryBackground,
    surfaceContainer = SecondaryBackground,
    surfaceContainerHigh = CardBackground,
    surfaceContainerHighest = Color(0xFFF0F0F6),
    outline = BorderColor,
    outlineVariant = BorderColor,
    error = ErrorRed,
    onError = Color.White,
)

private val WefunDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = DeepPurple,
    onPrimaryContainer = TextPrimaryDark,
    secondary = PinkAccent,
    onSecondary = Color.White,
    secondaryContainer = CharcoalCard,
    onSecondaryContainer = PinkAccent,
    tertiary = PremiumGold,
    onTertiary = CharcoalBackground,
    tertiaryContainer = Color(0xFF332800),
    onTertiaryContainer = PremiumGold,
    background = CharcoalBackground,
    onBackground = TextPrimaryDark,
    surface = CharcoalSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerLowest = CharcoalBackground,
    surfaceContainerLow = CharcoalSurface,
    surfaceContainer = CharcoalSurface,
    surfaceContainerHigh = CharcoalCard,
    surfaceContainerHighest = CharcoalElevated,
    outline = CharcoalGlassBorder,
    outlineVariant = CharcoalGlassBorder,
    error = ErrorRed,
    onError = Color.White,
)

val WefunShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun WEFUNTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    WefunTheme(
        darkTheme = darkTheme,
        content = content,
    )
}

@Composable
fun WefunTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) WefunDarkColorScheme else WefunLightColorScheme
    val extraColors = if (darkTheme) {
        WefunExtraColors(
            gold = PremiumGold,
            pink = PinkAccent,
            cyan = CyanPulse,
            rose = ErrorRed,
            emerald = SuccessGreen,
            warning = WarningAmber,
            glassSurface = CharcoalCard,
            glassBorder = CharcoalGlassBorder,
            elevatedCard = CharcoalElevated,
            mutedText = TextMutedDark,
            purpleGradientStart = PrimaryPurple,
            purpleGradientEnd = DeepPurple,
            isDark = true,
        )
    } else {
        WefunExtraColors(
            gold = Color(0xFFE6A800),
            pink = PinkAccent,
            cyan = TurquoiseActive,
            rose = ErrorRed,
            emerald = SuccessGreen,
            warning = WarningAmber,
            glassSurface = CardBackground,
            glassBorder = BorderColor,
            elevatedCard = SecondaryBackground,
            mutedText = SecondaryText,
            purpleGradientStart = PrimaryPurple,
            purpleGradientEnd = DeepPurple,
            isDark = false,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalWefunColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WEFUNTypography,
            shapes = WefunShapes,
            content = content,
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
