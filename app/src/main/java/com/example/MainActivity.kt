package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialRoute = intent?.getStringExtra(EXTRA_NAVIGATE_TO)

        val preferences = (application as? ClipbordApp)?.preferences ?: ClipbordApp.instance.preferences

        setContent {
            val darkMode by preferences.darkMode.collectAsState(initial = "SYSTEM")

            MyApplicationTheme(darkModeOption = darkMode) {
                AppNavigation(initialRoute = initialRoute)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    // ✅ onActivityResult حذف شد — دیگه نیازی به پرداخت نیست

    companion object {
        const val EXTRA_NAVIGATE_TO = "navigate_to"
        const val ROUTE_SETTINGS = "settings"
        const val ROUTE_THEMES = "themes"
        const val ROUTE_AI = "ai"
    }
}