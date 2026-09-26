package com.example.ime.panels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.ExpressionCenterData
import com.example.domain.smart.SocialCaptionAndBioData
import com.example.domain.smart.TextDecoratorEngine
import com.example.themes.KeyboardTheme

enum class ExpressionMainTab(val title: String) {
    EMOJI("ایموجی"),
    KAOMOJI("شکلک ژاپنی"),
    DECORATOR("زیباساز متن"),
    SEPARATORS("خطوط تزیینی"),
    CAPTIONS("کپشن و بیو"),
    FAVORITES("برگزیده‌ها ⭐")
}

@Composable
fun EmojiAndExpressionCenterPanel(
    theme: KeyboardTheme,
    initialText: String = "",
    onInsertText: (String) -> Unit,
    onBackspace: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var selectedMainTab by remember { mutableStateOf(ExpressionMainTab.EMOJI) }
    var selectedEmojiCatIndex by remember { mutableIntStateOf(0) }
    var selectedKaomojiCatIndex by remember { mutableIntStateOf(0) }
    var selectedSeparatorCatIndex by remember { mutableIntStateOf(0) }
    var selectedCaptionCatIndex by remember { mutableIntStateOf(0) }

    var decoratorInput by remember { mutableStateOf(if (initialText.isBlank()) "زندگی زیباست" else initialText) }
    var favoriteList by remember { mutableStateOf(ExpressionCenterData.getFavorites(context)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(265.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        // Main Tabs Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(ExpressionMainTab.values()) { tab ->
                    val isSel = tab == selectedMainTab
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) theme.accentColor else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSel) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier.clickable {
                            selectedMainTab = tab
                            if (tab == ExpressionMainTab.FAVORITES) {
                                favoriteList = ExpressionCenterData.getFavorites(context)
                            }
                        }
                    ) {
                        Text(
                            text = tab.title,
                            color = if (isSel) theme.accentTextColor else theme.keyTextColor,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onBackspace, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "حذف",
                        tint = theme.specialKeyTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Subcategory bar for EMOJI
        if (selectedMainTab == ExpressionMainTab.EMOJI) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(ExpressionCenterData.EMOJI_CATEGORIES.size) { idx ->
                    val cat = ExpressionCenterData.EMOJI_CATEGORIES[idx]
                    val isSel = idx == selectedEmojiCatIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) theme.accentColor.copy(alpha = 0.25f) else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSel) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier.clickable { selectedEmojiCatIndex = idx }
                    ) {
                        Text(
                            text = "${cat.icon} ${cat.title}",
                            color = if (isSel) theme.accentColor else theme.keySubTextColor,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Subcategory bar for KAOMOJI
        if (selectedMainTab == ExpressionMainTab.KAOMOJI) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(ExpressionCenterData.KAOMOJI_CATEGORIES.size) { idx ->
                    val cat = ExpressionCenterData.KAOMOJI_CATEGORIES[idx]
                    val isSel = idx == selectedKaomojiCatIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) theme.accentColor.copy(alpha = 0.25f) else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSel) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier.clickable { selectedKaomojiCatIndex = idx }
                    ) {
                        Text(
                            text = "${cat.icon} ${cat.title}",
                            color = if (isSel) theme.accentColor else theme.keySubTextColor,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Subcategory bar for SEPARATORS
        if (selectedMainTab == ExpressionMainTab.SEPARATORS) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(SocialCaptionAndBioData.SEPARATORS.size) { idx ->
                    val cat = SocialCaptionAndBioData.SEPARATORS[idx]
                    val isSel = idx == selectedSeparatorCatIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) theme.accentColor.copy(alpha = 0.25f) else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSel) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier.clickable { selectedSeparatorCatIndex = idx }
                    ) {
                        Text(
                            text = "${cat.icon} ${cat.name}",
                            color = if (isSel) theme.accentColor else theme.keySubTextColor,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Subcategory bar for CAPTIONS
        if (selectedMainTab == ExpressionMainTab.CAPTIONS) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(SocialCaptionAndBioData.CAPTION_CATEGORIES.size) { idx ->
                    val cat = SocialCaptionAndBioData.CAPTION_CATEGORIES[idx]
                    val isSel = idx == selectedCaptionCatIndex
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSel) theme.accentColor.copy(alpha = 0.25f) else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSel) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier.clickable { selectedCaptionCatIndex = idx }
                    ) {
                        Text(
                            text = cat.second,
                            color = if (isSel) theme.accentColor else theme.keySubTextColor,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(theme.keyBackgroundColor, RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            when (selectedMainTab) {
                ExpressionMainTab.EMOJI -> {
                    val cat = ExpressionCenterData.EMOJI_CATEGORIES[selectedEmojiCatIndex]
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 36.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(cat.items) { emoji ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.surfaceColor,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onInsertText(emoji) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                }

                ExpressionMainTab.KAOMOJI -> {
                    val cat = ExpressionCenterData.KAOMOJI_CATEGORIES[selectedKaomojiCatIndex]
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(cat.items) { km ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.surfaceColor,
                                border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .clickable { onInsertText(km) }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
                                    Text(text = km, color = theme.keyTextColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                ExpressionMainTab.DECORATOR -> {
                    val variations = remember(decoratorInput) {
                        TextDecoratorEngine.decorate(decoratorInput)
                    }
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = decoratorInput,
                            onValueChange = { decoratorInput = it },
                            placeholder = { Text("متن دلخواه را اینجا بنویسید...", fontSize = 11.sp, color = theme.keySubTextColor) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = theme.keyTextColor,
                                unfocusedTextColor = theme.keyTextColor,
                                focusedBorderColor = theme.accentColor,
                                unfocusedBorderColor = theme.keyTopHighlightColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(variations) { item ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = theme.surfaceColor,
                                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onInsertText(item.preview) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = item.title, color = theme.accentColor, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            Text(text = item.preview, color = theme.keyTextColor, fontSize = 12.sp)
                                        }
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                    cm?.setPrimaryClip(ClipData.newPlainText("decorated", item.preview))
                                                    Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = theme.keySubTextColor, modifier = Modifier.size(13.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ExpressionMainTab.SEPARATORS -> {
                    val cat = SocialCaptionAndBioData.SEPARATORS[selectedSeparatorCatIndex]
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(cat.items) { sep ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.surfaceColor,
                                border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onInsertText(sep) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = sep, color = theme.keyTextColor, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            cm?.setPrimaryClip(ClipData.newPlainText("separator", sep))
                                            Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = theme.keySubTextColor, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                ExpressionMainTab.CAPTIONS -> {
                    val catKey = SocialCaptionAndBioData.CAPTION_CATEGORIES[selectedCaptionCatIndex].first
                    val captions = SocialCaptionAndBioData.CAPTIONS.filter { it.categoryId == catKey }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(captions) { item ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = theme.surfaceColor,
                                border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onInsertText(item.content) }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = item.title, color = theme.accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = item.content, color = theme.keyTextColor, fontSize = 11.5.sp, maxLines = 3)
                                }
                            }
                        }
                    }
                }

                ExpressionMainTab.FAVORITES -> {
                    if (favoriteList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "هنوز موردی در برگزیده‌ها ذخیره نشده است.\nاز بخش‌های دیگر می‌توانید با ستاره موارد را ذخیره کنید.",
                                color = theme.keySubTextColor,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(favoriteList) { item ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = theme.surfaceColor,
                                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onInsertText(item) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = item, color = theme.keyTextColor, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 2)
                                        IconButton(
                                            onClick = {
                                                ExpressionCenterData.toggleFavorite(context, item)
                                                favoriteList = ExpressionCenterData.getFavorites(context)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = theme.accentColor, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
