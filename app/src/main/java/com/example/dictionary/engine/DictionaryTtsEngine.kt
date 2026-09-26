package com.example.dictionary.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class DictionaryTtsEngine(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isInitialized = true
                }
            }
        } catch (e: Exception) {
            Log.e("DictionaryTtsEngine", "Failed to initialize TTS", e)
        }
    }

    fun speak(text: String, language: String = "fa") {
        if (!isInitialized || tts == null || text.isBlank()) return

        try {
            val locale = if (language == "fa") Locale("fa", "IR") else Locale.US
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default or English
                tts?.setLanguage(Locale.US)
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dictionary_tts_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("DictionaryTtsEngine", "TTS speak failed", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
