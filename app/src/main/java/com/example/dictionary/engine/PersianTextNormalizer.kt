package com.example.dictionary.engine

object PersianTextNormalizer {

    private val DIACRITICS_REGEX = Regex("[\\u064B-\\u065F\\u0670]") // Aerab (Fatha, Damma, Kasra, Tanween, Tashdeed, Sukoon, etc.)
    private val TATWEEL_REGEX = Regex("\\u0640") // Kashida / Tatweel

    /**
     * Normalizes text for reliable dictionary search and indexing.
     */
    fun normalize(input: String): String {
        if (input.isBlank()) return ""

        var result = input.trim()

        // 1. Convert Arabic Yeh variants to Persian Yeh
        result = result
            .replace('ي', 'ی')
            .replace('ى', 'ی')
            .replace('ئ', 'ی')
            .replace('إ', 'ا')
            .replace('أ', 'ا')
            .replace('آ', 'ا')
            .replace('ك', 'ک')
            .replace('ة', 'ه')
            .replace('ؤ', 'و')

        // 2. Remove Diacritics (Aerab) & Tatweel
        result = result.replace(DIACRITICS_REGEX, "")
        result = result.replace(TATWEEL_REGEX, "")

        // 3. Remove zero-width non-joiners from normalized key for flexible matching (e.g. مسوول vs مسئول vs مسول)
        result = result.replace("\u200C", "")

        // 4. Normalize multiple whitespaces
        result = result.replace(Regex("\\s+"), " ")

        return result.lowercase().trim()
    }

    /**
     * Light normalization preserving vowels for display/preview.
     */
    fun cleanForDisplay(input: String): String {
        return input
            .replace('ي', 'ی')
            .replace('ك', 'ک')
            .replace('ة', 'ه')
            .replace(TATWEEL_REGEX, "")
            .trim()
    }
}
