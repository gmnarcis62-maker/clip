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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ClipbordApp
import com.example.ai.domain.AiOperation
import com.example.ai.domain.AiResult
import com.example.domain.smart.QuickRepliesAndSmartTone
import com.example.themes.KeyboardTheme
import kotlinx.coroutines.launch

@Composable
fun SmartTonePanel(
    theme: KeyboardTheme,
    selectedText: String,
    onReplaceText: (String) -> Unit,
    onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val aiRepo = ClipbordApp.instance.aiRepository

    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var selectedTone by remember { mutableStateOf<QuickRepliesAndSmartTone.ToneType?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun transformTone(tone: QuickRepliesAndSmartTone.ToneType) {
        if (selectedText.isBlank()) return
        selectedTone = tone
        isLoading = true
        errorMsg = null
        resultText = null

        scope.launch {
            val res = aiRepo.executeOperation(
                operation = AiOperation.CUSTOM_PROMPT,
                inputText = selectedText,
                customPrompt = "${tone.promptInstruction} فقط متن بازنویسی شده را بدون هیچ توضیح اضافه بنویس."
            )
            isLoading = false
            when (res) {
                is AiResult.Success -> resultText = res.outputText
                is AiResult.Error -> errorMsg = res.messagePersian
                is AiResult.LimitReached -> errorMsg = "سقف روزانه درخواست هوش مصنوعی پر شده است."
            }
        }
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
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🎭 تغییر لحن متن با هوش مصنوعی مرسانا",
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        if (selectedText.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "متنی برای تغییر لحن در فیلد تایپ پیدا نشد.\nابتدا متنی بنویسید یا انتخاب کنید.",
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
                // Tone buttons grid
                item {
                    val tones = QuickRepliesAndSmartTone.ToneType.values()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(tones) { tone ->
                            val isSelected = tone == selectedTone
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) theme.accentColor else theme.keyBackgroundColor,
                                border = BorderStroke(0.5.dp, if (isSelected) theme.accentColor else theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .clickable { transformTone(tone) }
                                    .height(38.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(tone.emoji, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tone.titlePersian,
                                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Loading
                if (isLoading) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.keyBackgroundColor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = theme.accentColor,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "در حال بازنویسی با لحن ${selectedTone?.titlePersian ?: ""}...",
                                    color = theme.keyTextColor,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Error
                if (errorMsg != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMsg ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Result
                if (resultText != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.keyBackgroundColor,
                            border = BorderStroke(1.dp, theme.accentColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = resultText ?: "",
                                    color = theme.keyTextColor,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onReplaceText(resultText ?: "") },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                ) {
                                    Icon(Icons.Default.Check, null, tint = theme.accentTextColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("جایگزینی در متن", color = theme.accentTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
