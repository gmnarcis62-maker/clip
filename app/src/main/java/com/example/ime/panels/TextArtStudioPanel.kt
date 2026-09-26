package com.example.ime.panels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.ExpressionCenterData
import com.example.domain.smart.SocialMode
import com.example.domain.smart.TextArtConfig
import com.example.domain.smart.TextArtEngine
import com.example.domain.smart.TextArtStyle
import com.example.ime.TextArtPickerActivity
import com.example.themes.KeyboardTheme

enum class TextArtViewTab {
    PREVIEW_TEXT,
    ORIGINAL_IMAGE,
    SETTINGS
}

@Composable
fun TextArtStudioPanel(
    theme: KeyboardTheme,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(TextArtViewTab.PREVIEW_TEXT) }
    var selectedPresetName by remember { mutableStateOf("heart") }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var textArtResult by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // Config states
    var selectedStyle by remember { mutableStateOf(TextArtStyle.REALISTIC_ASCII) }
    var selectedSocialMode by remember { mutableStateOf(SocialMode.UNIVERSAL) }
    var contrastValue by remember { mutableFloatStateOf(1.0f) }
    var subjectOnlyEnabled by remember { mutableStateOf(false) }
    var aspectCorrectionEnabled by remember { mutableStateOf(true) }

    // Initialize with heart preset
    LaunchedEffect(selectedPresetName) {
        if (selectedPresetName != "gallery_custom" || currentBitmap == null) {
            currentBitmap = TextArtEngine.createPresetBitmap(selectedPresetName)
        }
    }

    // Recalculate text art whenever bitmap or configs change
    LaunchedEffect(
        currentBitmap,
        selectedStyle,
        selectedSocialMode,
        contrastValue,
        subjectOnlyEnabled,
        aspectCorrectionEnabled
    ) {
        val bmp = currentBitmap ?: return@LaunchedEffect
        isProcessing = true
        val config = TextArtConfig(
            style = selectedStyle,
            socialMode = selectedSocialMode,
            contrast = contrastValue,
            aspectCorrection = aspectCorrectionEnabled,
            subjectOnly = subjectOnlyEnabled
        )
        textArtResult = TextArtEngine.processImageToTextArt(bmp, config)
        isProcessing = false
    }

    val presets = listOf(
        "heart" to "❤️ قلب",
        "star" to "⭐ ستاره",
        "cat" to "🐱 گربه",
        "flower" to "🌻 گل",
        "car" to "🚗 ماشین",
        "portrait" to "👤 پرتره"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(265.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeTab == TextArtViewTab.PREVIEW_TEXT) theme.accentColor else theme.keyBackgroundColor,
                    modifier = Modifier.clickable { activeTab = TextArtViewTab.PREVIEW_TEXT }
                ) {
                    Text(
                        text = "📄 هنر متنی TextArt",
                        color = if (activeTab == TextArtViewTab.PREVIEW_TEXT) theme.accentTextColor else theme.keySubTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeTab == TextArtViewTab.ORIGINAL_IMAGE) theme.accentColor else theme.keyBackgroundColor,
                    modifier = Modifier.clickable { activeTab = TextArtViewTab.ORIGINAL_IMAGE }
                ) {
                    Text(
                        text = "🖼️ تصویر اصلی",
                        color = if (activeTab == TextArtViewTab.ORIGINAL_IMAGE) theme.accentTextColor else theme.keySubTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeTab == TextArtViewTab.SETTINGS) theme.accentColor else theme.keyBackgroundColor,
                    modifier = Modifier.clickable { activeTab = TextArtViewTab.SETTINGS }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            tint = if (activeTab == TextArtViewTab.SETTINGS) theme.accentTextColor else theme.keySubTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "تنظیمات",
                            color = if (activeTab == TextArtViewTab.SETTINGS) theme.accentTextColor else theme.keySubTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        // Image / Preset Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Gallery button now opens a dedicated Activity instead of using
            // rememberLauncherForActivityResult (which crashes in an IME context)
            Button(
                onClick = {
                    try {
                        val intent = Intent(context, TextArtPickerActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "خطا در باز کردن استودیو عکس",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                modifier = Modifier.height(28.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    tint = theme.keyTextColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("گالری", color = theme.keyTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(4.dp))

            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(presets) { (key, label) ->
                    val isSel = selectedPresetName == key
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) theme.accentColor.copy(alpha = 0.25f) else theme.keyBackgroundColor,
                        border = BorderStroke(
                            0.5.dp,
                            if (isSel) theme.accentColor else theme.keyTopHighlightColor
                        ),
                        modifier = Modifier.clickable { selectedPresetName = key }
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) theme.accentColor else theme.keyTextColor,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Main Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(theme.keyBackgroundColor, RoundedCornerShape(8.dp))
                .border(0.5.dp, theme.keyTopHighlightColor, RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            when (activeTab) {
                TextArtViewTab.PREVIEW_TEXT -> {
                    if (isProcessing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = theme.accentColor,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        val vScroll = rememberScrollState()
                        val hScroll = rememberScrollState()
                        Text(
                            text = textArtResult,
                            color = theme.keyTextColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = if (selectedStyle.name.startsWith("EMOJI")) 7.sp else 6.5.sp,
                            lineHeight = if (selectedStyle.name.startsWith("EMOJI")) 8.sp else 6.5.sp,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(vScroll)
                                .horizontalScroll(hScroll)
                        )
                    }
                }

                TextArtViewTab.ORIGINAL_IMAGE -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        currentBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Original image",
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(theme.surfaceColor, RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }

                TextArtViewTab.SETTINGS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "سبک تبدیل (Style):",
                            color = theme.keySubTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(TextArtStyle.values().toList()) { style ->
                                val isSelected = style == selectedStyle
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                                    modifier = Modifier.clickable { selectedStyle = style }
                                ) {
                                    Text(
                                        text = style.displayName,
                                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            "بهینه‌سازی شبکه اجتماعی:",
                            color = theme.keySubTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(SocialMode.values().toList()) { mode ->
                                val isSelected = mode == selectedSocialMode
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                                    modifier = Modifier.clickable { selectedSocialMode = mode }
                                ) {
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (subjectOnlyEnabled) theme.accentColor else theme.surfaceColor,
                                modifier = Modifier.clickable { subjectOnlyEnabled = !subjectOnlyEnabled }
                            ) {
                                Text(
                                    text = if (subjectOnlyEnabled) "✓ فقط سوژه اصلی" else "فقط سوژه اصلی",
                                    color = if (subjectOnlyEnabled) theme.accentTextColor else theme.keyTextColor,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "کنتراست: ${String.format("%.1f", contrastValue)}",
                                    color = theme.keySubTextColor,
                                    fontSize = 10.sp
                                )
                                Slider(
                                    value = contrastValue,
                                    onValueChange = { contrastValue = it },
                                    valueRange = 0.6f..1.8f,
                                    modifier = Modifier.width(100.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = theme.accentColor,
                                        activeTrackColor = theme.accentColor
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    cm?.setPrimaryClip(ClipData.newPlainText("text_art", textArtResult))
                    Toast.makeText(context, "هنر متنی در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = theme.keyTextColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("کپی", color = theme.keyTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    onInsertText(textArtResult)
                    Toast.makeText(context, "درج در متن انجام شد", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .weight(1.4f)
                    .height(30.dp),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = theme.accentTextColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("درج در متن", color = theme.accentTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, textArtResult)
                        type = "text/plain"
                    }
                    val chooser = Intent.createChooser(sendIntent, "ارسال هنر متنی").apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(chooser)
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(30.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "اشتراک‌گذاری",
                    tint = theme.keyTextColor,
                    modifier = Modifier.size(13.dp)
                )
            }

            Button(
                onClick = {
                    ExpressionCenterData.toggleFavorite(context, textArtResult)
                    Toast.makeText(context, "به برگزیده‌ها اضافه شد", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(30.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "برگزیده",
                    tint = theme.accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}