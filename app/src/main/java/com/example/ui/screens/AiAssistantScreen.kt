package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.ai.domain.AiOperation
import com.example.ai.domain.AiResult
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.components.SectionTitle
import com.example.ui.theme.PrimaryCyan
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = ClipbordApp.instance
    val aiRepository = app.aiRepository
    val preferences = app.preferences
    val scope = rememberCoroutineScope()

    val aiEnabled by preferences.aiEnabled.collectAsState(initial = true)
    val aiButtonVisible by preferences.aiButtonVisible.collectAsState(initial = true)

    var inputText by remember { mutableStateOf("") }
    var customPromptText by remember { mutableStateOf("") }
    var selectedOp by remember { mutableStateOf(AiOperation.GRAMMAR_CORRECTION) }

    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isTestingDirectConnection by remember { mutableStateOf(false) }
    var directTestResult by remember { mutableStateOf<String?>(null) }

    fun runAi() {
        if (inputText.isBlank() && selectedOp != AiOperation.CUSTOM_PROMPT) {
            Toast.makeText(context, "لطفاً ابتدا متنی را وارد کنید.", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        errorMessage = null
        resultText = null

        scope.launch {
            val res = aiRepository.executeOperation(
                operation = selectedOp,
                inputText = inputText,
                customPrompt = customPromptText
            )
            isLoading = false
            when (res) {
                is AiResult.Success -> {
                    resultText = res.outputText
                }
                is AiResult.Error -> {
                    errorMessage = res.messagePersian
                }
                is AiResult.LimitReached -> {
                    errorMessage = "خطای موقت در سرویس هوش مصنوعی. لطفاً دوباره تلاش کنید."
                }
            }
        }
    }

    fun testAtriaDirect() {
        isTestingDirectConnection = true
        directTestResult = null
        scope.launch {
            val res = aiRepository.testDirectConnection("به فارسی بگو سلام")
            isTestingDirectConnection = false
            when (res) {
                is AiResult.Success -> {
                    directTestResult = "✓ اتصال با موفقیت برقرار شد:\n\n${res.outputText}"
                }
                is AiResult.Error -> {
                    directTestResult = "✗ خطا: ${res.messagePersian}"
                }
                is AiResult.LimitReached -> {
                    directTestResult = "سرویس موقتاً در دسترس نیست."
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "دستیار هوش مصنوعی مرسانا",
                    subtitle = "نگارش، ویرایش و بازنویسی هوشمند متون با هوش مصنوعی",
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
                    .testTag("ai_assistant_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card — همه امکانات فعال
                item {
                    PersianCard(borderColor = PrimaryCyan) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "دستیار هوشمند شما فعال است",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PrimaryCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "دسترسی نامحدود به تمام قابلیت‌های هوش مصنوعی ✨",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Direct Test Card
                item {
                    PersianCard(borderColor = Color(0xFFFFB800).copy(alpha = 0.4f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تست اتصال به هوش مصنوعی مرسانا",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFFFB800)
                                )
                                Text(
                                    text = "بررسی زنده پایداری و پاسخ‌دهی موتور هوش مصنوعی",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { testAtriaDirect() },
                                enabled = !isTestingDirectConnection,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("btn_test_direct_ai")
                            ) {
                                if (isTestingDirectConnection) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                } else {
                                    Text("تست اتصال", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (directTestResult != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = directTestResult ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Operation selector
                item {
                    SectionTitle(title = "عملیات هوش مصنوعی", icon = Icons.Default.AutoAwesome)
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AiOperation.values()) { op ->
                            val isSelected = op == selectedOp
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.clickable { selectedOp = op }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = op.iconEmoji, fontSize = 14.sp)
                                    Text(
                                        text = op.titlePersian,
                                        color = if (isSelected) Color(0xFF07211E) else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Input
                item {
                    PersianCard {
                        Text(
                            text = "متن ورودی شما:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("متن خود را اینجا تایپ کنید یا بچسبانید...", fontSize = 13.sp) },
                            minLines = 3,
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth().testTag("ai_input_text"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        if (selectedOp == AiOperation.CUSTOM_PROMPT) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "دستور دلخواه شما:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customPromptText,
                                onValueChange = { customPromptText = it },
                                placeholder = { Text("مثال: این متن را خلاصه کن / به لحن ادبی بنویس...", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFB800),
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { runAi() },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_run_ai"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF07211E))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("در حال پردازش با هوش مصنوعی...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("اجرای ${selectedOp.titlePersian}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                if (resultText != null) {
                    item {
                        PersianCard(borderColor = PrimaryCyan) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✨ نتیجه پردازش هوش مصنوعی:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = PrimaryCyan
                                )

                                IconButton(
                                    onClick = {
                                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cm.setPrimaryClip(ClipData.newPlainText("AI Result", resultText ?: ""))
                                        Toast.makeText(context, "نتیجه در کلیپ‌بورد کپی شد.", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, "کپی", tint = PrimaryCyan)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = resultText ?: "",
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Settings
                item {
                    SectionTitle(title = "تنظیمات هوش مصنوعی", icon = Icons.Default.Settings)

                    PersianCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("فعال بودن هوش مصنوعی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("فعال‌سازی سرویس هوش مصنوعی مرسانا در کیبورد و برنامه", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = aiEnabled,
                                onCheckedChange = { scope.launch { preferences.setAiEnabled(it) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF07211E), checkedTrackColor = PrimaryCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("دکمه هوش مصنوعی در نوار بالای کیبورد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("دسترسی سریع با دکمه ✨ در کیبورد", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = aiButtonVisible,
                                onCheckedChange = { scope.launch { preferences.setAiButtonVisible(it) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF07211E), checkedTrackColor = PrimaryCyan)
                            )
                        }
                    }
                }
            }
        }
    }
}