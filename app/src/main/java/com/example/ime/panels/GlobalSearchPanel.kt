package com.example.ime.panels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClipboardEntity
import com.example.domain.smart.SmartSnippetsAndDictionary
import com.example.themes.KeyboardTheme

@Composable
fun GlobalSearchPanel(
    theme: KeyboardTheme,
    clipboardItems: List<ClipboardEntity>,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredSnippets = remember(query) {
        SmartSnippetsAndDictionary.searchSnippets(query)
    }

    val filteredClipboard = remember(query, clipboardItems) {
        if (query.isBlank()) clipboardItems.take(5)
        else clipboardItems.filter { it.text.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        // Header Search Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        text = "جستجو در یادداشت‌ها، Snippet و کلیپ‌بورد...",
                        fontSize = 12.sp,
                        color = theme.keySubTextColor
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 13.sp,
                    color = theme.keyTextColor
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentColor,
                    unfocusedBorderColor = theme.keyBackgroundColor,
                    focusedContainerColor = theme.keyBackgroundColor,
                    unfocusedContainerColor = theme.keyBackgroundColor,
                    focusedTextColor = theme.keyTextColor,
                    unfocusedTextColor = theme.keyTextColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Snippets section
            if (filteredSnippets.isNotEmpty()) {
                item {
                    Text(
                        text = "⚡ قالب‌های آماده (Snippets):",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                items(filteredSnippets) { snippet ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInsertText(snippet.content) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = snippet.title,
                                        color = theme.keyTextColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = snippet.shortcut,
                                        color = theme.accentColor,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = snippet.content,
                                    color = theme.keySubTextColor,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Clipboard items section
            if (filteredClipboard.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📋 تاریخچه کلیپ‌بورد:",
                        color = theme.specialKeyTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                items(filteredClipboard) { clip ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInsertText(clip.text) }
                    ) {
                        Text(
                            text = clip.text,
                            color = theme.keyTextColor,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}