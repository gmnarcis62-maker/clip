package com.example

import com.example.dictionary.data.DictionarySeedData
import com.example.dictionary.engine.FuzzySearchEngine
import com.example.dictionary.engine.PersianTextNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryComprehensiveTest {

    @Test
    fun testPersianTextNormalizer() {
        // Test clean for display
        assertEquals("مسئول", PersianTextNormalizer.cleanForDisplay("مسئول"))
        assertEquals("مَسئُول", PersianTextNormalizer.cleanForDisplay("مَسئُول"))

        // Test normalize stripping aerab and mapping Arabic characters
        assertEquals("مسوول", PersianTextNormalizer.normalize("مَسؤُول"))
        assertEquals("مسیول", PersianTextNormalizer.normalize("مَسئُول"))

        // Test Arabic characters
        assertEquals("کتاب", PersianTextNormalizer.normalize("كتاب"))
        assertEquals("علی", PersianTextNormalizer.normalize("علي"))
    }

    @Test
    fun testFuzzySearchEngine() {
        val distance = FuzzySearchEngine.levenshteinDistance("مسول", "مسئول")
        assertTrue("Distance between 'مسول' and 'مسئول' should be <= 1", distance <= 1)

        val isMatch = FuzzySearchEngine.isFuzzyMatch("مسول", "مسئول")
        assertTrue("Fuzzy match should be true for typo 'مسول' against 'مسئول'", isMatch)

        val isPrefix = FuzzySearchEngine.isFuzzyMatch("پشت", "پشتکار")
        assertTrue("Fuzzy match should recognize prefix", isPrefix)
    }

    @Test
    fun testDictionarySeedDataIntegrity() {
        val entries = DictionarySeedData.INITIAL_ENTRIES
        assertTrue("Dictionary seed entries should not be empty", entries.size >= 15)

        for (entry in entries) {
            assertNotNull("Word must not be null", entry.word)
            assertNotNull("Normalized word must not be null", entry.normalizedWord)
            assertNotNull("Definition must not be null", entry.definition)
            assertTrue("Word must not be blank", entry.word.isNotBlank())
            assertTrue("Definition must not be blank", entry.definition.isNotBlank())
        }

        // Test specific known words
        val masool = entries.find { it.word == "مسئول" }
        assertNotNull("Word 'مسئول' must exist in seed data", masool)
        assertEquals("fa", masool?.language)
        assertTrue(masool?.synonyms?.contains("متصدی") == true)
        assertTrue(masool?.antonyms?.contains("بی‌مسئولیت") == true)
        assertTrue(masool?.englishTranslation?.contains("Responsible") == true)

        val englishWord = entries.find { it.word == "Responsible" }
        assertNotNull("English word 'Responsible' must exist", englishWord)
        assertEquals("en", englishWord?.language)
        assertTrue(englishWord?.englishTranslation?.contains("مسئول") == true)
    }
}
