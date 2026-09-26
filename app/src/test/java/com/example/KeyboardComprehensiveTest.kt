package com.example

import com.example.ai.data.AiConfig
import com.example.ai.domain.AiOperation
import com.example.domain.prediction.SuggestionEngine
import com.example.domain.smart.KeyboardCalculatorEngine
import com.example.domain.smart.LinkAndContactIntelligence
import com.example.domain.smart.PersianAutoFixEngine
import com.example.domain.smart.PersianDateTimeIntelligence
import com.example.domain.smart.PersianNumberIntelligence
import com.example.domain.smart.QuickRepliesAndSmartTone
import com.example.domain.smart.SmartSnippetsAndDictionary
import com.example.domain.smart.SnippetItem
import com.example.domain.smart.TextToolsEngine
import com.example.domain.smart.UnitConverterEngine
import com.example.keyboard.KeyType
import com.example.keyboard.KeyboardLanguage
import com.example.keyboard.KeyboardLayouts
import com.example.keyboard.KeyboardMode
import com.example.themes.ThemeManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardComprehensiveTest {

    @Test
    fun testPersianCharacterSetCompleteness() {
        val rows = KeyboardLayouts.getPersianRows(isShifted = false, usePersianNumbers = true, showHalfSpace = true)
        val allLabels = rows.flatMap { it.map { k -> k.label } }
        val allOutputs = rows.flatMap { it.map { k -> k.output } }
        val allPopups = rows.flatMap { it.flatMap { k -> k.popupOptions } }

        val persianAlphabet = listOf(
            "ا", "ب", "پ", "ت", "ث", "ج", "چ", "ح", "خ",
            "د", "ذ", "ر", "ز", "ژ", "س", "ش", "ص", "ض",
            "ط", "ظ", "ع", "غ", "ف", "ق", "ک", "گ",
            "ل", "م", "ن", "و", "ه", "ی"
        )

        for (letter in persianAlphabet) {
            val existsDirectOrPopup = allOutputs.contains(letter) || allPopups.contains(letter)
            assertTrue("Persian letter $letter must be present in layout or popups", existsDirectOrPopup)
        }

        val essential = listOf("ء", "ئ", "ة", "آ", "أ", "إ", "،", "؟", "!", ".")
        for (char in essential) {
            val exists = allOutputs.contains(char) || allPopups.contains(char) ||
                    KeyboardLayouts.getPersianRows(isShifted = true).flatMap { it.map { k -> k.output } }.contains(char)
            assertTrue("Essential character $char must be accessible", exists)
        }
    }

    @Test
    fun testHalfSpaceExactUnicode() {
        val rows = KeyboardLayouts.getPersianRows(isShifted = false, showHalfSpace = true)
        val halfSpaceKey = rows.flatMap { it }.find { it.type == KeyType.HALF_SPACE }
        assertNotNull("Half space key must exist", halfSpaceKey)
        assertEquals("Half space output must be exact U+200C (Zero Width Non-Joiner)", "\u200C", halfSpaceKey?.output)
    }

    @Test
    fun testPersianAutoFixEngine() {
        val test1 = PersianAutoFixEngine.fixText("می روم خانه ها")
        assertTrue("Prefix 'می' and suffix 'ها' should have half-spaces", test1.contains("می\u200Cروم") && test1.contains("خانه\u200Cها"))

        val test2 = PersianAutoFixEngine.fixText("مسول راجب به گزارش")
        assertTrue("Common typos should be auto-fixed", test2.contains("مسئول") && test2.contains("راجع به"))

        val shouldHalfSpace = PersianAutoFixEngine.shouldConvertSpaceToHalfSpace("من می")
        assertTrue("Space after 'می' should trigger smart half space", shouldHalfSpace)
    }

    @Test
    fun testPersianNumberIntelligence() {
        assertEquals("۱۲۵۰۰۰", PersianNumberIntelligence.toPersianDigits("125000"))
        assertEquals("125000", PersianNumberIntelligence.toEnglishDigits("۱۲۵۰۰۰"))
        assertEquals("۱۲۵,۰۰۰", PersianNumberIntelligence.formatWithCommas("125000"))

        val words = PersianNumberIntelligence.numberToPersianWords("125000")
        assertEquals("صد و بیست و پنج هزار", words)

        val wordsMillion = PersianNumberIntelligence.numberToPersianWords("1500000")
        assertEquals("یک میلیون و پانصد هزار", wordsMillion)
    }

    @Test
    fun testKeyboardCalculatorEngine() {
        val c1 = KeyboardCalculatorEngine.evaluate("25 + 75")
        assertTrue(c1.success)
        assertEquals("۱۰۰", c1.formattedResult)

        val c2 = KeyboardCalculatorEngine.evaluate("125000 × 3")
        assertTrue(c2.success)
        assertEquals("۳۷۵۰۰۰", c2.formattedResult)

        val c3 = KeyboardCalculatorEngine.evaluate("1000 ÷ 4")
        assertTrue(c3.success)
        assertEquals("۲۵۰", c3.formattedResult)

        val c4 = KeyboardCalculatorEngine.evaluate("100 - 25")
        assertTrue(c4.success)
        assertEquals("۷۵", c4.formattedResult)

        val c5 = KeyboardCalculatorEngine.evaluate("12.5 × 4")
        assertTrue(c5.success)
        assertEquals("۵۰", c5.formattedResult)

        val cPersianDigits = KeyboardCalculatorEngine.evaluate("۱۲۵۰۰۰ × ۳")
        assertTrue(cPersianDigits.success)
        assertEquals("۳۷۵۰۰۰", cPersianDigits.formattedResult)

        val cSqrt = KeyboardCalculatorEngine.evaluate("√25")
        assertTrue(cSqrt.success)
        assertEquals("۵", cSqrt.formattedResult)

        val cPow2 = KeyboardCalculatorEngine.evaluate("5²")
        assertTrue(cPow2.success)
        assertEquals("۲۵", cPow2.formattedResult)

        val cPow3 = KeyboardCalculatorEngine.evaluate("2³")
        assertTrue(cPow3.success)
        assertEquals("۸", cPow3.formattedResult)

        val cSin = KeyboardCalculatorEngine.evaluate("sin(0)")
        assertTrue(cSin.success)
        assertEquals("۰", cSin.formattedResult)

        val cCos = KeyboardCalculatorEngine.evaluate("cos(0)")
        assertTrue(cCos.success)
        assertEquals("۱", cCos.formattedResult)

        val cWords = KeyboardCalculatorEngine.evaluate("1500000")
        assertTrue(cWords.success)
        assertEquals("یک میلیون و پانصد هزار", cWords.persianWords)
    }

    @Test
    fun testUnitConverterEngineFull() {
        val kmToM = UnitConverterEngine.convert(UnitConverterEngine.UnitCategory.LENGTH, 1.0, "km", "m")
        assertNotNull(kmToM)
        assertEquals(1000.0, kmToM?.resultValue ?: 0.0, 0.001)

        val kgToG = UnitConverterEngine.convert(UnitConverterEngine.UnitCategory.WEIGHT, 1.0, "kg", "g")
        assertNotNull(kgToG)
        assertEquals(1000.0, kgToG?.resultValue ?: 0.0, 0.001)

        val lToMl = UnitConverterEngine.convert(UnitConverterEngine.UnitCategory.VOLUME, 1.0, "l", "ml")
        assertNotNull(lToMl)
        assertEquals(1000.0, lToMl?.resultValue ?: 0.0, 0.001)

        val minToH = UnitConverterEngine.convert(UnitConverterEngine.UnitCategory.TIME, 60.0, "min", "h")
        assertNotNull(minToH)
        assertEquals(1.0, minToH?.resultValue ?: 0.0, 0.001)
    }

    @Test
    fun testPersianRowArrangementOrder() {
        val rows = KeyboardLayouts.getPersianRows(isShifted = false)
        val row1Labels = rows[0].map { it.label }
        val expectedRow1 = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "چ")
        assertEquals(expectedRow1, row1Labels)

        val row2Labels = rows[1].map { it.label }
        val expectedRow2 = listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ")
        assertEquals(expectedRow2, row2Labels)

        val row3Labels = rows[2].filter { it.type == KeyType.CHARACTER }.map { it.label }
        val expectedRow3Letters = listOf("ظ", "ط", "ز", "ر", "د", "ذ", "ژ", "پ", "و")
        assertEquals(expectedRow3Letters, row3Labels)
    }

    @Test
    fun testUnitConverterEngine() {
        val res = UnitConverterEngine.convert(
            category = UnitConverterEngine.UnitCategory.WEIGHT,
            value = 5.0,
            fromUnitId = "kg",
            toUnitId = "g"
        )
        assertNotNull(res)
        assertEquals(5000.0, res?.resultValue ?: 0.0, 0.001)
    }

    @Test
    fun testTextToolsEngine() {
        val text = "سلام  جهان \n\n خط دوم  "
        val trimmed = TextToolsEngine.removeExtraSpaces(text)
        assertFalse(trimmed.contains("  "))

        val stats = TextToolsEngine.getStats("سلام جهان")
        assertEquals(2, stats.wordCount)
        assertEquals(9, stats.charCount)
    }

    @Test
    fun testSmartSnippetsAndDictionary() {
        val addrSnippet = SmartSnippetsAndDictionary.findSnippetByShortcut("/addr")
        assertNotNull(addrSnippet)
        assertTrue(addrSnippet?.content?.contains("تهران") == true)

        SmartSnippetsAndDictionary.addWord("ردلاین سافت البرز", "rsa")
        val expanded = SmartSnippetsAndDictionary.findWordByShortcut("rsa")
        assertEquals("ردلاین سافت البرز", expanded)
    }

    @Test
    fun testLinkAndContactIntelligence() {
        val sample = "آدرس https://example.com و تماس 09121234567 و کارت 6037997512345678"
        val detected = LinkAndContactIntelligence.detectEntities(sample)
        assertTrue(detected.any { it.type == LinkAndContactIntelligence.EntityType.URL })
        assertTrue(detected.any { it.type == LinkAndContactIntelligence.EntityType.PHONE })
        assertTrue(detected.any { it.type == LinkAndContactIntelligence.EntityType.BANK_CARD })
    }

    @Test
    fun testDateTimeIntelligence() {
        val formats = PersianDateTimeIntelligence.getAvailableFormats()
        assertTrue(formats.isNotEmpty())
        assertTrue(formats.any { it.category == "تاریخ" })
        assertTrue(formats.any { it.category == "ساعت" })
    }

    @Test
    fun testThemeManagerAllThemes() {
        assertEquals(10, ThemeManager.ALL_THEMES.size)
        for (theme in ThemeManager.ALL_THEMES) {
            assertNotNull(theme.id)
            assertNotNull(theme.namePersian)
        }
    }
}
