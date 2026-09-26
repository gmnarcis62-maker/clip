package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ClipbordApp
import com.example.themes.KeyboardTheme
import com.example.themes.ThemeManager
import com.example.ui.components.ClipbordTopBar
import com.example.ui.theme.VipGold
import kotlinx.coroutines.launch

@Composable
fun ThemesScreen(
    onBackClick: () -> Unit,
    onNavigateToVip: () -> Unit
) {
    val context = LocalContext.current
    val preferences = ClipbordApp.instance.preferences
    val scope = rememberCoroutineScope()

    val currentThemeId by preferences.themeId.collectAsState(initial = "turquoise")
    val isVip by preferences.isVip.collectAsState(initial = false)
    val themes = ThemeManager.ALL_THEMES

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "پوسته‌ها و قالب‌های کیبورد",
                    subtitle = "انتخاب از میان ۱۰ تم زیبا و اختصاصی",
                    onBackClick = onBackClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .testTag("themes_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(themes, key = { it.id }) { theme ->
                    val isSelected = theme.id == currentThemeId
                    val isLocked = theme.isPremium && !isVip

                    ThemeCard(
                        theme = theme,
                        isSelected = isSelected,
                        isLocked = isLocked,
                        onSelect = {
                            if (isLocked) {
                                Toast.makeText(
                                    context,
                                    "این پوسته مخصوص نسخه حرفه‌ای VIP است.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onNavigateToVip()
                            } else {
                                scope.launch {
                                    preferences.setThemeId(theme.id)
                                    // ✅ Sync the app's light/dark mode with the theme brightness
                                    preferences.setDarkMode(if (theme.isDark) "DARK" else "LIGHT")
                                    Toast.makeText(
                                        context,
                                        "پوسته «${theme.namePersian}» فعال شد.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: KeyboardTheme,
    isSelected: Boolean,
    isLocked: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("theme_card_${theme.id}"),
        shape = RoundedCornerShape(18.dp),
        color = theme.backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) theme.accentColor else Color.White.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(theme.accentColor)
                    )
                    Text(
                        text = theme.namePersian,
                        color = theme.keyTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.accentColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = theme.accentTextColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "پوسته فعال",
                                color = theme.accentTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (isLocked) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VipGold
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "VIP ویژه",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = theme.description,
                color = theme.keySubTextColor,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Realistic Complete Persian Keyboard Mockup
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = theme.surfaceColor,
                border = androidx.compose.foundation.BorderStroke(
                    0.5.dp,
                    Color.White.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Mini Toolbar
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(22.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = theme.surfaceColor
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("✨ AI", color = theme.accentColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text("📋 کلیپ‌بورد", color = theme.keySubTextColor, fontSize = 8.sp)
                            Text("📖 دیکشنری", color = theme.keySubTextColor, fontSize = 8.sp)
                            Text("😊 ایموجی", color = theme.keySubTextColor, fontSize = 8.sp)
                            Text("⚙️", color = theme.keySubTextColor, fontSize = 9.sp)
                        }
                    }

                    // Mini Suggestion bar
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(22.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = theme.suggestionBarBackgroundColor
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سلام", color = theme.suggestionHighlightColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("وقت بخیر", color = theme.suggestionTextColor, fontSize = 9.sp)
                            Text("خیلی ممنون", color = theme.suggestionTextColor, fontSize = 9.sp)
                        }
                    }

                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "چ", "پ").forEach { char ->
                            MiniKey(char = char, theme = theme, modifier = Modifier.weight(1f))
                        }
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ", "و", "ئ").forEach { char ->
                            MiniKey(char = char, theme = theme, modifier = Modifier.weight(1f))
                        }
                    }

                    // Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        MiniKey(char = "⇧", theme = theme, isSpecial = true, modifier = Modifier.weight(1.3f))
                        listOf("ظ", "ط", "ز", "ر", "ذ", "د", "ژ", "پ", "و", ".").forEach { char ->
                            MiniKey(char = char, theme = theme, modifier = Modifier.weight(1f))
                        }
                        MiniKey(char = "⌫", theme = theme, isSpecial = true, modifier = Modifier.weight(1.3f))
                    }

                    // Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.5.dp)
                    ) {
                        MiniKey(char = "۱۲۳", theme = theme, isSpecial = true, modifier = Modifier.weight(1.4f))
                        MiniKey(char = "🌐", theme = theme, isSpecial = true, modifier = Modifier.weight(1.1f))
                        MiniKey(char = "نیم‌فاصله", theme = theme, isSpecial = true, modifier = Modifier.weight(1.7f))
                        MiniKey(char = "فاصله", theme = theme, modifier = Modifier.weight(4.0f))
                        MiniKey(char = "،", theme = theme, modifier = Modifier.weight(1.0f))
                        MiniKey(char = "↵", theme = theme, isAction = true, modifier = Modifier.weight(1.5f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.White.copy(alpha = 0.12f) else theme.accentColor,
                    contentColor = if (isSelected) theme.keyTextColor else theme.accentTextColor
                )
            ) {
                Text(
                    text = if (isSelected) "این پوسته در حال استفاده است"
                    else if (isLocked) "بازگشایی در نسخه VIP"
                    else "فعال‌سازی این پوسته",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun MiniKey(
    char: String,
    theme: KeyboardTheme,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false,
    isAction: Boolean = false
) {
    val bg = when {
        isAction -> theme.accentColor
        isSpecial -> theme.specialKeyBackgroundColor
        else -> theme.keyBackgroundColor
    }
    val textColor = when {
        isAction -> theme.accentTextColor
        isSpecial -> theme.specialKeyTextColor
        else -> theme.keyTextColor
    }

    val fontSize = when {
        char.length > 4 -> 6.5.sp
        char.length > 2 -> 7.5.sp
        else -> 8.5.sp
    }

    Box(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(
                width = if (theme.keyBorderColor != Color.Transparent) 0.5.dp else 0.dp,
                color = theme.keyBorderColor,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}