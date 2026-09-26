package com.example

import com.example.ai.data.AiConfig
import com.example.ai.domain.AiOperation
import com.example.domain.prediction.PersianAutoCorrector
import com.example.domain.prediction.SuggestionEngine
import com.example.domain.shamsi.PersianDateUtils
import com.example.keyboard.KeyboardLayouts
import com.example.themes.ThemeManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPersianDigitsConversion() {
        val english = "1234567890"
        val persian = PersianDateUtils.toPersianDigits(english)
        assertEquals("۱۲۳۴۵۶۷۸۹۰", persian)
    }

    @Test
    fun testShamsiDateCalculation() {
        val (jy, jm, jd) = PersianDateUtils.gregorianToJalali(2026, 9, 24)
        assertTrue(jy >= 1405)
        assertTrue(jm in 1..12)
        assertTrue(jd in 1..31)
    }

    @Test
    fun testPersianAutoCorrection() {
        val correction1 = PersianAutoCorrector.getCorrection("میخام")
        assertEquals("می‌خوام", correction1)

        val correction2 = PersianAutoCorrector.getCorrection("میرم")
        assertEquals("می‌روم", correction2)

        val correction3 = PersianAutoCorrector.getCorrection("خاهش")
        assertEquals("خواهش", correction3)

        val correction4 = PersianAutoCorrector.getCorrection("اصلن")
        assertEquals("اصلاً", correction4)
    }

    @Test
    fun testSuggestionEngine() {
        val engine = SuggestionEngine(isAutoCorrectionEnabled = true, isSuggestionsEnabled = true)

        val suggestions = engine.getSuggestions("سل", isPersian = true)
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.startsWith("سل") })

        val nextWords = engine.getSuggestions("", previousWord = "سلام", isPersian = true)
        assertTrue(nextWords.isNotEmpty())
    }

    @Test
    fun testKeyboardLayouts() {
        val persianRows = KeyboardLayouts.getPersianRows(isShifted = false)
        assertTrue(persianRows.size >= 4)

        val englishRows = KeyboardLayouts.getEnglishRows(isShifted = false)
        assertTrue(englishRows.size >= 4)

        val numbersRows = KeyboardLayouts.getNumbersRows(usePersianDigits = true)
        assertTrue(numbersRows.isNotEmpty())
    }

    @Test
    fun testThemes() {
        val allThemes = ThemeManager.ALL_THEMES
        assertEquals(10, allThemes.size)

        val turquoise = ThemeManager.getThemeById("turquoise")
        assertEquals("فیروزه‌ای مخملی ایرانی", turquoise.namePersian)
        assertFalse(turquoise.isPremium)

        val amoled = ThemeManager.getThemeById("amoled")
        assertTrue(amoled.isPremium)
    }

    @Test
    fun testAiOperationsPromptBuilding() {
        val userText = "سلام فایل رو براتون فرستادم"

        // Test Grammar Operation
        val grammarMessages = AiOperation.GRAMMAR_CORRECTION.buildMessages(userText)
        assertEquals(2, grammarMessages.size)
        assertEquals("system", grammarMessages[0].role)
        assertEquals(userText, grammarMessages[1].content)

        // Test Formal Tone Operation
        val formalMessages = AiOperation.FORMAL_TONE.buildMessages(userText)
        assertEquals(2, formalMessages.size)
        assertTrue(formalMessages[0].content.contains("رسمی"))

        // Test Custom Prompt Operation
        val customMessages = AiOperation.CUSTOM_PROMPT.buildMessages(userText, customInstruction = "خلاصه کن")
        assertTrue(customMessages[1].content.contains("خلاصه کن"))

        // Test AiConfig
        assertEquals("atr_ZtEtzGtqJmQkrO-AiemLVy2jHGzPtBoD", AiConfig.DEFAULT_API_KEY)
        assertTrue(AiConfig.DEFAULT_BASE_URL.contains("api.atria-asi.ai"))
    }
}
