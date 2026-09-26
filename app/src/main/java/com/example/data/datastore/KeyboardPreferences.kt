package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "clipbord_settings")

class KeyboardPreferences(private val context: Context) {

    companion object {
        val KEY_THEME_ID = stringPreferencesKey("theme_id")
        val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
        val KEY_KEYBOARD_HEIGHT = floatPreferencesKey("keyboard_height")
        val KEY_FONT_SIZE_SCALE = floatPreferencesKey("font_size_scale")
        val KEY_VIBRATION = booleanPreferencesKey("vibration_enabled")
        val KEY_VIBRATION_STRENGTH = intPreferencesKey("vibration_strength")
        val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        val KEY_SUGGESTIONS = booleanPreferencesKey("suggestions_enabled")
        val KEY_AUTO_CORRECTION = booleanPreferencesKey("auto_correction_enabled")
        val KEY_PERSIAN_NUMBERS = booleanPreferencesKey("persian_numbers_enabled")
        val KEY_HALF_SPACE = booleanPreferencesKey("half_space_enabled")
        val KEY_VOICE_TYPING = booleanPreferencesKey("voice_typing_enabled")
        val KEY_IS_VIP = booleanPreferencesKey("is_vip_purchased")

        // Smart Writing Preferences
        val KEY_SMART_HALF_SPACE = booleanPreferencesKey("smart_half_space_enabled")
        val KEY_SMART_AUTO_FIX = booleanPreferencesKey("smart_auto_fix_enabled")
        val KEY_KEYBOARD_MODE = stringPreferencesKey("keyboard_mode") // FAST, SMART, PRO
        val KEY_ONE_HAND_MODE = stringPreferencesKey("one_hand_mode") // NONE, LEFT, RIGHT
        val KEY_GESTURES_ENABLED = booleanPreferencesKey("gestures_enabled")

        // AI Preferences
        val KEY_AI_ENABLED = booleanPreferencesKey("ai_enabled")
        val KEY_AI_BUTTON_VISIBLE = booleanPreferencesKey("ai_button_visible")
        val KEY_AI_DEFAULT_TONE = stringPreferencesKey("ai_default_tone")
        val KEY_AI_DAILY_USAGE_COUNT = intPreferencesKey("ai_daily_usage_count")
        val KEY_AI_LAST_USAGE_DATE = stringPreferencesKey("ai_last_usage_date")
        val KEY_CUSTOM_AI_ENDPOINT = stringPreferencesKey("custom_ai_endpoint")
        val KEY_CUSTOM_AI_MODEL = stringPreferencesKey("custom_ai_model")
    }

    val themeId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_ID] ?: "turquoise"
    }

    val darkMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: "SYSTEM"
    }

    val keyboardHeightRatio: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_KEYBOARD_HEIGHT] ?: 1.0f
    }

    val fontSizeScale: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_FONT_SIZE_SCALE] ?: 1.0f
    }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_VIBRATION] ?: true
    }

    val vibrationStrength: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_VIBRATION_STRENGTH] ?: 25
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SOUND] ?: true
    }

    val suggestionsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SUGGESTIONS] ?: true
    }

    val autoCorrectionEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTO_CORRECTION] ?: true
    }

    val persianNumbersEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PERSIAN_NUMBERS] ?: true
    }

    val halfSpaceEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_HALF_SPACE] ?: true
    }

    val voiceTypingEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_VOICE_TYPING] ?: true
    }

    val isVip: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_VIP] ?: false
    }

    val smartHalfSpaceEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SMART_HALF_SPACE] ?: true
    }

    val smartAutoFixEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SMART_AUTO_FIX] ?: true
    }

    val keyboardMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_KEYBOARD_MODE] ?: "SMART"
    }

    val oneHandMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONE_HAND_MODE] ?: "NONE"
    }

    val gesturesEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_GESTURES_ENABLED] ?: true
    }

    // AI Flow accessors
    val aiEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AI_ENABLED] ?: true
    }

    val aiButtonVisible: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AI_BUTTON_VISIBLE] ?: true
    }

    val aiDefaultTone: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_AI_DEFAULT_TONE] ?: "NEUTRAL"
    }

    val aiDailyUsageCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_AI_DAILY_USAGE_COUNT] ?: 0
    }

    val aiLastUsageDate: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_AI_LAST_USAGE_DATE] ?: ""
    }

    val customAiEndpoint: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_CUSTOM_AI_ENDPOINT] ?: ""
    }

    val customAiModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_CUSTOM_AI_MODEL] ?: ""
    }

    suspend fun setThemeId(themeId: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_ID] = themeId
        }
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = mode
        }
    }

    suspend fun setKeyboardHeight(ratio: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_KEYBOARD_HEIGHT] = ratio
        }
    }

    suspend fun setFontSizeScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FONT_SIZE_SCALE] = scale
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VIBRATION] = enabled
        }
    }

    suspend fun setVibrationStrength(strength: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VIBRATION_STRENGTH] = strength
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SOUND] = enabled
        }
    }

    suspend fun setSuggestionsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SUGGESTIONS] = enabled
        }
    }

    suspend fun setAutoCorrectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_CORRECTION] = enabled
        }
    }

    suspend fun setPersianNumbersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PERSIAN_NUMBERS] = enabled
        }
    }

    suspend fun setHalfSpaceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HALF_SPACE] = enabled
        }
    }

    suspend fun setVoiceTypingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VOICE_TYPING] = enabled
        }
    }

    suspend fun setVipStatus(isVip: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_VIP] = isVip
        }
    }

    suspend fun setSmartHalfSpaceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SMART_HALF_SPACE] = enabled
        }
    }

    suspend fun setSmartAutoFixEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SMART_AUTO_FIX] = enabled
        }
    }

    suspend fun setKeyboardMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_KEYBOARD_MODE] = mode
        }
    }

    suspend fun setOneHandMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONE_HAND_MODE] = mode
        }
    }

    suspend fun setGesturesEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GESTURES_ENABLED] = enabled
        }
    }

    suspend fun setAiEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AI_ENABLED] = enabled
        }
    }

    suspend fun setAiButtonVisible(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AI_BUTTON_VISIBLE] = visible
        }
    }

    suspend fun setAiDefaultTone(tone: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AI_DEFAULT_TONE] = tone
        }
    }

    suspend fun setAiUsage(count: Int, date: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AI_DAILY_USAGE_COUNT] = count
            preferences[KEY_AI_LAST_USAGE_DATE] = date
        }
    }

    suspend fun setCustomAiConfig(endpoint: String, model: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CUSTOM_AI_ENDPOINT] = endpoint
            preferences[KEY_CUSTOM_AI_MODEL] = model
        }
    }
}
