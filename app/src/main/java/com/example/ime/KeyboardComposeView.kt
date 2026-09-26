package com.example.ime

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.ClipbordApp
import com.example.ai.domain.AiOperation
import com.example.ai.domain.AiResult
import com.example.data.local.ClipboardEntity
import com.example.domain.shamsi.PersianDateUtils
import com.example.keyboard.EmojiCategory
import com.example.keyboard.EmojiData
import com.example.keyboard.KeyItem
import com.example.keyboard.KeyType
import com.example.keyboard.KeyboardLanguage
import com.example.keyboard.KeyboardLayouts
import com.example.keyboard.KeyboardMode
import com.example.themes.KeyboardTheme
import com.example.themes.ThemeManager
import com.example.ime.panels.SmartWritingPanel
import com.example.ime.panels.QuickRepliesPanel
import com.example.ime.panels.SmartTonePanel
import com.example.ime.panels.CalculatorPanel
import com.example.ime.panels.DateTimePanel
import com.example.ime.panels.UnitConverterPanel
import com.example.ime.panels.TextToolsPanel
import com.example.ime.panels.KaomojiSymbolsPanel
import com.example.ime.panels.GlobalSearchPanel
import com.example.ime.panels.MoreToolsHubPanel
import com.example.ime.panels.TextArtStudioPanel
import com.example.ime.panels.EmojiAndExpressionCenterPanel
import com.example.dictionary.ui.DictionaryPanel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ActiveImePanel {
    KEYBOARD,
    EMOJI,
    CLIPBOARD,
    THEME_PICKER,
    AI_ASSISTANT,
    CURSOR_TOOLS,
    SMART_WRITING_AUTO_FIX,
    QUICK_REPLIES,
    SMART_TONE,
    CALCULATOR,
    DATE_TIME,
    UNIT_CONVERTER,
    TEXT_TOOLS,
    KAOMOJI_SYMBOLS,
    GLOBAL_SEARCH,
    MORE_TOOLS,
    SMART_DICTIONARY,
    TEXT_ART,
    EMOJI_CENTER,
    TEXT_DECORATOR,
    CAPTION_BIO
}

@Composable
fun KeyboardComposeView(
    currentLanguage: KeyboardLanguage,
    currentMode: KeyboardMode,
    isShifted: Boolean,
    isCapsLock: Boolean,
    theme: KeyboardTheme,
    heightRatio: Float,
    fontSizeScale: Float,
    persianNumbersDefault: Boolean,
    halfSpaceEnabled: Boolean,
    suggestions: List<String>,
    clipboardItems: List<ClipboardEntity>,
    isPasswordField: Boolean = false,
    currentExtractedText: String = "",
    onKeyPress: (KeyItem) -> Unit,
    onKeyLongPress: (KeyItem) -> Unit = {},
    onSuggestionClick: (String) -> Unit,
    onLanguageToggle: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onShiftToggle: () -> Unit,
    onVoiceClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onThemeSelect: (String) -> Unit,
    onPasteClipboardItem: (String) -> Unit,
    onAiReplaceText: (String) -> Unit
) {
    var activePanel by remember { mutableStateOf(ActiveImePanel.KEYBOARD) }
    var dictionarySearchQuery by remember { mutableStateOf(currentExtractedText) }

    // Reset dictionary search query when the dictionary panel opens
    LaunchedEffect(activePanel) {
        if (activePanel == ActiveImePanel.SMART_DICTIONARY) {
            dictionarySearchQuery = currentExtractedText
        }
    }

    // Shared mode-switch handler used by both the KEYBOARD panel and the
    // dictionary-search keyboard to avoid duplicating logic.
    val handleModeSwitch: (KeyItem) -> Unit = { key ->
        val label = key.label
        val isToSymbols = label == "=#\\" || label.contains("=") || label.contains("#")
        val isToNumbers = label == "123" || label == "۱۲۳" || label == "?123" ||
                label == "١٢٣" || label.contains("1") || label.contains("۱")
        when (currentMode) {
            KeyboardMode.TEXT -> {
                if (isToSymbols) onModeChange(KeyboardMode.SYMBOLS)
                else onModeChange(KeyboardMode.NUMBERS)
            }
            KeyboardMode.NUMBERS -> {
                if (isToSymbols) onModeChange(KeyboardMode.SYMBOLS)
                else onModeChange(KeyboardMode.TEXT)
            }
            KeyboardMode.SYMBOLS -> {
                if (isToNumbers) onModeChange(KeyboardMode.NUMBERS)
                else onModeChange(KeyboardMode.TEXT)
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.backgroundColor),
            color = theme.backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                VelvetSuggestionToolbar(
                    theme = theme,
                    suggestions = suggestions,
                    activePanel = activePanel,
                    onSuggestionClick = onSuggestionClick,
                    onVoiceClick = onVoiceClick,
                    onSettingsClick = onSettingsClick,
                    onToggleSmartWriting = {
                        activePanel = if (activePanel == ActiveImePanel.SMART_WRITING_AUTO_FIX) ActiveImePanel.KEYBOARD else ActiveImePanel.SMART_WRITING_AUTO_FIX
                    },
                    onToggleDictionary = {
                        activePanel = if (activePanel == ActiveImePanel.SMART_DICTIONARY) ActiveImePanel.KEYBOARD else ActiveImePanel.SMART_DICTIONARY
                    },
                    onToggleQuickReplies = {
                        activePanel = if (activePanel == ActiveImePanel.QUICK_REPLIES) ActiveImePanel.KEYBOARD else ActiveImePanel.QUICK_REPLIES
                    },
                    onToggleMoreTools = {
                        activePanel = if (activePanel == ActiveImePanel.MORE_TOOLS) ActiveImePanel.KEYBOARD else ActiveImePanel.MORE_TOOLS
                    },
                    onToggleEmoji = {
                        activePanel = if (activePanel == ActiveImePanel.EMOJI) ActiveImePanel.KEYBOARD else ActiveImePanel.EMOJI
                    },
                    onToggleClipboard = {
                        activePanel = if (activePanel == ActiveImePanel.CLIPBOARD) ActiveImePanel.KEYBOARD else ActiveImePanel.CLIPBOARD
                    },
                    onToggleThemePicker = {
                        activePanel = if (activePanel == ActiveImePanel.THEME_PICKER) ActiveImePanel.KEYBOARD else ActiveImePanel.THEME_PICKER
                    },
                    onToggleAi = {
                        activePanel = if (activePanel == ActiveImePanel.AI_ASSISTANT) ActiveImePanel.KEYBOARD else ActiveImePanel.AI_ASSISTANT
                    },
                    onToggleCursorTools = {
                        activePanel = if (activePanel == ActiveImePanel.CURSOR_TOOLS) ActiveImePanel.KEYBOARD else ActiveImePanel.CURSOR_TOOLS
                    }
                )

                when (activePanel) {
                    ActiveImePanel.KEYBOARD -> {
                        MainVelvetKeyLayout(
                            currentLanguage = currentLanguage,
                            currentMode = currentMode,
                            isShifted = isShifted,
                            isCapsLock = isCapsLock,
                            theme = theme,
                            heightRatio = heightRatio,
                            fontSizeScale = fontSizeScale,
                            persianNumbersDefault = persianNumbersDefault,
                            halfSpaceEnabled = halfSpaceEnabled,
                            onKeyPress = { key ->
                                when (key.type) {
                                    KeyType.LANG_SWITCH -> onLanguageToggle()
                                    KeyType.SHIFT -> onShiftToggle()
                                    KeyType.MODE_SWITCH -> handleModeSwitch(key)
                                    KeyType.EMOJI -> activePanel = ActiveImePanel.EMOJI
                                    KeyType.CLIPBOARD -> activePanel = ActiveImePanel.CLIPBOARD
                                    KeyType.VOICE -> onVoiceClick()
                                    KeyType.SETTINGS -> onSettingsClick()
                                    else -> onKeyPress(key)
                                }
                            },
                            onKeyLongPress = onKeyLongPress,
                            onInsertOutput = { output ->
                                onKeyPress(KeyItem(label = output, output = output, type = KeyType.CHARACTER))
                            }
                        )
                    }

                    ActiveImePanel.CURSOR_TOOLS -> {
                        VelvetCursorToolsPanel(
                            theme = theme,
                            onKeyPress = onKeyPress,
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.EMOJI,
                    ActiveImePanel.EMOJI_CENTER,
                    ActiveImePanel.TEXT_DECORATOR,
                    ActiveImePanel.CAPTION_BIO -> {
                        EmojiAndExpressionCenterPanel(
                            theme = theme,
                            initialText = currentExtractedText,
                            onInsertText = { text ->
                                onPasteClipboardItem(text)
                            },
                            onBackspace = {
                                onKeyPress(KeyItem(label = "⌫", type = KeyType.BACKSPACE))
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.TEXT_ART -> {
                        TextArtStudioPanel(
                            theme = theme,
                            onInsertText = { textArt ->
                                onPasteClipboardItem(textArt)
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.CLIPBOARD -> {
                        VelvetClipboardPanel(
                            theme = theme,
                            clipboardItems = clipboardItems,
                            onItemClick = { text ->
                                onPasteClipboardItem(text)
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.THEME_PICKER -> {
                        VelvetThemeQuickPickerPanel(
                            currentThemeId = theme.id,
                            onThemeSelected = { newThemeId ->
                                onThemeSelect(newThemeId)
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.AI_ASSISTANT -> {
                        VelvetAiBottomSheetPanel(
                            theme = theme,
                            initialText = currentExtractedText,
                            isPasswordField = isPasswordField,
                            onReplace = { newText ->
                                onAiReplaceText(newText)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.SMART_WRITING_AUTO_FIX -> {
                        SmartWritingPanel(
                            theme = theme,
                            currentText = currentExtractedText,
                            onReplaceText = { newText ->
                                onAiReplaceText(newText)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.QUICK_REPLIES -> {
                        QuickRepliesPanel(
                            theme = theme,
                            onSelectReply = { reply ->
                                onPasteClipboardItem(reply)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.SMART_TONE -> {
                        SmartTonePanel(
                            theme = theme,
                            selectedText = currentExtractedText,
                            onReplaceText = { newText ->
                                onAiReplaceText(newText)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.CALCULATOR -> {
                        CalculatorPanel(
                            theme = theme,
                            onInsertResult = { res ->
                                onPasteClipboardItem(res)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.DATE_TIME -> {
                        DateTimePanel(
                            theme = theme,
                            onInsertText = { dt ->
                                onPasteClipboardItem(dt)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.UNIT_CONVERTER -> {
                        UnitConverterPanel(
                            theme = theme,
                            onInsertText = { txt ->
                                onPasteClipboardItem(txt)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.TEXT_TOOLS -> {
                        TextToolsPanel(
                            theme = theme,
                            currentText = currentExtractedText,
                            onReplaceText = { newText ->
                                onAiReplaceText(newText)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.KAOMOJI_SYMBOLS -> {
                        KaomojiSymbolsPanel(
                            theme = theme,
                            onInsertText = { sym ->
                                onKeyPress(KeyItem(label = sym, output = sym, type = KeyType.CHARACTER))
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.GLOBAL_SEARCH -> {
                        GlobalSearchPanel(
                            theme = theme,
                            clipboardItems = clipboardItems,
                            onInsertText = { txt ->
                                onPasteClipboardItem(txt)
                                activePanel = ActiveImePanel.KEYBOARD
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.MORE_TOOLS -> {
                        MoreToolsHubPanel(
                            theme = theme,
                            onOpenPanel = { panel ->
                                activePanel = panel
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.SMART_DICTIONARY -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            DictionaryPanel(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                theme = theme,
                                searchQuery = dictionarySearchQuery,
                                onSearchQueryChange = { dictionarySearchQuery = it },
                                onInsertText = { txt ->
                                    onPasteClipboardItem(txt)
                                    activePanel = ActiveImePanel.KEYBOARD
                                },
                                onClose = { activePanel = ActiveImePanel.KEYBOARD }
                            )

                            // Main keyboard below the dictionary panel,
                            // routing keystrokes into the search query.
                            MainVelvetKeyLayout(
                                currentLanguage = currentLanguage,
                                currentMode = currentMode,
                                isShifted = isShifted,
                                isCapsLock = isCapsLock,
                                theme = theme,
                                heightRatio = heightRatio,
                                fontSizeScale = fontSizeScale,
                                persianNumbersDefault = persianNumbersDefault,
                                halfSpaceEnabled = halfSpaceEnabled,
                                onKeyPress = { key ->
                                    when (key.type) {
                                        KeyType.LANG_SWITCH -> onLanguageToggle()
                                        KeyType.SHIFT -> onShiftToggle()
                                        KeyType.MODE_SWITCH -> handleModeSwitch(key)
                                        KeyType.CHARACTER -> dictionarySearchQuery += key.output
                                        KeyType.SPACE -> dictionarySearchQuery += " "
                                        KeyType.HALF_SPACE -> dictionarySearchQuery += "\u200C"
                                        KeyType.BACKSPACE -> {
                                            if (dictionarySearchQuery.isNotEmpty()) {
                                                dictionarySearchQuery = dictionarySearchQuery.dropLast(1)
                                            }
                                        }
                                        else -> {}
                                    }
                                },
                                onKeyLongPress = {},
                                onInsertOutput = { output ->
                                    dictionarySearchQuery += output
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VelvetSuggestionToolbar(
    theme: KeyboardTheme,
    suggestions: List<String>,
    activePanel: ActiveImePanel,
    onSuggestionClick: (String) -> Unit,
    onVoiceClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggleSmartWriting: () -> Unit,
    onToggleDictionary: () -> Unit,
    onToggleQuickReplies: () -> Unit,
    onToggleMoreTools: () -> Unit,
    onToggleEmoji: () -> Unit,
    onToggleClipboard: () -> Unit,
    onToggleThemePicker: () -> Unit,
    onToggleAi: () -> Unit,
    onToggleCursorTools: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(bottom = 3.dp),
        shape = RoundedCornerShape(10.dp),
        color = theme.suggestionBarBackgroundColor,
        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activePanel == ActiveImePanel.AI_ASSISTANT) theme.accentColor else theme.keyBackgroundColor.copy(alpha = 0.9f),
                    border = BorderStroke(0.5.dp, if (activePanel == ActiveImePanel.AI_ASSISTANT) theme.accentColor else Color(0xFFFFB800).copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clickable { onToggleAi() }
                        .padding(horizontal = 2.dp)
                        .testTag("ime_ai_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "✨ هوش مصنوعی",
                            color = if (activePanel == ActiveImePanel.AI_ASSISTANT) theme.accentTextColor else Color(0xFFFFB800),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onToggleDictionary,
                    modifier = Modifier.size(30.dp).testTag("ime_dictionary_btn")
                ) {
                    Text(text = "📖", fontSize = 14.sp)
                }

                IconButton(
                    onClick = onToggleSmartWriting,
                    modifier = Modifier.size(30.dp).testTag("ime_smart_writing_btn")
                ) {
                    Text(text = "✍️", fontSize = 14.sp)
                }

                IconButton(
                    onClick = onToggleMoreTools,
                    modifier = Modifier.size(30.dp).testTag("ime_more_tools_btn")
                ) {
                    Text(text = "⚡", fontSize = 14.sp)
                }

                IconButton(
                    onClick = onToggleQuickReplies,
                    modifier = Modifier.size(30.dp).testTag("ime_quick_replies_btn")
                ) {
                    Text(text = "💬", fontSize = 14.sp)
                }

                IconButton(
                    onClick = onToggleClipboard,
                    modifier = Modifier.size(30.dp).testTag("ime_clipboard_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "کلیپبورد",
                        tint = if (activePanel == ActiveImePanel.CLIPBOARD) theme.accentColor else theme.specialKeyTextColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = onToggleEmoji,
                    modifier = Modifier.size(30.dp).testTag("ime_emoji_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = "ایموجی",
                        tint = if (activePanel == ActiveImePanel.EMOJI) theme.accentColor else theme.specialKeyTextColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = onToggleCursorTools,
                    modifier = Modifier.size(30.dp).testTag("ime_cursor_tools_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "ابزارهای ویرایش و مکان‌نما",
                        tint = if (activePanel == ActiveImePanel.CURSOR_TOOLS) theme.accentColor else theme.specialKeyTextColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = onToggleThemePicker,
                    modifier = Modifier.size(30.dp).testTag("ime_theme_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "پوسته",
                        tint = if (activePanel == ActiveImePanel.THEME_PICKER) theme.accentColor else theme.specialKeyTextColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier.size(30.dp).testTag("ime_voice_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "تایپ صوتی",
                        tint = theme.specialKeyTextColor,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(30.dp).testTag("ime_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "تنظیمات",
                        tint = theme.keySubTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(theme.keySubTextColor.copy(alpha = 0.25f))
            )

            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                if (suggestions.isEmpty()) {
                    item {
                        Text(
                            text = "مرسانا آماده تایپ",
                            color = theme.keySubTextColor.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                } else {
                    items(suggestions) { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.keyBackgroundColor.copy(alpha = 0.85f),
                            border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                            modifier = Modifier
                                .clickable { onSuggestionClick(suggestion) }
                                .padding(vertical = 4.dp)
                                .testTag("suggestion_${suggestion.take(5)}")
                        ) {
                            Text(
                                text = suggestion,
                                color = theme.suggestionHighlightColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainVelvetKeyLayout(
    currentLanguage: KeyboardLanguage,
    currentMode: KeyboardMode,
    isShifted: Boolean,
    isCapsLock: Boolean,
    theme: KeyboardTheme,
    heightRatio: Float,
    fontSizeScale: Float,
    persianNumbersDefault: Boolean,
    halfSpaceEnabled: Boolean,
    onKeyPress: (KeyItem) -> Unit,
    onKeyLongPress: (KeyItem) -> Unit,
    onInsertOutput: (String) -> Unit
) {
    val rows = remember(currentLanguage, currentMode, isShifted, isCapsLock, persianNumbersDefault, halfSpaceEnabled) {
        val isPersian = currentLanguage == KeyboardLanguage.PERSIAN
        when (currentMode) {
            KeyboardMode.TEXT -> {
                if (isPersian) {
                    KeyboardLayouts.getPersianRows(
                        isShifted = isShifted,
                        usePersianNumbers = persianNumbersDefault,
                        showHalfSpace = halfSpaceEnabled
                    )
                } else {
                    KeyboardLayouts.getEnglishRows(
                        isShifted = isShifted,
                        isCapsLock = isCapsLock
                    )
                }
            }
            KeyboardMode.NUMBERS -> KeyboardLayouts.getNumbersRows(
                usePersianDigits = (isPersian && persianNumbersDefault),
                isPersian = isPersian
            )
            KeyboardMode.SYMBOLS -> KeyboardLayouts.getSymbolsRows(
                usePersianDigits = (isPersian && persianNumbersDefault),
                isPersian = isPersian
            )
        }
    }

    val baseRowHeight = (49 * heightRatio).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_keyboard_grid"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(baseRowHeight),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowKeys.forEach { key ->
                    VelvetKeyItemView(
                        key = key,
                        theme = theme,
                        fontSizeScale = fontSizeScale,
                        modifier = Modifier.weight(key.weight),
                        onClick = { onKeyPress(key) },
                        onLongClick = { onKeyLongPress(key) },
                        onSelectOption = { option -> onInsertOutput(option) }
                    )
                }
            }
        }
    }
}

@Composable
fun VelvetKeyItemView(
    key: KeyItem,
    theme: KeyboardTheme,
    fontSizeScale: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectOption: (String) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var showPopupDialog by remember { mutableStateOf(false) }
    var isRepeating by remember { mutableStateOf(false) }

    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)

    // Continuous delete: fires the first deletion immediately on press,
    // then keeps deleting every 55 ms after a 400 ms initial hold delay.
    LaunchedEffect(isRepeating) {
        if (isRepeating && key.type == KeyType.BACKSPACE) {
            currentOnClick()
            delay(400)
            while (isRepeating) {
                currentOnClick()
                delay(55)
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = 800f),
        label = "key_scale"
    )

    // Press overlay alpha: 0 when idle, ~0.42 when pressed
    val pressedOverlayAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.42f else 0f,
        animationSpec = tween(durationMillis = 60),
        label = "key_pressed_overlay"
    )

    val isSpecial = key.type != KeyType.CHARACTER && key.type != KeyType.SPACE && key.type != KeyType.HALF_SPACE
    val isAction = key.type == KeyType.ENTER
    val isHalfSpace = key.type == KeyType.HALF_SPACE

    // Velvet multi-layer background gradient
    val backgroundBrush = when {
        isAction -> Brush.verticalGradient(
            listOf(theme.accentColor, theme.accentColor.copy(alpha = 0.85f))
        )
        isHalfSpace -> Brush.verticalGradient(
            listOf(theme.specialKeyBackgroundColor, theme.keyGradientBottom)
        )
        isSpecial -> Brush.verticalGradient(
            listOf(theme.specialKeyBackgroundColor, theme.keyGradientBottom)
        )
        key.type == KeyType.SPACE -> Brush.verticalGradient(
            listOf(theme.keyBackgroundColor, theme.keyGradientBottom)
        )
        else -> Brush.verticalGradient(
            listOf(theme.keyBackgroundColor, theme.keyGradientBottom)
        )
    }

    val textColor = when {
        isAction -> theme.accentTextColor
        isHalfSpace -> theme.accentColor
        isSpecial -> theme.specialKeyTextColor
        else -> theme.keyTextColor
    }

    // Press overlay color per key type so the press is always visible.
    // - Enter (already accent-filled) → white overlay (lightens it)
    // - Half-space & everything else → accent overlay tint
    val pressOverlayColor = when {
        isAction -> Color.White
        isHalfSpace -> theme.accentColor
        else -> theme.accentColor
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .shadow(
                elevation = if (theme.keyShadowElevationDp > 0) theme.keyShadowElevationDp.dp else 0.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color.Black.copy(alpha = 0.4f),
                ambientColor = Color.Black.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundBrush)
            .border(
                BorderStroke(
                    width = 0.75.dp,
                    brush = if (isHalfSpace) Brush.verticalGradient(listOf(theme.accentColor.copy(alpha = 0.5f), Color.Transparent))
                    else if (isAction) Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent))
                    else Brush.verticalGradient(listOf(theme.keyTopHighlightColor, Color.Transparent))
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (key.type == KeyType.BACKSPACE) {
                            isRepeating = true
                        }
                        tryAwaitRelease()
                        isPressed = false
                        if (key.type == KeyType.BACKSPACE) {
                            isRepeating = false
                        }
                    },
                    onTap = {
                        // Backspace clicks are handled by the LaunchedEffect above
                        if (key.type != KeyType.BACKSPACE) {
                            currentOnClick()
                        }
                    },
                    onLongPress = {
                        if (key.popupOptions.isNotEmpty()) {
                            showPopupDialog = true
                        } else {
                            currentOnLongClick()
                        }
                    }
                )
            }
            .testTag("key_${key.label}"),
        contentAlignment = Alignment.Center
    ) {
        // Press overlay - drawn first (bottom layer) so labels stay on top
        if (pressedOverlayAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pressOverlayColor.copy(alpha = pressedOverlayAlpha))
            )
        }

        // Velvet top corner sub-label (Persian digit or secondary symbol)
        if (key.subLabel != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 2.dp, start = 4.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = key.subLabel,
                    color = if (isHalfSpace) theme.accentColor else theme.keySubTextColor.copy(alpha = 0.85f),
                    fontSize = (9 * fontSizeScale).sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Velvet main key label / vector icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (key.type) {
                KeyType.BACKSPACE -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "حذف",
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                KeyType.HALF_SPACE -> {
                    Text(
                        text = "نیم‌فاصله",
                        color = textColor,
                        fontSize = (11 * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                KeyType.SPACE -> {
                    Text(
                        text = key.label,
                        color = theme.keySubTextColor,
                        fontSize = (12 * fontSizeScale).sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
                KeyType.TAB -> {
                    Text(
                        text = "Tab",
                        color = textColor,
                        fontSize = (11 * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                KeyType.SHIFT -> {
                    Text(
                        text = key.label,
                        color = textColor,
                        fontSize = (16 * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Text(
                        text = key.label,
                        color = textColor,
                        fontSize = when {
                            key.label.length > 2 -> (12 * fontSizeScale).sp
                            else -> (18 * fontSizeScale).sp
                        },
                        fontWeight = if (isAction || isSpecial) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        // Velvet Long-Press Floating Popup
        if (showPopupDialog && key.popupOptions.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = -140),
                onDismissRequest = { showPopupDialog = false }
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.surfaceColor,
                    shadowElevation = 10.dp,
                    border = BorderStroke(1.dp, theme.accentColor.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        key.popupOptions.forEach { opt ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = theme.keyBackgroundColor,
                                border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable {
                                        showPopupDialog = false
                                        onSelectOption(opt)
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = opt,
                                        color = theme.keyTextColor,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Velvet Real-time Touch Key Preview
        if (isPressed && !showPopupDialog && (key.type == KeyType.CHARACTER || key.type == KeyType.HALF_SPACE)) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = -120),
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
                    color = theme.surfaceColor,
                    shadowElevation = 14.dp,
                    border = BorderStroke(1.2.dp, theme.accentColor.copy(alpha = 0.85f)),
                    modifier = Modifier.size(width = 52.dp, height = 54.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        theme.keyBackgroundColor,
                                        theme.keyGradientBottom
                                    )
                                )
                            )
                    ) {
                        Text(
                            text = if (key.type == KeyType.HALF_SPACE) "‌" else key.label,
                            color = theme.accentColor,
                            fontSize = (26 * fontSizeScale).sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VelvetCursorToolsPanel(
    theme: KeyboardTheme,
    onKeyPress: (KeyItem) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
            .testTag("ime_cursor_tools_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🧭 ابزارهای ویرایش، انتخاب و مکان‌نما (PC Style)",
                color = theme.keyTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "بازگشت به کیبورد",
                    tint = theme.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onKeyPress(KeyItem(label = "↑", type = KeyType.CURSOR_UP)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, null, tint = theme.keyTextColor)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { onKeyPress(KeyItem(label = "→", type = KeyType.CURSOR_RIGHT)) },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, null, tint = theme.keyTextColor)
                    }

                    Button(
                        onClick = { onKeyPress(KeyItem(label = "↓", type = KeyType.CURSOR_DOWN)) },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = theme.keyTextColor)
                    }

                    Button(
                        onClick = { onKeyPress(KeyItem(label = "←", type = KeyType.CURSOR_LEFT)) },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, null, tint = theme.keyTextColor)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { onKeyPress(KeyItem(label = "Select All", type = KeyType.SELECT_ALL)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Default.SelectAll, null, tint = theme.accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("انتخاب همه (Ctrl+A)", color = theme.keyTextColor, fontSize = 11.sp)
                }

                Button(
                    onClick = { onKeyPress(KeyItem(label = "Copy", type = KeyType.COPY)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, null, tint = theme.accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کپی متن (Ctrl+C)", color = theme.keyTextColor, fontSize = 11.sp)
                }

                Button(
                    onClick = { onKeyPress(KeyItem(label = "Paste", type = KeyType.PASTE)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Default.ContentPaste, null, tint = theme.accentTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("چسباندن متن (Ctrl+V)", color = theme.accentTextColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = { onKeyPress(KeyItem(label = "Tab", type = KeyType.TAB)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Default.KeyboardTab, null, tint = theme.keyTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کلید Tab", color = theme.keyTextColor, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun VelvetEmojiPickerPanel(
    theme: KeyboardTheme,
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = EmojiData.CATEGORIES

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
            .testTag("emoji_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories.size) { index ->
                    val cat = categories[index]
                    val isSelected = index == selectedCategoryIndex
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) theme.accentColor else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSelected) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier
                            .clickable { selectedCategoryIndex = index }
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "${cat.icon} ${cat.name}",
                            color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onBackspace, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "حذف",
                        tint = theme.specialKeyTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "بازگشت به کیبورد",
                        tint = theme.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        val activeEmojis = categories[selectedCategoryIndex].emojis
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 38.dp),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(activeEmojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.keyBackgroundColor.copy(alpha = 0.6f))
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun VelvetClipboardPanel(
    theme: KeyboardTheme,
    clipboardItems: List<ClipboardEntity>,
    onItemClick: (String) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(clipboardItems, searchQuery) {
        if (searchQuery.isBlank()) clipboardItems
        else clipboardItems.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
            .testTag("ime_clipboard_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 متن‌های کلیپبورد و پاسخ سریع",
                color = theme.keyTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "بازگشت به کیبورد",
                    tint = theme.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجو در یادداشت‌ها...", fontSize = 11.sp, color = theme.keySubTextColor) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = theme.keySubTextColor, modifier = Modifier.size(16.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentColor,
                unfocusedBorderColor = theme.keyBackgroundColor,
                focusedContainerColor = theme.keyBackgroundColor,
                unfocusedContainerColor = theme.keyBackgroundColor,
                focusedTextColor = theme.keyTextColor,
                unfocusedTextColor = theme.keyTextColor
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "هیچ متنی یافت نشد",
                    color = theme.keySubTextColor,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item.text) },
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.isPinned) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "پین شده",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(14.dp).padding(end = 4.dp)
                                )
                            }
                            Text(
                                text = item.text,
                                color = theme.keyTextColor,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VelvetThemeQuickPickerPanel(
    currentThemeId: String,
    onThemeSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    val themes = ThemeManager.ALL_THEMES

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .background(Color(0xFF161C28), RoundedCornerShape(12.dp))
            .padding(8.dp)
            .testTag("ime_theme_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎨 پوسته‌های مخملی (Velvet Themes)",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "بستن",
                    tint = Color(0xFF00D2BE),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(themes) { th ->
                val isSelected = th.id == currentThemeId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = th.backgroundColor,
                    modifier = Modifier
                        .height(54.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) th.accentColor else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onThemeSelected(th.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = th.namePersian,
                                color = th.keyTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (th.isPremium) {
                                Text(
                                    text = "VIP ویژه",
                                    color = Color(0xFFFFB300),
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(th.accentColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VelvetAiBottomSheetPanel(
    theme: KeyboardTheme,
    initialText: String,
    isPasswordField: Boolean,
    onReplace: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val app = ClipbordApp.instance
    val aiRepository = app.aiRepository
    val scope = rememberCoroutineScope()

    var userText by remember { mutableStateOf(initialText) }
    var customInstruction by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentOp by remember { mutableStateOf<AiOperation?>(null) }
    var remainingQuota by remember { mutableIntStateOf(5) }

    LaunchedEffect(Unit) {
        remainingQuota = aiRepository.getRemainingDailyUsage()
    }

    fun executeAi(operation: AiOperation, prompt: String? = null) {
        if (userText.isBlank() && operation != AiOperation.CUSTOM_PROMPT) {
            Toast.makeText(context, "لطفاً ابتدا متنی بنویسید یا در کادر زیر وارد کنید.", Toast.LENGTH_SHORT).show()
            return
        }

        currentOp = operation
        isLoading = true
        errorMessage = null
        resultText = null

        scope.launch {
            val res = aiRepository.executeOperation(
                operation = operation,
                inputText = userText,
                customPrompt = prompt
            )
            isLoading = false
            when (res) {
                is AiResult.Success -> {
                    resultText = res.outputText
                    remainingQuota = if (res.isVip) Int.MAX_VALUE else res.remainingUsage
                }
                is AiResult.Error -> {
                    errorMessage = res.messagePersian
                }
                is AiResult.LimitReached -> {
                    errorMessage = "سقف ۵ درخواست رایگان امروز شما به پایان رسیده است. برای استفاده نامحدود نسخه VIP را فعال کنید."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(265.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
            .testTag("ime_ai_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "✨ دستیار هوش مصنوعی مرسانا",
                    color = Color(0xFFFFB800),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (remainingQuota == Int.MAX_VALUE) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFB800)) {
                        Text("VIP نامحدود", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                } else {
                    Surface(shape = RoundedCornerShape(6.dp), color = theme.keyBackgroundColor) {
                        Text("باقی‌مانده: ${PersianDateUtils.toPersianDigits(remainingQuota)}", color = theme.keySubTextColor, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "بستن",
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (isPasswordField) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "جهت حفظ امنیت و حریم خصوصی، هوش مصنوعی در فیلدهای رمز عبور غیرفعال است.",
                        color = theme.keyTextColor,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            return@Column
        }

        if (isLoading) {
            val infiniteTransition = rememberInfiniteTransition(label = "ai_loading")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "loading_alpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.keyBackgroundColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFFFFB800),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "در حال پردازش هوشمند متن با هوش مصنوعی مرسانا... ✨",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
            return@Column
        }

        if (resultText != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.keyBackgroundColor, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = resultText ?: "",
                            color = theme.keyTextColor,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onReplace(resultText ?: "") },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f).height(38.dp)
                    ) {
                        Icon(Icons.Default.Check, null, tint = theme.accentTextColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("✓ جایگزین کردن", color = theme.accentTextColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.surfaceColor,
                        modifier = Modifier
                            .height(38.dp)
                            .clickable {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("AI Result", resultText ?: ""))
                                Toast.makeText(context, "در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCopy, null, tint = theme.keyTextColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کپی", color = theme.keyTextColor, fontSize = 11.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.surfaceColor,
                        modifier = Modifier
                            .height(38.dp)
                            .clickable {
                                currentOp?.let { executeAi(it, customInstruction) }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = theme.keyTextColor, modifier = Modifier.size(14.dp))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.surfaceColor,
                        modifier = Modifier
                            .height(38.dp)
                            .clickable { resultText = null }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = theme.keyTextColor, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
            return@Column
        }

        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "بستن",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { errorMessage = null }
                    )
                }
            }
        }

        OutlinedTextField(
            value = userText,
            onValueChange = { userText = it },
            placeholder = { Text("متن ورودی خود را اینجا بنویسید یا از پیام انتخاب کنید...", fontSize = 11.sp, color = theme.keySubTextColor) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentColor,
                unfocusedBorderColor = theme.keyBackgroundColor,
                focusedContainerColor = theme.keyBackgroundColor,
                unfocusedContainerColor = theme.keyBackgroundColor,
                focusedTextColor = theme.keyTextColor,
                unfocusedTextColor = theme.keyTextColor
            ),
            singleLine = false,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (showCustomInput) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customInstruction,
                    onValueChange = { customInstruction = it },
                    placeholder = { Text("مثال: این متن را خلاصه کن / با لحن ادبی بنویس...", fontSize = 11.sp, color = theme.keySubTextColor) },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFB800),
                        unfocusedBorderColor = theme.keyBackgroundColor,
                        focusedContainerColor = theme.keyBackgroundColor,
                        unfocusedContainerColor = theme.keyBackgroundColor,
                        focusedTextColor = theme.keyTextColor,
                        unfocusedTextColor = theme.keyTextColor
                    ),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (customInstruction.isNotBlank()) {
                            executeAi(AiOperation.CUSTOM_PROMPT, customInstruction)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB800)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(42.dp)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }

        val operations = AiOperation.values()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(operations) { op ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                    modifier = Modifier
                        .height(40.dp)
                        .clickable {
                            if (op == AiOperation.CUSTOM_PROMPT) {
                                showCustomInput = !showCustomInput
                            } else {
                                executeAi(op)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = op.iconEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = op.titlePersian,
                            color = theme.keyTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}