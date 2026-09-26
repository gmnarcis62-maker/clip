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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionAnswer
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
import com.example.domain.smart.QuickRepliesAndSmartTone
import com.example.themes.KeyboardTheme

@Composable
fun QuickRepliesPanel(
    theme: KeyboardTheme,
    onSelectReply: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = QuickRepliesAndSmartTone.QUICK_REPLY_CATEGORIES

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "💬 پاسخ‌های سریع و هوشمند روزمره",
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Category tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(categories) { index, cat ->
                val isSelected = index == selectedCategoryIndex
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) theme.accentColor else theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, if (isSelected) theme.accentColor else theme.keyTopHighlightColor),
                    modifier = Modifier.clickable { selectedCategoryIndex = index }
                ) {
                    Text(
                        text = cat.title,
                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Replies list
        val currentReplies = categories[selectedCategoryIndex].replies
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(currentReplies) { reply ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectReply(reply) }
                ) {
                    Text(
                        text = reply,
                        color = theme.keyTextColor,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
