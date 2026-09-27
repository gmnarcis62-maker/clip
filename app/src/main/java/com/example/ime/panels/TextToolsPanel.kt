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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.TextToolsEngine
import com.example.themes.KeyboardTheme

@Composable
fun TextToolsPanel(
    theme: KeyboardTheme,
    currentText: String,
    modifier: Modifier = Modifier.fillMaxWidth().height(250.dp),
    onReplaceText: (String) -> Unit,
    onClose: () -> Unit
) {
    val stats = remember(currentText) {
        TextToolsEngine.getStats(currentText)
    }

    data class ToolAction(
        val title: String,
        val icon: String,
        val action: () -> String
    )

    val tools = listOf(
        ToolAction("حذف فاصله‌های اضافی", "✂️") { TextToolsEngine.removeExtraSpaces(currentText) },
        ToolAction("حذف خطوط خالی", "🧹") { TextToolsEngine.removeEmptyLines(currentText) },
        ToolAction("حذف خطوط تکراری", "♻️") { TextToolsEngine.removeDuplicateLines(currentText) },
        ToolAction("مرتب‌سازی الفبایی خطوط", "🔤") { TextToolsEngine.sortLinesAscending(currentText) },
        ToolAction("برعکس کردن متن (معکوس)", "🔄") { TextToolsEngine.reverseText(currentText) },
        ToolAction("حروف بزرگ انگلیسی (UPPER)", "🔠") { TextToolsEngine.toUpperCase(currentText) },
        ToolAction("حروف کوچک انگلیسی (lower)", "🔡") { TextToolsEngine.toLowerCase(currentText) },
        ToolAction("حرف اول بزرگ (Title Case)", "🔤") { TextToolsEngine.toTitleCase(currentText) }
    )

    Column(
        modifier = modifier
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🛠️ جعبه ابزار ویرایش متن (Text Tools)",
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

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = theme.keyBackgroundColor,
            border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("حروف: ${stats.charCount}", color = theme.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("کلمات: ${stats.wordCount}", color = theme.keyTextColor, fontSize = 11.sp)
                Text("خطوط: ${stats.lineCount}", color = theme.keyTextColor, fontSize = 11.sp)
                Text("پاراگراف: ${stats.paragraphCount}", color = theme.keySubTextColor, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (currentText.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "متنی برای پردازش در فیلد تایپ نیست.\nمتنی را بنویسید یا انتخاب کنید.",
                    color = theme.keySubTextColor,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(tools) { tool ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clickable {
                                val transformed = tool.action()
                                onReplaceText(transformed)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tool.icon, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tool.title,
                                color = theme.keyTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}