package com.example.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AppIntroScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.ClipboardManagerScreen
import com.example.ui.screens.DateTimeScreen
import com.example.ui.screens.DictionaryScreen
import com.example.ui.screens.EmojiExplorerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KaomojiScreen
import com.example.ui.screens.KeyboardTestScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TextToolsScreen
import com.example.ui.screens.ThemesScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.screens.VipScreen

object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val AI_ASSISTANT = "ai_assistant"
    const val KEYBOARD_TEST = "keyboard_test"
    const val CLIPBOARD_MANAGER = "clipboard_manager"
    const val THEMES = "themes"
    const val EMOJI_EXPLORER = "emoji_explorer"
    const val SETTINGS = "settings"
    const val VIP = "vip"
    const val ABOUT = "about"
    const val APP_INTRO = "app_intro"
    const val PRIVACY_POLICY = "privacy_policy"
    // ✅ مسیرهای جدید برای ابزارهای مستقل
    const val DICTIONARY = "dictionary"
    const val CALCULATOR = "calculator"
    const val UNIT_CONVERTER = "unit_converter"
    const val TEXT_TOOLS = "text_tools"
    const val KAOMOJI = "kaomoji"
    const val DATE_TIME = "date_time"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            BackHandler {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000) {
                    (context as? Activity)?.finish()
                } else {
                    lastBackPressTime = currentTime
                    Toast.makeText(context, "برای خروج دوباره دکمه بازگشت را بزنید", Toast.LENGTH_SHORT).show()
                }
            }

            HomeScreen(
                onNavigateToTest = { navController.navigate(NavRoutes.KEYBOARD_TEST) },
                onNavigateToClipboard = { navController.navigate(NavRoutes.CLIPBOARD_MANAGER) },
                onNavigateToThemes = { navController.navigate(NavRoutes.THEMES) },
                onNavigateToEmoji = { navController.navigate(NavRoutes.EMOJI_EXPLORER) },
                onNavigateToAi = { navController.navigate(NavRoutes.AI_ASSISTANT) },
                onNavigateToSuggestions = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToVoice = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToVip = { navController.navigate(NavRoutes.VIP) },
                onNavigateToAbout = { navController.navigate(NavRoutes.ABOUT) },
                onNavigateToIntro = { navController.navigate(NavRoutes.APP_INTRO) },
                onNavigateToPrivacy = { navController.navigate(NavRoutes.PRIVACY_POLICY) },
                // ✅ مسیرهای جدید
                onNavigateToDictionary = { navController.navigate(NavRoutes.DICTIONARY) },
                onNavigateToCalculator = { navController.navigate(NavRoutes.CALCULATOR) },
                onNavigateToUnitConverter = { navController.navigate(NavRoutes.UNIT_CONVERTER) },
                onNavigateToTextTools = { navController.navigate(NavRoutes.TEXT_TOOLS) },
                onNavigateToKaomoji = { navController.navigate(NavRoutes.KAOMOJI) },
                onNavigateToDateTime = { navController.navigate(NavRoutes.DATE_TIME) }
            )
        }

        composable(NavRoutes.AI_ASSISTANT) {
            AiAssistantScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToVip = { navController.navigate(NavRoutes.VIP) }
            )
        }

        composable(NavRoutes.KEYBOARD_TEST) {
            KeyboardTestScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavRoutes.CLIPBOARD_MANAGER) {
            ClipboardManagerScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavRoutes.THEMES) {
            ThemesScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToVip = { navController.navigate(NavRoutes.VIP) }
            )
        }

        composable(NavRoutes.EMOJI_EXPLORER) {
            EmojiExplorerScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToThemes = { navController.navigate(NavRoutes.THEMES) },
                onNavigateToAi = { navController.navigate(NavRoutes.AI_ASSISTANT) }
            )
        }

        composable(NavRoutes.VIP) {
            VipScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavRoutes.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToPrivacy = { navController.navigate(NavRoutes.PRIVACY_POLICY) },
                onNavigateToIntro = { navController.navigate(NavRoutes.APP_INTRO) }
            )
        }

        composable(NavRoutes.APP_INTRO) {
            AppIntroScreen(onBackClick = { navController.popBackStack() })
        }

        composable(NavRoutes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
        }

        // ✅ صفحات ابزارهای مستقل
        composable(NavRoutes.DICTIONARY) {
            DictionaryScreen(onBackClick = { navController.popBackStack() })
        }
        composable(NavRoutes.CALCULATOR) {
            CalculatorScreen(onBackClick = { navController.popBackStack() })
        }
        composable(NavRoutes.UNIT_CONVERTER) {
            UnitConverterScreen(onBackClick = { navController.popBackStack() })
        }
        composable(NavRoutes.TEXT_TOOLS) {
            TextToolsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(NavRoutes.KAOMOJI) {
            KaomojiScreen(onBackClick = { navController.popBackStack() })
        }
        composable(NavRoutes.DATE_TIME) {
            DateTimeScreen(onBackClick = { navController.popBackStack() })
        }
    }
}