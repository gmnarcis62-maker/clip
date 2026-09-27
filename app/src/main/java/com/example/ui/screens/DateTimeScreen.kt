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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ClipbordApp
import com.example.ime.panels.DateTimePanel
import com.example.themes.ThemeManager
import com.example.ui.components.ClipbordTopBar

@Composable
fun DateTimeScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val preferences = ClipbordApp.instance.preferences
    val themeId by preferences.themeId.collectAsState(initial = "turquoise")
    val theme = ThemeManager.getThemeById(themeId)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "تاریخ و ساعت",
                    subtitle = "تاریخ شمسی و میلادی",
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
                    .testTag("date_time_screen")
            ) {
                DateTimePanel(
                    theme = theme,
                    modifier = Modifier.fillMaxSize(),
                    onInsertText = { dt ->
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("date", dt))
                        Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                    },
                    onClose = onBackClick
                )
            }
        }
    }
}