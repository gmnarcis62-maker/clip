package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

    // AI States
    val aiEnabled by preferences.aiEnabled.collectAsState(initial = true)
    val aiButtonVisible by preferences.aiButtonVisible.collectAsState(initial = true)
    val aiTone by preferences.aiDefaultTone.collectAsState(initial = "NEUTRAL")
    val aiUsageCount by preferences.aiDailyUsageCount.collectAsState(initial = 0)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "تنظیمات کیبورد",
                    subtitle = "شخصی‌سازی ظاهر، تایپ، صدا، لرزش و هوش مصنوعی",
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
                            SectionTitle(title = "هوش مصنوعی و دستیار متن", icon = Icons.Default.AutoAwesome)
                            Button(
                                onClick = onNavigateToAi,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("آزمایش AI", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Usage status
                        Text(
                            text = if (isVip) "وضعیت: حساب حرفه‌ای VIP (نامحدود) ✓" else "استفاده امروز: ${PersianDateUtils.toPersianDigits(aiUsageCount)} از ${PersianDateUtils.toPersianDigits(AiConfig.FREE_DAILY_REQUEST_LIMIT)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVip) Color(0xFF10B981) else Color(0xFFFFB800)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "فعال بودن قابلیت‌های هوش مصنوعی",
                            subtitle = "دسترسی به اصلاح متن، بازنویسی، ترجمه و پاسخ هوشمند",
                            checked = aiEnabled,
                            onCheckedChange = { scope.launch { preferences.setAiEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "دکمه ✨ هوش مصنوعی در کیبورد",
                            subtitle = "نمایش دکمه دسترسی سریع در نوار بالای کیبورد",
                            checked = aiButtonVisible,
                            onCheckedChange = { scope.launch { preferences.setAiButtonVisible(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("لحن پیش‌فرض هوش مصنوعی:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("NEUTRAL" to "خنثی", "FRIENDLY" to "خودمانی", "FORMAL" to "رسمی").forEach { (tone, label) ->
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

                // 1. Appearance & Height
                item {
                    PersianCard {
                        SectionTitle(title = "ظاهر، ابعاد و پوسته", icon = Icons.Default.Palette)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Theme Shortcut
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("پوسته کیبورد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("انتخاب تم‌های ایرانی، مدرن و امولد", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                            Button(
                                onClick = onNavigateToThemes,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                            ) {
                                Text("تغییر تم", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dark Mode selector
                        Text("حالت تم برنامه:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("SYSTEM" to "خودکار", "LIGHT" to "روشن", "DARK" to "تاریک", "AMOLED" to "امولد").forEach { (mode, label) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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

                        // Height Slider
                        Text("ارتفاع کیبورد: ${PersianDateUtils.toPersianDigits((heightRatio * 100).toInt())}%", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Slider(
                            value = heightRatio,
                            onValueChange = { scope.launch { preferences.setKeyboardHeight(it) } },
                            valueRange = 0.85f..1.25f,
                            steps = 4,
                            colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan)
                        )
                    }
                }

                // 2. Typing & Predictions
                item {
                    PersianCard {
                        SectionTitle(title = "تایپ، اصلاح خودکار و لغت‌نامه", icon = Icons.Default.Psychology)

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "پیشنهاد هوشمند کلمات",
                            subtitle = "نمایش کلمات پیشنهادی در نوار بالای کیبورد",
                            checked = suggestionsEnabled,
                            onCheckedChange = { scope.launch { preferences.setSuggestionsEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "اصلاح خودکار غلط‌های املایی فارسی",
                            subtitle = "اصلاح غلط‌های رایج مانند میخام → می‌خوام",
                            checked = autoCorrectionEnabled,
                            onCheckedChange = { scope.launch { preferences.setAutoCorrectionEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "استفاده پیش‌فرض از اعداد فارسی",
                            subtitle = "تایپ اعداد به صورت ۰۱۲۳۴۵۶۷۸۹",
                            checked = persianNumbersDefault,
                            onCheckedChange = { scope.launch { preferences.setPersianNumbersEnabled(it) } }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "کلید اختصاصی نیم‌فاصله",
                            subtitle = "دسترسی سریع به نیم‌فاصله در ردیف پایین",
                            checked = halfSpaceEnabled,
                            onCheckedChange = { scope.launch { preferences.setHalfSpaceEnabled(it) } }
                        )
                    }
                }

                // 3. Sound & Vibration
                item {
                    PersianCard {
                        SectionTitle(title = "صدا و بازخورد لمسی (ویبره)", icon = Icons.Default.Vibration)

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "لرزش هنگام لمس کلیدها (Haptic)",
                            subtitle = "ایجاد حس فیزیکی تایپ",
                            checked = vibrationEnabled,
                            onCheckedChange = { scope.launch { preferences.setVibrationEnabled(it) } }
                        )

                        if (vibrationEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("شدت لرزش: ${PersianDateUtils.toPersianDigits(vibrationStrength)} میلی‌ثانیه", fontSize = 12.sp)
                            Slider(
                                value = vibrationStrength.toFloat(),
                                onValueChange = { scope.launch { preferences.setVibrationStrength(it.toInt()) } },
                                valueRange = 10f..60f,
                                colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingToggleRow(
                            title = "صدای کلیک هنگام لمس کلیدها",
                            subtitle = "پخش افکت صوتی استاندارد سیستم",
                            checked = soundEnabled,
                            onCheckedChange = { scope.launch { preferences.setSoundEnabled(it) } }
                        )
                    }
                }

                // 4. Voice Typing
                item {
                    PersianCard {
                        SectionTitle(title = "تایپ صوتی و تشخیص گفتار", icon = Icons.Default.TextFields)

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingToggleRow(
                            title = "کلید میکروفون تایپ صوتی",
                            subtitle = "تبدیل صدای کاربر به متن از طریق سرویس تشخیص گفتار",
                            checked = voiceTypingEnabled,
                            onCheckedChange = { scope.launch { preferences.setVoiceTypingEnabled(it) } }
                        )
                    }
                }

                // Reset Settings Button
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
                                Toast.makeText(context, "تنظیمات به حالت پیش‌فرض بازگشت.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("بازنشانی تمام تنظیمات به پیش‌فرض", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
