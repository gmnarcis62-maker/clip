package com.example.domain.prediction

class SuggestionEngine(
    private val isAutoCorrectionEnabled: Boolean = true,
    private val isSuggestionsEnabled: Boolean = true
) {

    fun getSuggestions(
        currentWord: String,
        previousWord: String? = null,
        isPersian: Boolean = true,
        maxCount: Int = 4
    ): List<String> {
        if (!isSuggestionsEnabled) return emptyList()

        val results = mutableListOf<String>()
        val cleanCurrent = currentWord.trim()

        // 1. Auto-correction check
        if (cleanCurrent.isNotEmpty() && isAutoCorrectionEnabled && isPersian) {
            PersianAutoCorrector.getCorrection(cleanCurrent)?.let { correction ->
                if (correction != cleanCurrent) {
                    results.add(correction)
                }
            }
        }

        // 2. Next-word prediction if current word is empty and previous word exists
        if (cleanCurrent.isEmpty() && !previousWord.isNullOrBlank() && isPersian) {
            val nextWords = PersianDictionary.NEXT_WORD_PREDICTIONS[previousWord.trim()]
            if (nextWords != null) {
                results.addAll(nextWords.take(maxCount))
                return results
            }
        }

        // 3. Prefix matching from vocabulary
        if (cleanCurrent.isNotEmpty()) {
            val wordList = if (isPersian) PersianDictionary.COMMON_PERSIAN_WORDS else PersianDictionary.COMMON_ENGLISH_WORDS
            val matches = wordList.filter { word ->
                word.startsWith(cleanCurrent, ignoreCase = true) && !results.contains(word) && word != cleanCurrent
            }.take(maxCount - results.size)

            results.addAll(matches)
        }

        // 4. Default quick greetings if both are empty
        if (results.isEmpty() && cleanCurrent.isEmpty()) {
            if (isPersian) {
                results.addAll(listOf("سلام", "درود", "خسته نباشید", "خیلی ممنون"))
            } else {
                results.addAll(listOf("Hello", "Thanks", "Please", "Welcome"))
            }
        }

        return results.distinct().take(maxCount)
    }
}
