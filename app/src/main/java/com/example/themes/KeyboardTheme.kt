package com.example.themes

import androidx.compose.ui.graphics.Color

data class KeyboardTheme(
    val id: String,
    val namePersian: String,
    val description: String,
    val isPremium: Boolean,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val keyBackgroundColor: Color,
    val keyGradientBottom: Color,
    val keyTextColor: Color,
    val keySubTextColor: Color,
    val specialKeyBackgroundColor: Color,
    val specialKeyTextColor: Color,
    val accentColor: Color,
    val accentTextColor: Color,
    val suggestionBarBackgroundColor: Color,
    val suggestionTextColor: Color,
    val suggestionHighlightColor: Color,
    val keyBorderColor: Color = Color.Transparent,
    val keyTopHighlightColor: Color = Color(0x1AFFFFFF),
    val keyShadowElevationDp: Float = 2.5f,
    val isDark: Boolean = true
)
