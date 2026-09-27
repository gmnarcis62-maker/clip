package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color(0xFF07211E),
    primaryContainer = Color(0xFF004D46),
    onPrimaryContainer = Color(0xFF70F8EA),
    secondary = SecondaryGold,
    onSecondary = Color(0xFF422C00),
    secondaryContainer = Color(0xFF5F4100),
    onSecondaryContainer = Color(0xFFFFDEA3),
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF334155),
    outlineVariant = Color(0x3300D2BE)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCyanVariant,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCEF7F2),
    onPrimaryContainer = Color(0xFF003833),
    secondary = SecondaryGold,
    onSecondary = Color(0xFF3B2700),
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextDarkMuted,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0x3300D2BE)
)

private val AmoledColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color.Black,
    secondary = SecondaryGold,
    onSecondary = Color.Black,
    background = AmoledBackground,
    onBackground = TextWhite,
    surface = AmoledSurface,
    onSurface = TextWhite,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = TextMuted
)

@Composable
fun MyApplicationTheme(
    darkModeOption: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val darkTheme = when (darkModeOption) {
        "LIGHT" -> false
        "DARK", "AMOLED" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        darkModeOption == "AMOLED" -> AmoledColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // ✅ With enableEdgeToEdge() in MainActivity, system bars are already
    //    transparent. We only need to flip the icon color (light/dark)
    //    so it stays readable on top of the app content.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}