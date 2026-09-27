package com.example.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.ClipbordApp
import com.example.MainActivity
import com.example.domain.prediction.SuggestionEngine
import com.example.domain.smart.PersianAutoFixEngine
import com.example.domain.smart.SmartSnippetsAndDictionary
import com.example.keyboard.HapticAndSoundFeedback
import com.example.keyboard.KeyItem
import com.example.keyboard.KeyType
import com.example.keyboard.KeyboardLanguage
import com.example.keyboard.KeyboardMode
import com.example.themes.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ClipbordIME : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    companion object {
        private const val TAG = "ClipbordIME"
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private lateinit var feedback: HapticAndSoundFeedback
    private var suggestionEngine = SuggestionEngine()

    private var currentLanguage by mutableStateOf(KeyboardLanguage.PERSIAN)
    private var currentMode by mutableStateOf(KeyboardMode.TEXT)
    private var isShifted by mutableStateOf(false)
    private var isCapsLock by mutableStateOf(false)
    private var suggestions by mutableStateOf(listOf<String>())
    private var isPasswordField by mutableStateOf(false)
    private var isNumericPasswordField by mutableStateOf(false)
    private var extractedTextForAi by mutableStateOf("")

    private var speechRecognizer: SpeechRecognizer? = null
    private var cachedComposeView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
        try {
            savedStateRegistryController.performRestore(null)
            if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error initializing saved state or lifecycle in onCreate", e)
        }
        feedback = HapticAndSoundFeedback(this)
    }

    override fun onConfigureWindow(win: Window, isFullscreen: Boolean, isCandidatesOnly: Boolean) {
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly)
        attachDecorViewOwners(win.decorView)
    }

    private fun ensureWindowDecorViewOwners() {
        try {
            window?.window?.decorView?.let { decorView ->
                attachDecorViewOwners(decorView)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to attach decorView owners", e)
        }
    }

    private fun attachDecorViewOwners(decorView: View) {
        try {
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        } catch (e: Exception) {
            Log.w(TAG, "Exception attaching decorView owners", e)
        }
    }

    private fun startAndResumeLifecycle() {
        try {
            if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }
            if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            } else if (lifecycleRegistry.currentState == Lifecycle.State.STARTED) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error updating lifecycle to resume", e)
        }
    }

    private fun pauseLifecycle() {
        try {
            if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error updating lifecycle to pause", e)
        }
    }

    private fun stopLifecycle() {
        try {
            if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }
            if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error updating lifecycle to stop", e)
        }
    }

    override fun onCreateInputView(): View {
        ensureWindowDecorViewOwners()
        startAndResumeLifecycle()

        cachedComposeView?.let { existingView ->
            (existingView.parent as? ViewGroup)?.removeView(existingView)
            return existingView
        }

        val app = application as? ClipbordApp ?: ClipbordApp.instance
        val preferences = app.preferences
        val clipboardRepo = app.clipboardRepository

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@ClipbordIME.lifecycle)
            )
            setViewTreeLifecycleOwner(this@ClipbordIME)
            setViewTreeSavedStateRegistryOwner(this@ClipbordIME)
            setViewTreeViewModelStoreOwner(this@ClipbordIME)

            setContent {
                val themeId by preferences.themeId.collectAsState(initial = "turquoise")
                val heightRatio by preferences.keyboardHeightRatio.collectAsState(initial = 1.0f)
                val fontSizeScale by preferences.fontSizeScale.collectAsState(initial = 1.0f)
                val vibrationEnabled by preferences.vibrationEnabled.collectAsState(initial = true)
                val vibrationStrength by preferences.vibrationStrength.collectAsState(initial = 25)
                val soundEnabled by preferences.soundEnabled.collectAsState(initial = true)
                val persianNumbersDefault by preferences.persianNumbersEnabled.collectAsState(initial = true)
                val halfSpaceEnabled by preferences.halfSpaceEnabled.collectAsState(initial = true)
                val suggestionsEnabled by preferences.suggestionsEnabled.collectAsState(initial = true)
                val clipboardItems by clipboardRepo.allItems.collectAsState(initial = emptyList())

                val activeTheme = ThemeManager.getThemeById(themeId)

                KeyboardComposeView(
                    currentLanguage = currentLanguage,
                    currentMode = currentMode,
                    isShifted = isShifted,
                    isCapsLock = isCapsLock,
                    theme = activeTheme,
                    heightRatio = heightRatio,
                    fontSizeScale = fontSizeScale,
                    persianNumbersDefault = persianNumbersDefault,
                    halfSpaceEnabled = halfSpaceEnabled,
                    suggestions = if (isPasswordField || !suggestionsEnabled) emptyList() else suggestions,
                    clipboardItems = clipboardItems,
                    isPasswordField = isPasswordField,
                    currentExtractedText = extractedTextForAi,
                    onKeyPress = { key ->
                        feedback.keyPress(vibrationEnabled, vibrationStrength, soundEnabled)
                        handleKeyAction(key)
                    },
                    onKeyLongPress = { key ->
                        if (key.subLabel != null) {
                            feedback.keyPress(vibrationEnabled, vibrationStrength, soundEnabled)
                            currentInputConnection?.commitText(key.subLabel, 1)
                            updateSuggestionsAndExtractedText()
                        }
                    },
                    onSuggestionClick = { word ->
                        replaceCurrentWordWithSuggestion(word)
                    },
                    onLanguageToggle = {
                        currentLanguage = if (currentLanguage == KeyboardLanguage.PERSIAN) {
                            KeyboardLanguage.ENGLISH
                        } else {
                            KeyboardLanguage.PERSIAN
                        }
                        isShifted = false
                        isCapsLock = false
                        if (currentMode != KeyboardMode.BIG_NUMBERS) {
                            currentMode = KeyboardMode.TEXT
                        }
                        updateSuggestionsAndExtractedText()
                    },
                    onModeChange = { mode ->
                        currentMode = mode
                    },
                    onShiftToggle = {
                        if (isShifted) {
                            if (!isCapsLock && currentLanguage == KeyboardLanguage.ENGLISH) {
                                isCapsLock = true
                            } else {
                                isShifted = false
                                isCapsLock = false
                            }
                        } else {
                            isShifted = true
                        }
                    },
                    onVoiceClick = {
                        startVoiceRecognition()
                    },
                    onSettingsClick = {
                        val intent = Intent(this@ClipbordIME, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        startActivity(intent)
                    },
                    onThemeSelect = { newThemeId ->
                        serviceScope.launch {
                            preferences.setThemeId(newThemeId)
                        }
                    },
                    onPasteClipboardItem = { text ->
                        currentInputConnection?.commitText(text, 1)
                        updateSuggestionsAndExtractedText()
                    },
                    onAiReplaceText = { newText ->
                        replaceSelectedOrCurrentTextWithAiResult(newText)
                    }
                )
            }
        }

        cachedComposeView = composeView
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        ensureWindowDecorViewOwners()
        startAndResumeLifecycle()

        isPasswordField = false
        isNumericPasswordField = false

        info?.let {
            val inputType = it.inputType
            val inputClass = inputType and InputType.TYPE_MASK_CLASS
            val variation = inputType and InputType.TYPE_MASK_VARIATION

            // 1. تشخیص فیلد رمز عبور متنی
            if (
                variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            ) {
                isPasswordField = true
            }

            // 2. تشخیص فیلد رمز عبور عددی (رمز دوم بانکی، پین‌کد و...)
            if (
                inputClass == InputType.TYPE_CLASS_NUMBER &&
                (variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                 variation == InputType.TYPE_NUMBER_VARIATION_NORMAL) &&
                (it.inputType and InputType.TYPE_NUMBER_FLAG_DECIMAL == 0) &&
                (it.inputType and InputType.TYPE_NUMBER_FLAG_SIGNED == 0) &&
                isLikelySecretField(it)
            ) {
                isNumericPasswordField = true
                isPasswordField = true
            }

            // 3. سوییچ به حالت مناسب
            currentMode = when {
                isNumericPasswordField -> KeyboardMode.BIG_NUMBERS
                inputClass == InputType.TYPE_CLASS_NUMBER ||
                        inputClass == InputType.TYPE_CLASS_PHONE -> KeyboardMode.NUMBERS
                else -> KeyboardMode.TEXT
            }
        }

        updateSuggestionsAndExtractedText()
    }

    /**
     * بررسی می‌کند که آیا این فیلد احتمالاً یک فیلد حساس عددی (رمز دوم، CVV2، پین) است.
     * با استفاده از HintText و imeOptions.
     */
    private fun isLikelySecretField(info: EditorInfo): Boolean {
        val hint = info.hintText?.toString()?.lowercase() ?: ""
        val secretKeywords = listOf(
            "رمز", "پین", "pin", "password", "cvv", "cvc",
            "otp", "کد تایید", "کد تأیید", "شماره کارت", "second"
        )
        if (secretKeywords.any { hint.contains(it) }) return true

        // رمز دوم بانکی معمولاً NO_SUGGESTIONS یا PASSWORD دارد
        val imeOptions = info.imeOptions
        val noSuggestions = (imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING) != 0 ||
                (imeOptions and EditorInfo.IME_FLAG_NO_FULLSCREEN) != 0

        return noSuggestions
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        pauseLifecycle()
    }

    private fun handleKeyAction(key: KeyItem) {
        val ic = currentInputConnection ?: return

        try {
            when (key.type) {
                KeyType.CHARACTER -> {
                    ic.commitText(key.output, 1)
                    if (isShifted && !isCapsLock) {
                        isShifted = false
                    }
                    updateSuggestionsAndExtractedText()
                }
                KeyType.SPACE -> {
                    val textBefore = ic.getTextBeforeCursor(20, 0)?.toString() ?: ""
                    if (currentLanguage == KeyboardLanguage.PERSIAN &&
                        PersianAutoFixEngine.shouldConvertSpaceToHalfSpace(textBefore)
                    ) {
                        ic.commitText("\u200C", 1)
                    } else {
                        ic.commitText(" ", 1)
                    }
                    updateSuggestionsAndExtractedText()
                }
                KeyType.HALF_SPACE -> {
                    ic.commitText("\u200C", 1)
                    updateSuggestionsAndExtractedText()
                }
                KeyType.BACKSPACE -> {
                    val selectedText = ic.getSelectedText(0)
                    if (selectedText.isNullOrEmpty()) {
                        ic.deleteSurroundingText(1, 0)
                    } else {
                        ic.commitText("", 1)
                    }
                    updateSuggestionsAndExtractedText()
                }
                KeyType.ENTER -> {
                    val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
                    if (action != null && action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                        ic.performEditorAction(action)
                    } else {
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                    }
                }
                KeyType.TAB -> {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_TAB))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_TAB))
                }
                KeyType.CURSOR_LEFT -> {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
                }
                KeyType.CURSOR_RIGHT -> {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
                }
                KeyType.CURSOR_UP -> {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP))
                }
                KeyType.CURSOR_DOWN -> {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN))
                }
                KeyType.SELECT_ALL -> {
                    ic.performContextMenuAction(android.R.id.selectAll)
                }
                KeyType.COPY -> {
                    ic.performContextMenuAction(android.R.id.copy)
                }
                KeyType.PASTE -> {
                    ic.performContextMenuAction(android.R.id.paste)
                    updateSuggestionsAndExtractedText()
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception during handleKeyAction", e)
        }
    }

    private fun updateSuggestionsAndExtractedText() {
        val ic = currentInputConnection ?: return

        try {
            if (isPasswordField) {
                suggestions = emptyList()
                extractedTextForAi = ""
                return
            }

            val selectedText = ic.getSelectedText(0)?.toString()
            if (!selectedText.isNullOrBlank()) {
                extractedTextForAi = selectedText.trim()
            } else {
                val textBefore = ic.getTextBeforeCursor(200, 0)?.toString() ?: ""
                extractedTextForAi = textBefore.trim()
            }

            val textBefore = ic.getTextBeforeCursor(30, 0)?.toString() ?: ""
            val words = textBefore.split(" ", "\n", "\t")
            val currentWord = words.lastOrNull() ?: ""
            val previousWord = if (words.size > 1) words[words.size - 2] else null

            val computedSuggestions = suggestionEngine.getSuggestions(
                currentWord = currentWord,
                previousWord = previousWord,
                isPersian = currentLanguage == KeyboardLanguage.PERSIAN,
                maxCount = 4
            ).toMutableList()

            if (currentWord.isNotBlank()) {
                val snippetMatch = SmartSnippetsAndDictionary.findSnippetByShortcut(currentWord)
                val wordShortcutMatch = SmartSnippetsAndDictionary.findWordByShortcut(currentWord)
                if (snippetMatch != null) {
                    computedSuggestions.add(0, snippetMatch.content)
                } else if (wordShortcutMatch != null) {
                    computedSuggestions.add(0, wordShortcutMatch)
                }
            }

            suggestions = computedSuggestions
        } catch (e: Exception) {
            Log.w(TAG, "Error updating suggestions/extracted text", e)
        }
    }

    private fun replaceCurrentWordWithSuggestion(word: String) {
        val ic = currentInputConnection ?: return
        try {
            val textBefore = ic.getTextBeforeCursor(30, 0)?.toString() ?: ""
            val words = textBefore.split(" ", "\n", "\t")
            val currentWord = words.lastOrNull() ?: ""

            if (currentWord.isNotEmpty()) {
                ic.deleteSurroundingText(currentWord.length, 0)
            }
            ic.commitText("$word ", 1)
            updateSuggestionsAndExtractedText()
        } catch (e: Exception) {
            Log.w(TAG, "Error replacing word with suggestion", e)
        }
    }

    private fun replaceSelectedOrCurrentTextWithAiResult(newText: String) {
        val ic = currentInputConnection ?: return
        try {
            val selectedText = ic.getSelectedText(0)?.toString()

            if (!selectedText.isNullOrEmpty()) {
                ic.commitText(newText, 1)
            } else {
                val before = ic.getTextBeforeCursor(200, 0)?.toString() ?: ""
                if (before.isNotEmpty()) {
                    ic.deleteSurroundingText(before.length, 0)
                }
                ic.commitText(newText, 1)
            }
            updateSuggestionsAndExtractedText()
        } catch (e: Exception) {
            Log.w(TAG, "Error replacing text with AI result", e)
        }
    }

    private fun startVoiceRecognition() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (currentLanguage == KeyboardLanguage.PERSIAN) "fa" else "en-US")
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "در حال گوش دادن... لطفاً صحبت کنید")
                }

                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                }

                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Toast.makeText(this@ClipbordIME, "🎙 در حال گوش دادن...", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            currentInputConnection?.commitText(matches[0] + " ", 1)
                            updateSuggestionsAndExtractedText()
                        }
                    }
                    override fun onError(error: Int) {
                        Toast.makeText(this@ClipbordIME, "صدا تشخیص داده نشد", Toast.LENGTH_SHORT).show()
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                speechRecognizer?.startListening(intent)
            } else {
                Toast.makeText(this, "سرویس تشخیص گفتار روی دستگاه فعال نیست", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "خطا در اجرای تایپ صوتی: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        stopLifecycle()
        try {
            if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error handling onDestroy lifecycle", e)
        }
        store.clear()
        cachedComposeView = null
        serviceJob.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
        super.onDestroy()
    }
}