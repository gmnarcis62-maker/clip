package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.domain.ime.ImeActivationManager
import com.example.domain.ime.KeyboardActivationState
import com.example.ui.components.ActivationWizardBottomSheet
import com.example.ui.components.HomeMoreToolsBottomSheet
import com.example.ui.components.RadialHomeMenu
import com.example.ui.components.core.CyberTelemetryFooter
import com.example.ui.components.core.CyberTelemetryHeader
import com.example.ui.components.core.SmartNodeItem

@Composable
fun HomeScreen(
    onNavigateToTest: () -> Unit,
    onNavigateToClipboard: () -> Unit,
    onNavigateToThemes: () -> Unit,
    onNavigateToEmoji: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToSuggestions: () -> Unit,
    onNavigateToVoice: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToVip: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToIntro: () -> Unit,
    onNavigateToPrivacy: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var activationState by remember { mutableStateOf(ImeActivationManager.checkActivationState(context)) }
    var showActivationWizard by remember { mutableStateOf(false) }
    var showMoreToolsSheet by remember { mutableStateOf(false) }

    // Re-check IME state on every ON_RESUME (e.g. when returning from Settings or Picker)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                activationState = ImeActivationManager.checkActivationState(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Helper: shows a toast and navigates to the keyboard test screen so the user
    // can use the tool from the keyboard toolbar.
    fun openKeyboardTool(toolName: String) {
        Toast.makeText(
            context,
            "$toolName از نوار ابزار داخل کیبورد مرسانا قابل دسترسی است. حالا کیبورد را باز کنید و آیکن آن را بزنید.",
            Toast.LENGTH_LONG
        ).show()
        onNavigateToTest()
    }

    val smartNodes = remember {
        listOf(
            SmartNodeItem(
                id = "dictionary",
                title = "دیکشنری",
                codeName = "LEXICON",
                icon = Icons.Default.MenuBook,
                neonColor = Color(0xFF00E5FF),
                testTag = "radial_btn_dictionary",
                onClick = onNavigateToTest
            ),
            SmartNodeItem(
                id = "ai",
                title = "هوش مصنوعی",
                codeName = "MERSANA AI",
                icon = Icons.Default.AutoAwesome,
                neonColor = Color(0xFFFFB800),
                testTag = "radial_btn_ai",
                onClick = onNavigateToAi
            ),
            SmartNodeItem(
                id = "themes",
                title = "تم‌ها",
                codeName = "VELVET UI",
                icon = Icons.Default.Palette,
                neonColor = Color(0xFF00E5FF),
                testTag = "radial_btn_themes",
                onClick = onNavigateToThemes
            ),
            SmartNodeItem(
                id = "settings",
                title = "تنظیمات",
                codeName = "ENGINE CFG",
                icon = Icons.Default.Settings,
                neonColor = Color(0xFFFFB800),
                testTag = "radial_btn_settings",
                onClick = onNavigateToSettings
            ),
            SmartNodeItem(
                id = "vip",
                title = "نسخه VIP",
                codeName = "PRO ACCESS",
                icon = Icons.Default.Star,
                neonColor = Color(0xFFFFB800),
                testTag = "radial_btn_vip",
                onClick = onNavigateToVip
            ),
            SmartNodeItem(
                id = "about",
                title = "درباره",
                codeName = "SYSTEM INFO",
                icon = Icons.Default.Info,
                neonColor = Color(0xFF00E5FF),
                testTag = "radial_btn_about",
                onClick = onNavigateToAbout
            ),
            SmartNodeItem(
                id = "clipboard",
                title = "کلیپ‌بورد",
                codeName = "CLIP STACK",
                icon = Icons.Default.ContentPaste,
                neonColor = Color(0xFF00E5FF),
                testTag = "radial_btn_clipboard",
                onClick = onNavigateToClipboard
            ),
            SmartNodeItem(
                id = "more",
                title = "بیشتر...",
                codeName = "EXPAND HUB",
                icon = Icons.Default.Apps,
                neonColor = Color(0xFFFFB800),
                testTag = "radial_btn_more",
                onClick = { showMoreToolsSheet = true }
            )
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF030712),
                            Color(0xFF0B1329),
                            Color(0xFF020617)
                        )
                    )
                )
                .testTag("home_screen_cyber_radial_root")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CyberTelemetryHeader(
                    onVipClick = onNavigateToVip
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    RadialHomeMenu(
                        activationState = activationState,
                        items = smartNodes,
                        onCenterClick = {
                            when (activationState) {
                                KeyboardActivationState.NOT_ENABLED -> {
                                    showActivationWizard = true
                                }
                                KeyboardActivationState.ENABLED_NOT_DEFAULT -> {
                                    val pickerOpened = ImeActivationManager.showInputMethodPicker(context)
                                    if (!pickerOpened) {
                                        showActivationWizard = true
                                    }
                                }
                                KeyboardActivationState.DEFAULT -> {
                                    onNavigateToTest()
                                }
                                KeyboardActivationState.SYSTEM_PROBLEM, KeyboardActivationState.UNKNOWN -> {
                                    showActivationWizard = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                CyberTelemetryFooter(
                    activationState = activationState,
                    onFooterClick = {
                        when (activationState) {
                            KeyboardActivationState.DEFAULT -> onNavigateToTest()
                            else -> showActivationWizard = true
                        }
                    }
                )
            }

            if (showActivationWizard) {
                ActivationWizardBottomSheet(
                    currentState = activationState,
                    onDismissRequest = {
                        showActivationWizard = false
                        activationState = ImeActivationManager.checkActivationState(context)
                    },
                    onNavigateToTest = {
                        showActivationWizard = false
                        onNavigateToTest()
                    },
                    onRefreshState = {
                        activationState = ImeActivationManager.checkActivationState(context)
                    }
                )
            }

            if (showMoreToolsSheet) {
                HomeMoreToolsBottomSheet(
                    onDismissRequest = { showMoreToolsSheet = false },

                    // Dictionary is a keyboard-only panel — route to test + toast
                    onNavigateToDictionary = {
                        showMoreToolsSheet = false
                        openKeyboardTool("دیکشنری هوشمند")
                    },

                    // Emoji has its own screen
                    onNavigateToEmoji = {
                        showMoreToolsSheet = false
                        onNavigateToEmoji()
                    },

                    // Calculator — keyboard-only panel
                    onNavigateToCalculator = {
                        showMoreToolsSheet = false
                        openKeyboardTool("ماشین حساب")
                    },

                    // Unit Converter — keyboard-only panel
                    onNavigateToUnitConverter = {
                        showMoreToolsSheet = false
                        openKeyboardTool("تبدیل واحدها")
                    },

                    // Text Tools — keyboard-only panel
                    onNavigateToTextTools = {
                        showMoreToolsSheet = false
                        openKeyboardTool("جعبه ابزار متن")
                    },

                    // Kaomoji — keyboard-only panel
                    onNavigateToKaomoji = {
                        showMoreToolsSheet = false
                        openKeyboardTool("شکلک و نمادها")
                    },

                    // Date/Time — keyboard-only panel
                    onNavigateToDateTime = {
                        showMoreToolsSheet = false
                        openKeyboardTool("تاریخ و ساعت")
                    },

                    // Intro screen
                    onNavigateToIntro = {
                        showMoreToolsSheet = false
                        onNavigateToIntro()
                    },

                    // Privacy screen
                    onNavigateToPrivacy = {
                        showMoreToolsSheet = false
                        onNavigateToPrivacy()
                    }
                )
            }
        }
    }
}