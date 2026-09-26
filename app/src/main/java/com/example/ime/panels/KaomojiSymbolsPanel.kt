package com.example.ime.panels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.KaomojiAndSymbolsData
import com.example.themes.KeyboardTheme

@Composable
fun KaomojiSymbolsPanel(
    theme: KeyboardTheme,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    var isKaomojiMode by remember { mutableIntStateOf(0) } // 0: Kaomoji, 1: Symbols
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val kaomojiCats = KaomojiAndSymbolsData.KAOMOJI_CATEGORIES
    val symbolCats = KaomojiAndSymbolsData.SYMBOL_CATEGORIES

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        // Header with Kaomoji vs Symbols toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isKaomojiMode == 0) theme.accentColor else theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, if (isKaomojiMode == 0) theme.accentColor else theme.keyTopHighlightColor),
                    modifier = Modifier.clickable {
                        isKaomojiMode = 0
                        selectedCategoryIndex = 0
                    }
                ) {
                    Text(
                        text = "(｡♥‿♥｡) شکلک‌های متنی (Kaomoji)",
                        color = if (isKaomojiMode == 0) theme.accentTextColor else theme.keyTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isKaomojiMode == 1) theme.accentColor else theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, if (isKaomojiMode == 1) theme.accentColor else theme.keyTopHighlightColor),
                    modifier = Modifier.clickable {
                        isKaomojiMode = 1
                        selectedCategoryIndex = 0
                    }
                ) {
                    Text(
                        text = "★ ﷽ نمادها و نشانه‌ها",
                        color = if (isKaomojiMode == 1) theme.accentTextColor else theme.keyTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Categories tabs
        val currentCategories = if (isKaomojiMode == 0) kaomojiCats.map { it.title } else symbolCats.map { it.title }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(currentCategories) { idx, catTitle ->
                val isSelected = idx == selectedCategoryIndex
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) theme.accentColor else theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, if (isSelected) theme.accentColor else theme.keyTopHighlightColor),
                    modifier = Modifier.clickable { selectedCategoryIndex = idx }
                ) {
                    Text(
                        text = catTitle,
                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Items Grid
        if (isKaomojiMode == 0) {
            val kaomojis = kaomojiCats.getOrNull(selectedCategoryIndex)?.items ?: emptyList()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(kaomojis) { k ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clickable { onInsertText(k) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = k,
                                color = theme.keyTextColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            val symbols = symbolCats.getOrNull(selectedCategoryIndex)?.symbols ?: emptyList()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 42.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(symbols) { sym ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onInsertText(sym) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = sym,
                                color = theme.keyTextColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
