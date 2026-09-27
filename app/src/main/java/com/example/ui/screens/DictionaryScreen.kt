package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ClipbordApp
import com.example.dictionary.ui.DictionaryPanel
import com.example.themes.ThemeManager
import com.example.ui.components.ClipbordTopBar

@Composable
fun DictionaryScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val preferences = ClipbordApp.instance.preferences
    val themeId by preferences.themeId.collectAsState(initial = "turquoise")
    val theme = ThemeManager.getThemeById(themeId)

    var searchQuery by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "دیکشنری هوشمند",
                    subtitle = "معنی، مترادف، ترجمه و مثال",
                    onBackClick = onBackClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .testTag("dictionary_screen")
            ) {
                DictionaryPanel(
                    modifier = Modifier.fillMaxSize(),
                    theme = theme,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onInsertText = { txt ->
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("dictionary", txt))
                        Toast.makeText(context, "در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                    },
                    onClose = onBackClick
                )
            }
        }
    }
}