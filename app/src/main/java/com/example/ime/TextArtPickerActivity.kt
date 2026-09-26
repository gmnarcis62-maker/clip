package com.example.ime

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ClipbordApp
import com.example.domain.smart.TextArtConfig
import com.example.domain.smart.TextArtEngine
import com.example.domain.smart.TextArtStyle
import com.example.themes.KeyboardTheme
import com.example.themes.ThemeManager
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextArtPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val preferences = ClipbordApp.instance.preferences
            val themeId by preferences.themeId.collectAsState(initial = "turquoise")
            val darkMode by preferences.darkMode.collectAsState(initial = "SYSTEM")
            val theme = ThemeManager.getThemeById(themeId)

            MyApplicationTheme(darkModeOption = darkMode) {
                TextArtPickerScreen(
                    theme = theme,
                    onDone = { finish() }
                )
            }
        }
    }
}

@Composable
fun TextArtPickerScreen(
    theme: KeyboardTheme,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var textArt by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedStyle by remember { mutableStateOf(TextArtStyle.REALISTIC_ASCII) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            isProcessing = true
            val loaded = withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        TextArtEngine.decodeSafeBitmapFromStream(stream, 280)
                    }
                } catch (_: Exception) {
                    null
                }
            }
            bitmap = loaded
            if (loaded == null) {
                Toast.makeText(context, "خطا در بارگذاری تصویر", Toast.LENGTH_SHORT).show()
            }
            isProcessing = false
        }
    }

    // Auto-launch picker on first open
    LaunchedEffect(Unit) {
        launcher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // Recalculate text art when bitmap or style changes
    LaunchedEffect(bitmap, selectedStyle) {
        val bmp = bitmap ?: return@LaunchedEffect
        isProcessing = true
        textArt = withContext(Dispatchers.Default) {
            TextArtEngine.processImageToTextArt(
                bmp,
                TextArtConfig(style = selectedStyle)
            )
        }
        isProcessing = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "استودیو هنر متنی",
                    color = theme.keyTextColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "عکس را به متن هنری تبدیل کن",
                    color = theme.keySubTextColor,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = onDone) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keyTextColor)
            }
        }

        // Preview
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(theme.surfaceColor, RoundedCornerShape(12.dp))
                .border(0.5.dp, theme.keyTopHighlightColor, RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isProcessing -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = theme.accentColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "در حال پردازش تصویر...",
                            color = theme.keySubTextColor,
                            fontSize = 12.sp
                        )
                    }
                }
                textArt.isNotEmpty() -> {
                    val vScroll = rememberScrollState()
                    val hScroll = rememberScrollState()
                    Text(
                        text = textArt,
                        color = theme.keyTextColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 7.sp,
                        lineHeight = 7.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(vScroll)
                            .horizontalScroll(hScroll)
                    )
                }
                else -> {
                    Text(
                        text = "هنوز عکسی انتخاب نشده",
                        color = theme.keySubTextColor,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Style selector
        Text(
            "سبک تبدیل:",
            color = theme.keySubTextColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(TextArtStyle.values().toList()) { style ->
                val isSelected = style == selectedStyle
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) theme.accentColor else theme.surfaceColor,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isSelected) theme.accentColor else theme.keyTopHighlightColor
                    ),
                    modifier = Modifier.clickable { selectedStyle = style }
                ) {
                    Text(
                        text = style.displayName,
                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("انتخاب عکس", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (textArt.isNotEmpty()) {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("text_art", textArt))
                        Toast.makeText(context, "در کلیپ‌بورد کپی شد", Toast.LENGTH_LONG).show()
                        onDone()
                    }
                },
                enabled = textArt.isNotEmpty() && !isProcessing,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = theme.accentTextColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "کپی و بستن",
                    color = theme.accentTextColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}