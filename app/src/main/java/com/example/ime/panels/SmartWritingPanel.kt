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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.PersianAutoFixEngine
import com.example.domain.smart.TextToolsEngine
import com.example.themes.KeyboardTheme

@Composable
fun SmartWritingPanel(
    theme: KeyboardTheme,
    currentText: String,
    onReplaceText: (String) -> Unit,
    onClose: () -> Unit
) {
    var fixedText by remember(currentText) {
        mutableStateOf(if (currentText.isNotBlank()) PersianAutoFixEngine.fixText(currentText) else "")
    }
    val repeatedWords = remember(currentText) {
        if (currentText.isNotBlank()) PersianAutoFixEngine.detectRepeatedWords(currentText) else emptyList()
    }
    val stats = remember(currentText) {
        TextToolsEngine.getStats(currentText)
    }

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
                    imageVector = Icons.Default.Spellcheck,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "✍️ ویرایش و اصلاح هوشمند فارسی",
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        if (currentText.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "متنی برای اصلاح در فیلد تایپ یافت نشد.\nابتدا متنی بنویسید یا انتخاب کنید.",
                    color = theme.keySubTextColor,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Fixed text card
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "متن اصلاح‌شده (املایی، نیم‌فاصله و علائم):",
                                    fontSize = 11.sp,
                                    color = theme.accentColor,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${stats.wordCount} کلمه | ${stats.charCount} حرف",
                                    fontSize = 10.sp,
                                    color = theme.keySubTextColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fixedText,
                                color = theme.keyTextColor,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { onReplaceText(fixedText) },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = theme.accentTextColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "جایگزینی در متن",
                                    color = theme.accentTextColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Repeated words alert
                if (repeatedWords.isNotEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFB800).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFFFB800).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "کلمات تکراری پشت سر هم یافت شد: ${repeatedWords.joinToString("، ")}",
                                    color = Color(0xFFFFB800),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
