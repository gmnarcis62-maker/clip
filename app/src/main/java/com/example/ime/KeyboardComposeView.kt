package com.example.ime

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardTab
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
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

// ✅ Design tokens — مقادیر یکدست برای کل کیبورد
private val KeyRadius = 10.dp
private val PanelRadius = 14.dp
private val HKeyGap = 6.dp
private val VKeyGap = 8.dp
private val NormalKeyHeight = 52.dp
private val BigNumberKeyHeight = 68.dp
private val ToolbarHeight = 48.dp

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
    inputSessionKey: Int = 0,
    hapticEnabled: Boolean = true,
    soundEnabled: Boolean = true,
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

    LaunchedEffect(activePanel) {
        if (activePanel == ActiveImePanel.SMART_DICTIONARY) {
            dictionarySearchQuery = currentExtractedText
        }
    }

    // ✅ وقتی کاربر روی یک فیلد متنی جدید کلیک می‌کند، برگرد به کیبورد اصلی
    LaunchedEffect(inputSessionKey) {
        if (inputSessionKey > 0) {
            activePanel = ActiveImePanel.KEYBOARD
        }
    }

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
            KeyboardMode.BIG_NUMBERS -> {
                // حالت اعداد درشت فقط برای رمز دوم
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
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                if (currentMode != KeyboardMode.BIG_NUMBERS) {
                    VelvetSuggestionToolbar(
                        theme = theme,
                        suggestions = suggestions,
                        activePanel = activePanel,
                        onSuggestionClick = onSuggestionClick,
                        onVoiceClick = onVoiceClick,
                        onSettingsClick = onSettingsClick,
                        onToggleAi = {
                            activePanel = if (activePanel == ActiveImePanel.AI_ASSISTANT) ActiveImePanel.KEYBOARD else ActiveImePanel.AI_ASSISTANT
                        },
                        onToggleEmoji = {
                            activePanel = if (activePanel == ActiveImePanel.EMOJI) ActiveImePanel.KEYBOARD else ActiveImePanel.EMOJI
                        },
                        onToggleClipboard = {
                            activePanel = if (activePanel == ActiveImePanel.CLIPBOARD) ActiveImePanel.KEYBOARD else ActiveImePanel.CLIPBOARD
                        },
                        onToggleMoreTools = {
                            activePanel = if (activePanel == ActiveImePanel.MORE_TOOLS) ActiveImePanel.KEYBOARD else ActiveImePanel.MORE_TOOLS
                        }
                    )
                }

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
                            hapticEnabled = hapticEnabled,
                            soundEnabled = soundEnabled,
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
                            onInsertText = { text -> onPasteClipboardItem(text) },
                            onBackspace = {
                                onKeyPress(KeyItem(label = "⌫", type = KeyType.BACKSPACE))
                            },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.TEXT_ART -> {
                        TextArtStudioPanel(
                            theme = theme,
                            onInsertText = { textArt -> onPasteClipboardItem(textArt) },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.CLIPBOARD -> {
                        VelvetClipboardPanel(
                            theme = theme,
                            clipboardItems = clipboardItems,
                            onItemClick = { text -> onPasteClipboardItem(text) },
                            onClose = { activePanel = ActiveImePanel.KEYBOARD }
                        )
                    }

                    ActiveImePanel.THEME_PICKER -> {
                        VelvetThemeQuickPickerPanel(
                            currentThemeId = theme.id,
                            onThemeSelected = { newThemeId -> onThemeSelect(newThemeId) },
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
                            onOpenPanel = { panel -> activePanel = panel },
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
                                hapticEnabled = hapticEnabled,
                                soundEnabled = soundEnabled,
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
                                onInsertOutput = { output -> dictionarySearchQuery += output }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Toolbar
// ═══════════════════════════════════════════════════════════════════

@Composable
fun VelvetSuggestionToolbar(
    theme: KeyboardTheme,
    suggestions: List<String>,
    activePanel: ActiveImePanel,
    onSuggestionClick: (String) -> Unit,
    onVoiceClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggleAi: () -> Unit,
    onToggleEmoji: () -> Unit,
    onToggleClipboard: () -> Unit,
    onToggleMoreTools: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(ToolbarHeight)
            .padding(bottom = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = theme.suggestionBarBackgroundColor,
        border = BorderStroke(0.5.dp, theme.keyBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AiToolbarChip(
                    theme = theme,
                    isActive = activePanel == ActiveImePanel.AI_ASSISTANT,
                    onClick = onToggleAi
                )

                ToolbarIconButton(
                    icon = Icons.Filled.EmojiEmotions,
                    contentDescription = "ایموجی",
                    isActive = activePanel == ActiveImePanel.EMOJI,
                    theme = theme,
                    onClick = onToggleEmoji,
                    testTag = "ime_emoji_btn"
                )

                ToolbarIconButton(
                    icon = Icons.Filled.ContentPaste,
                    contentDescription = "کلیپ‌بورد",
                    isActive = activePanel == ActiveImePanel.CLIPBOARD,
                    theme = theme,
                    onClick = onToggleClipboard,
                    testTag = "ime_clipboard_btn"
                )

                ToolbarIconButton(
                    icon = Icons.Filled.GridView,
                    contentDescription = "ابزارها",
                    isActive = activePanel == ActiveImePanel.MORE_TOOLS,
                    theme = theme,
                    onClick = onToggleMoreTools,
                    testTag = "ime_more_tools_btn"
                )

                ToolbarIconButton(
                    icon = Icons.Filled.Mic,
                    contentDescription = "تایپ صوتی",
                    isActive = false,
                    theme = theme,
                    onClick = onVoiceClick,
                    testTag = "ime_voice_btn"
                )

                ToolbarIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "تنظیمات",
                    isActive = false,
                    theme = theme,
                    onClick = onSettingsClick,
                    testTag = "ime_settings_btn"
                )
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .width(1.dp)
                    .height(22.dp)
                    .background(theme.keySubTextColor.copy(alpha = 0.15f))
            )

            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                if (suggestions.isEmpty()) {
                    item {
                        Text(
                            text = "شروع به تایپ کنید...",
                            color = theme.keySubTextColor.copy(alpha = 0.55f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else {
                    items(suggestions) { suggestion ->
                        SuggestionChip(
                            text = suggestion,
                            theme = theme,
                            onClick = { onSuggestionClick(suggestion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiToolbarChip(
    theme: KeyboardTheme,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bg by animateColorAsState(
        targetValue = when {
            isActive -> theme.accentColor
            isPressed -> theme.accentColor.copy(alpha = 0.18f)
            else -> theme.accentColor.copy(alpha = 0.10f)
        },
        animationSpec = tween(150),
        label = "ai_chip_bg"
    )

    val fg by animateColorAsState(
        targetValue = if (isActive) theme.accentTextColor else theme.accentColor,
        animationSpec = tween(150),
        label = "ai_chip_fg"
    )

    Surface(
        shape = RoundedCornerShape(9.dp),
        color = bg,
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("ime_ai_btn")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "هوش مصنوعی",
                color = fg,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    isActive: Boolean,
    theme: KeyboardTheme,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bg by animateColorAsState(
        targetValue = when {
            isActive -> theme.accentColor.copy(alpha = 0.14f)
            isPressed -> theme.keySubTextColor.copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        animationSpec = tween(120),
        label = "toolbar_btn_bg"
    )

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) theme.accentColor else theme.keySubTextColor,
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun SuggestionChip(
    text: String,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bg by animateColorAsState(
        targetValue = if (isPressed) theme.accentColor.copy(alpha = 0.14f) else Color.Transparent,
        animationSpec = tween(100),
        label = "suggestion_bg"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("suggestion_${text.take(5)}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = theme.suggestionHighlightColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// Keyboard Grid
// ═══════════════════════════════════════════════════════════════════

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
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
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
            KeyboardMode.BIG_NUMBERS -> KeyboardLayouts.getBigNumbersRows(
                usePersianDigits = (isPersian && persianNumbersDefault)
            )
        }
    }

    val baseRowHeight = when (currentMode) {
        KeyboardMode.BIG_NUMBERS -> (BigNumberKeyHeight.value * heightRatio).dp
        else -> (NormalKeyHeight.value * heightRatio).dp
    }

    val horizontalGap = if (currentMode == KeyboardMode.BIG_NUMBERS) 8.dp else HKeyGap
    val verticalGap = if (currentMode == KeyboardMode.BIG_NUMBERS) 8.dp else VKeyGap

    val bigFontScale = if (currentMode == KeyboardMode.BIG_NUMBERS) fontSizeScale * 1.35f else fontSizeScale

    // ✅ جهت هر ردیف:
    // - انگلیسی: کل کیبورد LTR (مثل کامپیوتر)
    // - فارسی در حالت NUMBERS/SYMBOLS/BIG_NUMBERS: LTR (اعداد از چپ)
    // - ردیف بالای اعداد در حالت TEXT فارسی: LTR
    // - بقیه حالت‌ها: RTL (حروف فارسی از راست)
    val forceKeyboardLtr = currentLanguage == KeyboardLanguage.ENGLISH ||
            currentMode != KeyboardMode.TEXT

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_keyboard_grid"),
        verticalArrangement = Arrangement.spacedBy(verticalGap)
    ) {
        rows.forEachIndexed { rowIndex, rowKeys ->
            val isTopNumberRow = rowIndex == 0
            val rowDirection = when {
                forceKeyboardLtr -> LayoutDirection.Ltr
                isTopNumberRow -> LayoutDirection.Ltr
                else -> LayoutDirection.Rtl
            }

            CompositionLocalProvider(LocalLayoutDirection provides rowDirection) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(baseRowHeight),
                    horizontalArrangement = Arrangement.spacedBy(horizontalGap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowKeys.forEach { key ->
                        VelvetKeyItemView(
                            key = key,
                            theme = theme,
                            fontSizeScale = bigFontScale,
                            hapticEnabled = hapticEnabled,
                            soundEnabled = soundEnabled,
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
}

// ═══════════════════════════════════════════════════════════════════
// Key View
// ═══════════════════════════════════════════════════════════════════

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VelvetKeyItemView(
    key: KeyItem,
    theme: KeyboardTheme,
    fontSizeScale: Float,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSelectOption: (String) -> Unit
) {
    var showPopupDialog by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)

    val isCharacterKey = key.type == KeyType.CHARACTER || key.type == KeyType.HALF_SPACE

    // ✅ فیدبک لمسی + صوتی — مستقیم از خود View
    // این روش مطمئن‌تر از HapticAndSoundFeedback است و در IME کار می‌کند
    LaunchedEffect(isPressed) {
        if (isPressed) {
            if (hapticEnabled) {
                view.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }
            if (soundEnabled) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }
        }
    }

    // حذف پیوسته وقتی Backspace نگه داشته می‌شه
    LaunchedEffect(isPressed) {
        if (isPressed && key.type == KeyType.BACKSPACE) {
            currentOnClick()
            delay(400)
            while (isPressed) {
                currentOnClick()
                delay(55)
            }
        }
    }

    // پیش‌نمایش حرف فقط بعد از نگه داشتن (نه روی هر لمس)
    LaunchedEffect(isPressed) {
        if (isPressed && isCharacterKey) {
            delay(220)
            showPreview = true
        } else {
            showPreview = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.80f, stiffness = 1200f),
        label = "key_scale"
    )

    val pressOverlayAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.18f else 0f,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "key_overlay"
    )

    val isSpecial = key.type != KeyType.CHARACTER && key.type != KeyType.SPACE && key.type != KeyType.HALF_SPACE
    val isAction = key.type == KeyType.ENTER
    val isHalfSpace = key.type == KeyType.HALF_SPACE
    val isBigNumber = key.label.length == 1 && key.label[0].isDigit() ||
            key.label in listOf("۱","۲","۳","۴","۵","۶","۷","۸","۹","۰")

    val keyColor = when {
        isAction -> theme.accentColor
        isSpecial -> theme.specialKeyBackgroundColor
        else -> theme.keyBackgroundColor
    }

    val textColor = when {
        isAction -> theme.accentTextColor
        isHalfSpace -> theme.accentColor
        isSpecial -> theme.specialKeyTextColor
        else -> theme.keyTextColor
    }

    val overlayColor = when {
        isAction -> Color.White
        isSpecial -> theme.specialKeyTextColor
        else -> theme.keyTextColor
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .scale(scale)
            .clip(RoundedCornerShape(KeyRadius))
            .background(keyColor)
            .border(
                width = 0.5.dp,
                color = if (isAction)
                    theme.accentColor.copy(alpha = 0.5f)
                else
                    theme.keyBorderColor,
                shape = RoundedCornerShape(KeyRadius)
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (key.type != KeyType.BACKSPACE) {
                        currentOnClick()
                    }
                },
                onLongClick = {
                    if (key.popupOptions.isNotEmpty()) {
                        showPopupDialog = true
                    } else {
                        currentOnLongClick()
                    }
                }
            )
            .testTag("key_${key.label}"),
        contentAlignment = Alignment.Center
    ) {
        if (pressOverlayAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor.copy(alpha = pressOverlayAlpha * 0.35f))
            )
        }

        if (key.subLabel != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, start = 6.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = key.subLabel,
                    color = if (isHalfSpace) theme.accentColor else theme.keySubTextColor.copy(alpha = 0.7f),
                    fontSize = (9 * fontSizeScale).sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

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
                        modifier = Modifier.size(if (isBigNumber) 24.dp else 19.dp)
                    )
                }
                KeyType.HALF_SPACE -> {
                    Text(
                        text = "نیم‌فاصله",
                        color = textColor,
                        fontSize = (11 * fontSizeScale).sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
                KeyType.SPACE -> {
                    Text(
                        text = key.label,
                        color = theme.keySubTextColor.copy(alpha = 0.7f),
                        fontSize = (12 * fontSizeScale).sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
                KeyType.TAB -> {
                    Text(
                        text = "Tab",
                        color = textColor,
                        fontSize = (11 * fontSizeScale).sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
                KeyType.SHIFT -> {
                    Text(
                        text = key.label,
                        color = textColor,
                        fontSize = (15 * fontSizeScale).sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    val charWeight = if (isAction || isSpecial || isBigNumber) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    }
                    Text(
                        text = key.label,
                        color = textColor,
                        fontSize = when {
                            key.label.length > 2 -> (12 * fontSizeScale).sp
                            isBigNumber -> (24 * fontSizeScale).sp
                            else -> (18 * fontSizeScale).sp
                        },
                        fontWeight = charWeight,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }

        if (showPopupDialog && key.popupOptions.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = -140),
                onDismissRequest = { showPopupDialog = false }
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.surfaceColor,
                    border = BorderStroke(1.dp, theme.keyBorderColor),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        key.popupOptions.forEach { opt ->
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.keyBackgroundColor)
                                    .clickable {
                                        showPopupDialog = false
                                        onSelectOption(opt)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    color = theme.keyTextColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showPreview && !showPopupDialog && isCharacterKey) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = -100),
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, theme.keyBorderColor),
                    modifier = Modifier.size(width = 48.dp, height = 52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (key.type == KeyType.HALF_SPACE) "\u200C" else key.label,
                            color = theme.keyTextColor,
                            fontSize = (22 * fontSizeScale).sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Cursor Tools Panel
// ═══════════════════════════════════════════════════════════════════

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
            .background(theme.surfaceColor, RoundedCornerShape(PanelRadius))
            .padding(10.dp)
            .testTag("ime_cursor_tools_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ابزارهای ویرایش و مکان‌نما",
                color = theme.keyTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Keyboard,
                    contentDescription = "بازگشت به کیبورد",
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
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
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, null, tint = theme.keyTextColor)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { onKeyPress(KeyItem(label = "→", type = KeyType.CURSOR_RIGHT)) },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Filled.KeyboardArrowRight, null, tint = theme.keyTextColor)
                    }

                    Button(
                        onClick = { onKeyPress(KeyItem(label = "↓", type = KeyType.CURSOR_DOWN)) },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, null, tint = theme.keyTextColor)
                    }

                    Button(
                        onClick = { onKeyPress(KeyItem(label = "←", type = KeyType.CURSOR_LEFT)) },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Filled.KeyboardArrowLeft, null, tint = theme.keyTextColor)
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
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Filled.SelectAll, null, tint = theme.accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("انتخاب همه", color = theme.keyTextColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = { onKeyPress(KeyItem(label = "Copy", type = KeyType.COPY)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Filled.ContentCopy, null, tint = theme.accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کپی", color = theme.keyTextColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = { onKeyPress(KeyItem(label = "Paste", type = KeyType.PASTE)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Filled.ContentPaste, null, tint = theme.accentTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("چسباندن", color = theme.accentTextColor, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }

                Button(
                    onClick = { onKeyPress(KeyItem(label = "Tab", type = KeyType.TAB)) },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.keyBackgroundColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Filled.KeyboardTab, null, tint = theme.keyTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کلید Tab", color = theme.keyTextColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Emoji Picker Panel
// ═══════════════════════════════════════════════════════════════════

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
            .background(theme.surfaceColor, RoundedCornerShape(PanelRadius))
            .padding(8.dp)
            .testTag("emoji_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) theme.accentColor.copy(alpha = 0.16f)
                                else Color.Transparent
                            )
                            .clickable { selectedCategoryIndex = index }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${cat.icon} ${cat.name}",
                            color = if (isSelected) theme.accentColor else theme.keyTextColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onBackspace, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "حذف",
                        tint = theme.specialKeyTextColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Keyboard,
                        contentDescription = "بازگشت",
                        tint = theme.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        val activeEmojis = categories[selectedCategoryIndex].emojis
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 40.dp),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(activeEmojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.keyBackgroundColor)
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 22.sp)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Clipboard Panel
// ═══════════════════════════════════════════════════════════════════

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
            .background(theme.surfaceColor, RoundedCornerShape(PanelRadius))
            .padding(8.dp)
            .testTag("ime_clipboard_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "کلیپ‌بورد",
                color = theme.keyTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Keyboard,
                    contentDescription = "بازگشت",
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجو...", fontSize = 12.sp, color = theme.keySubTextColor) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = theme.keySubTextColor, modifier = Modifier.size(16.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentColor,
                unfocusedBorderColor = theme.keyBorderColor,
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.keyBackgroundColor)
                            .clickable { onItemClick(item.text) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.isPinned) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "پین",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(14.dp).padding(end = 6.dp)
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

// ═══════════════════════════════════════════════════════════════════
// Theme Picker Panel
// ═══════════════════════════════════════════════════════════════════

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
            .background(Color(0xFF151A24), RoundedCornerShape(PanelRadius))
            .padding(10.dp)
            .testTag("ime_theme_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "پوسته‌ها",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Keyboard,
                    contentDescription = "بستن",
                    tint = Color(0xFF14B8A6),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(themes) { th ->
                val isSelected = th.id == currentThemeId
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(th.backgroundColor)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected) th.accentColor else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onThemeSelected(th.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = th.namePersian,
                                color = th.keyTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (th.isPremium) {
                                Text(
                                    text = "VIP",
                                    color = Color(0xFFE8A33D),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(th.accentColor)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// AI Assistant Panel
// ═══════════════════════════════════════════════════════════════════

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
            .background(theme.surfaceColor, RoundedCornerShape(PanelRadius))
            .padding(10.dp)
            .testTag("ime_ai_panel")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "دستیار هوشمند",
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (remainingQuota == Int.MAX_VALUE) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIP",
                            color = theme.accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        text = "${PersianDateUtils.toPersianDigits(remainingQuota)} درخواست",
                        color = theme.keySubTextColor,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Filled.Keyboard,
                    contentDescription = "بستن",
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (isPasswordField) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Warning, null, tint = theme.keySubTextColor, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "به دلایل امنیتی، در فیلد رمز عبور غیرفعال است.",
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
                    .background(theme.keyBackgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = theme.accentColor,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "در حال پردازش...",
                        color = theme.keySubTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
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
                    .background(theme.keyBackgroundColor, RoundedCornerShape(12.dp))
                    .padding(10.dp),
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
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f).height(38.dp)
                    ) {
                        Icon(Icons.Filled.Check, null, tint = theme.accentTextColor, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("جایگزینی", color = theme.accentTextColor, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }

                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.keyBackgroundColor)
                            .clickable {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("AI Result", resultText ?: ""))
                                Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.ContentCopy, null, tint = theme.keySubTextColor, modifier = Modifier.size(15.dp))
                    }

                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.keyBackgroundColor)
                            .clickable {
                                currentOp?.let { executeAi(it, customInstruction) }
                            }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Refresh, null, tint = theme.keySubTextColor, modifier = Modifier.size(15.dp))
                    }

                    Box(
                        modifier = Modifier
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(theme.keyBackgroundColor)
                            .clickable { resultText = null }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = theme.keySubTextColor, modifier = Modifier.size(15.dp))
                    }
                }
            }
            return@Column
        }

        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.keyBackgroundColor)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = theme.accentColor,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "بستن",
                        color = theme.keyTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { errorMessage = null }
                    )
                }
            }
        }

        OutlinedTextField(
            value = userText,
            onValueChange = { userText = it },
            placeholder = { Text("متن ورودی...", fontSize = 11.sp, color = theme.keySubTextColor) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentColor,
                unfocusedBorderColor = theme.keyBorderColor,
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
                    placeholder = { Text("مثال: خلاصه کن...", fontSize = 11.sp, color = theme.keySubTextColor) },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.accentColor,
                        unfocusedBorderColor = theme.keyBorderColor,
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
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(42.dp)
                ) {
                    Icon(Icons.Filled.Send, null, tint = theme.accentTextColor, modifier = Modifier.size(16.dp))
                }
            }
        }

        val operations = AiOperation.values()
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(operations) { op ->
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.keyBackgroundColor)
                        .clickable {
                            if (op == AiOperation.CUSTOM_PROMPT) {
                                showCustomInput = !showCustomInput
                            } else {
                                executeAi(op)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = op.iconEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = op.titlePersian,
                            color = theme.keyTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}