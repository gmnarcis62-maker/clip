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

        val preferences = (application as? ClipbordApp)?.preferences ?: ClipbordApp.instance.preferences

        setContent {
            val darkMode by preferences.darkMode.collectAsState(initial = "SYSTEM")

            MyApplicationTheme(darkModeOption = darkMode) {
                AppNavigation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val app = application as? ClipbordApp ?: ClipbordApp.instance
            app.billingManager.handlePurchaseResult(requestCode, resultCode, data)
        } catch (_: Exception) {
            // Ignore errors here so the UI never crashes on purchase result handling
        }
    }
}