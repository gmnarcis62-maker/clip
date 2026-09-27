package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ai.data.AiConfig
import com.example.domain.shamsi.PersianDateUtils
import com.example.themes.KeyboardTheme
import com.example.themes.ThemeManager
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.components.SectionTitle
import com.example.ui.theme.PrimaryCyan
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToThemes: () -> Unit,
    onNavigateToAi: () -> Unit = {}
) {
    val context = LocalContext.current
    val preferences = ClipbordApp.instance.preferences
    val scope = rememberCoroutineScope()

    val isVip by preferences.isVip.collectAsState(initial = false)
    val darkMode by preferences.darkMode.collectAsState(initial = "SYSTEM")
    val themeId by preferences.themeId.collectAsState(initial = "turquoise")
    val heightRatio by preferences.keyboardHeightRatio.collectAsState(initial = 1.0f)
    val fontSizeScale by preferences.fontSizeScale.collectAsState(initial = 1.0f)
    val vibrationEnabled by preferences.vibrationEnabled.collectAsState(initial = true)
    val vibrationStrength by preferences.vibrationStrength.collectAsState(initial = 25)
    val soundEnabled by preferences.soundEnabled.collectAsState(initial = true)
    val suggestionsEnabled by preferences.suggestionsEnabled.collectAsState(initial = true)
    val autoCorrectionEnabled by preferences.autoCorrectionEnabled.collectAsState(initial = true)
    val persianNumbersDefault by preferences.persianNumbersEnabled.collectAsState(initial = true)
    val halfSpaceEnabled by preferences.halfSpaceEnabled.collectAsState(initial = true)
    val voiceTypingEnabled by preferences.voiceTypingEnabled.collectAsState(initial = true)

    val aiEnabled by preferences.aiEnabled.collectAsState(initial = true)
    val aiButtonVisible by preferences.aiButtonVisible.collectAsState(initial = true)
    val aiTone by preferences.aiDefaultTone.collectAsState(initial = "NEUTRAL")
    val aiUsageCount by preferences.aiDailyUsageCount.collectAsState(initial = 0)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "تنظیمات برنامه",
                    subtitle = "شخصی‌سازی کیبورد مرسانا و مدیریت امکانات هوشمند",
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
                    .testTag("settings_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI Settings Card
                item {
                    PersianCard(borderColor = Color(0xFFFFB800).copy(alpha = 0.5f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle(title = "دستیار هوش مصنوعی مرسانا", icon = Icons.Default.AutoAwesome)
                            Button(
                                onClick = onNavigateToAi,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("تنظیمات AI", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isVip) "وضعیت: اشتراک ویژه VIP (نامحدود)" else "استفاده امروز: ${PersianDateUtils.toPersianDigits(aiUsageCount)} از ${PersianDateUtils.toPersianDigits(AiConfig.FREE_DAILY_REQUEST_LIMIT)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVip) Color(0xFF10B981) else Color(0xFFFFB800)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "فعال بودن دستیار هوش مصنوعی",
                            subtitle = "امکان استفاده از هوش مصنوعی برای بازنویسی، ترجمه و خلاصه‌سازی متن",
                            checked = aiEnabled,
                            onCheckedChange = { scope.launch { preferences.setAiEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "نمایش دکمه هوش مصنوعی در کیبورد",
                            subtitle = "نمایش دکمه اختصاصی AI در نوار بالای کیبورد",
                            checked = aiButtonVisible,
                            onCheckedChange = { scope.launch { preferences.setAiButtonVisible(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("لحن پیش‌فرض هوش مصنوعی:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf(
                                "NEUTRAL" to "معمولی",
                                "FRIENDLY" to "دوستانه",
                                "FORMAL" to "رسمی"
                            ).forEach { (tone, label) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = aiTone == tone,
                                        onClick = { scope.launch { preferences.setAiDefaultTone(tone) } },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryCyan)
                                    )
                                    Text(label, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Appearance, Size & Font with LIVE PREVIEW
                item {
                    PersianCard {
                        SectionTitle(title = "ظاهر، اندازه و چیدمان", icon = Icons.Default.Palette)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("پوسته کیبورد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("انتخاب رنگ و طرح دلخواه برای کیبورد", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                            Button(
                                onClick = onNavigateToThemes,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                            ) {
                                Text("مشاهده", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("حالت نمایش:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf(
                                "SYSTEM" to "خودکار",
                                "LIGHT" to "روشن",
                                "DARK" to "تیره",
                                "AMOLED" to "مشکی"
                            ).forEach { (mode, label) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = darkMode == mode,
                                        onClick = { scope.launch { preferences.setDarkMode(mode) } },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryCyan)
                                    )
                                    Text(label, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // ✅ پیش‌نمایش زنده‌ی کیبورد
                        Text(
                            "پیش‌نمایش زنده:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        KeyboardLivePreview(
                            heightRatio = heightRatio,
                            fontSizeScale = fontSizeScale,
                            themeId = themeId
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Keyboard Height Slider
                        Text(
                            "ارتفاع کیبورد: ${PersianDateUtils.toPersianDigits((heightRatio * 100).toInt())}%",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Slider(
                            value = heightRatio,
                            onValueChange = { scope.launch { preferences.setKeyboardHeight(it) } },
                            valueRange = 0.70f..1.40f,
                            steps = 6,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryCyan,
                                activeTrackColor = PrimaryCyan
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "اندازه فونت کیبورد: ${PersianDateUtils.toPersianDigits((fontSizeScale * 100).toInt())}%",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Slider(
                            value = fontSizeScale,
                            onValueChange = { scope.launch { preferences.setFontSizeScale(it) } },
                            valueRange = 0.80f..1.30f,
                            steps = 4,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryCyan,
                                activeTrackColor = PrimaryCyan
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "💡 با تغییر اسلایدرها، پیش‌نمایش بالا به‌صورت زنده به‌روزرسانی می‌شود تا دقیقاً ببینید کیبورد در گوشی شما چه اندازه‌ای خواهد شد.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Typing & Predictions
                item {
                    PersianCard {
                        SectionTitle(title = "تایپ هوشمند و پیش‌بینی متن", icon = Icons.Default.Psychology)

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "نمایش پیشنهاد کلمات",
                            subtitle = "نمایش کلمات پیشنهادی برای تایپ سریع‌تر و راحت‌تر",
                            checked = suggestionsEnabled,
                            onCheckedChange = { scope.launch { preferences.setSuggestionsEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "تصحیح خودکار غلط‌های تایپی",
                            subtitle = "تصحیح خودکار غلط‌های رایج هنگام تایپ فارسی",
                            checked = autoCorrectionEnabled,
                            onCheckedChange = { scope.launch { preferences.setAutoCorrectionEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "استفاده از اعداد فارسی",
                            subtitle = "نمایش اعداد به صورت فارسی (۱، ۲، ۳) در کیبورد",
                            checked = persianNumbersDefault,
                            onCheckedChange = { scope.launch { preferences.setPersianNumbersEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "نمایش نیم‌فاصله در کیبورد",
                            subtitle = "نمایش دکمه اختصاصی نیم‌فاصله برای تایپ صحیح کلمات فارسی",
                            checked = halfSpaceEnabled,
                            onCheckedChange = { scope.launch { preferences.setHalfSpaceEnabled(it) } }
                        )
                    }
                }

                // Sound & Vibration
                item {
                    PersianCard {
                        SectionTitle(title = "صدا و لرزش هنگام تایپ", icon = Icons.Default.Vibration)

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "لرزش هنگام فشردن کلید (Haptic)",
                            subtitle = "ایجاد لرزش کوتاه هنگام فشردن دکمه‌ها",
                            checked = vibrationEnabled,
                            onCheckedChange = { scope.launch { preferences.setVibrationEnabled(it) } }
                        )

                        if (vibrationEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "قدرت لرزش: ${PersianDateUtils.toPersianDigits(vibrationStrength)} میلی‌ثانیه",
                                fontSize = 12.sp
                            )
                            Slider(
                                value = vibrationStrength.toFloat(),
                                onValueChange = { scope.launch { preferences.setVibrationStrength(it.toInt()) } },
                                valueRange = 10f..60f,
                                colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "صدای کلیک هنگام فشردن کلید",
                            subtitle = "پخش صدای کوتاه هنگام فشردن دکمه‌ها",
                            checked = soundEnabled,
                            onCheckedChange = { scope.launch { preferences.setSoundEnabled(it) } }
                        )
                    }
                }

                // Voice Typing
                item {
                    PersianCard {
                        SectionTitle(title = "تایپ صوتی و گفتاری", icon = Icons.Default.TextFields)

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "فعال بودن تایپ صوتی",
                            subtitle = "امکان تبدیل گفتار به نوشتار با استفاده از میکروفون دستگاه",
                            checked = voiceTypingEnabled,
                            onCheckedChange = { scope.launch { preferences.setVoiceTypingEnabled(it) } }
                        )
                    }
                }

                // Reset
                item {
                    Button(
                        onClick = {
                            scope.launch {
                                preferences.setThemeId("turquoise")
                                preferences.setDarkMode("SYSTEM")
                                preferences.setKeyboardHeight(1.0f)
                                preferences.setFontSizeScale(1.0f)
                                preferences.setVibrationEnabled(true)
                                preferences.setVibrationStrength(25)
                                preferences.setSoundEnabled(true)
                                preferences.setSuggestionsEnabled(true)
                                preferences.setAutoCorrectionEnabled(true)
                                preferences.setPersianNumbersEnabled(true)
                                preferences.setHalfSpaceEnabled(true)
                                preferences.setAiEnabled(true)
                                preferences.setAiButtonVisible(true)
                                Toast.makeText(context, "تنظیمات به حالت پیش‌فرض بازگردانده شد.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("بازگرداندن تنظیمات به حالت پیش‌فرض", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF07211E),
                checkedTrackColor = PrimaryCyan
            )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// ✅ پیش‌نمایش زنده‌ی کیبورد — با هر تغییر اسلایدر به‌روز می‌شود
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun KeyboardLivePreview(
    heightRatio: Float,
    fontSizeScale: Float,
    themeId: String
) {
    val theme: KeyboardTheme = ThemeManager.getThemeById(themeId)

    // ارتفاع کل پیش‌نمایش متناسب با heightRatio
    val previewHeight = (170 * heightRatio).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(previewHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(theme.backgroundColor)
            .border(1.dp, theme.keyBorderColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // ردیف اعداد (فقط نمایشی)
            PreviewRow(
                labels = listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰"),
                theme = theme,
                fontSizeScale = fontSizeScale * 0.9f,
                weight = 1f
            )

            // ردیف اول حروف
            PreviewRow(
                labels = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح"),
                theme = theme,
                fontSizeScale = fontSizeScale
            )

            // ردیف دوم
            PreviewRow(
                labels = listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک"),
                theme = theme,
                fontSizeScale = fontSizeScale
            )

            // ردیف سوم
            PreviewRow(
                labels = listOf("ظ", "ط", "ز", "ر", "ذ", "د", "پ", "و", "چ"),
                theme = theme,
                fontSizeScale = fontSizeScale
            )

            // ردیف پایین
            PreviewRow(
                labels = listOf("۱۲۳", "EN", "😀", "نیم‌فاصله", "فاصله", "،", "↵"),
                theme = theme,
                fontSizeScale = fontSizeScale * 0.85f
            )
        }
    }
}

@Composable
private fun PreviewRow(
    labels: List<String>,
    theme: KeyboardTheme,
    fontSizeScale: Float,
    weight: Float = 1f
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(weight),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        labels.forEach { label ->
            PreviewKey(
                label = label,
                theme = theme,
                fontSizeScale = fontSizeScale
            )
        }
    }
}

@Composable
private fun RowScope.PreviewKey(
    label: String,
    theme: KeyboardTheme,
    fontSizeScale: Float
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(theme.keyBackgroundColor)
            .border(
                width = 0.5.dp,
                color = theme.keyBorderColor,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = theme.keyTextColor,
            fontSize = (13 * fontSizeScale).sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}