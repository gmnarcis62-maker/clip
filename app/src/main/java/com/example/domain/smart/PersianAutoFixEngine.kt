package com.example.domain.smart

object PersianAutoFixEngine {

    // Common typo mapping in Persian writing
    private val COMMON_TYPOS = mapOf(
        "مسول" to "مسئول",
        "مسولیت" to "مسئولیت",
        "مسولان" to "مسئولان",
        "مسولین" to "مسئولین",
        "راجب" to "راجع به",
        "راجب به" to "راجع به",
        "راجعبه" to "راجع به",
        "بابت اینکه" to "به خاطر اینکه",
        "انشاالله" to "ان‌شاءالله",
        "انشالله" to "ان‌شاءالله",
        "ایشالله" to "ان‌شاءالله",
        "ایشالا" to "ان‌شاءالله",
        "ماشاالله" to "ماشاءالله",
        "ماشالله" to "ماشاءالله",
        "حتا" to "حتی",
        "گذارشت" to "گزارش",
        "گذارش" to "گزارش",
        "سپاس گذار" to "سپاسگزار",
        "سپاسگذار" to "سپاسگزار",
        "سپاسگذارم" to "سپاسگزارم",
        "نماز گذار" to "نمازگزار",
        "نمازگذار" to "نمازگزار",
        "خدمتگذار" to "خدمتگزار",
        "خدمت گذار" to "خدمتگزار",
        "قوربانت" to "قربانت",
        "قوربان" to "قربان",
        "خاهش" to "خواهش",
        "خاهر" to "خواهر",
        "خاستن" to "خواستن",
        "توجیح" to "توجیه",
        "ترجیه" to "ترجیح",
        "اصطحکاک" to "اصطکاک",
        "استعفا" to "استعفا",
        "وهله" to "وهله",
        "هول" to "هول",
        "حوله" to "حوله",
        "حول و حوش" to "حول‌وحوش"
    )

    // Prefixes requiring ZWNJ
    private val ZWNJ_PREFIXES = listOf("می", "نمی", "بی")

    // Suffixes requiring ZWNJ
    private val ZWNJ_SUFFIXES = listOf(
        "ها", "های", "هایم", "هایت", "هایش", "هایمان", "هایتان", "هایشان",
        "هام", "هات", "هاش",
        "ام", "ات", "اش", "ای", "اید", "اند",
        "تر", "ترین", "تری",
        "پذیر", "ناپذیر", "طلب", "سازی", "ساز", "گرایی", "کننده", "شناس", "شناسی",
        "آور", "آمیز", "انگیز", "افزا", "افکن", "آسا", "بار", "سنج", "سنجی", "مهر", "نما"
    )

    /**
     * Fixes spelling, spacing, half-spacing, and punctuation in Persian text.
     */
    fun fixText(input: String): String {
        if (input.isBlank()) return input

        var text = input

        // 1. Normalize Arabic characters to Persian
        text = normalizePersianCharacters(text)

        // 2. Fix common typos
        COMMON_TYPOS.forEach { (typo, correct) ->
            val regex = Regex("(?<!\\S)${Regex.escape(typo)}(?!\\S)")
            text = text.replace(regex, correct)
        }

        // 3. Fix prefix half-spacing (می روم -> می‌روم)
        ZWNJ_PREFIXES.forEach { prefix ->
            val regex = Regex("(?<!\\S)$prefix\\s+([\\u0600-\\u06FF]+)")
            text = text.replace(regex) { match ->
                val verb = match.groupValues[1]
                "$prefix\u200C$verb"
            }
        }

        // 4. Fix suffix half-spacing (خانه ها -> خانه‌ها, کتاب هایم -> کتاب‌هایم)
        ZWNJ_SUFFIXES.forEach { suffix ->
            val regex = Regex("([\\u0600-\\u06FF]{2,})\\s+$suffix(?![\\u0600-\\u06FF])")
            text = text.replace(regex) { match ->
                val base = match.groupValues[1]
                "$base\u200C$suffix"
            }
        }

        // 5. Fix punctuation spacing
        text = fixPunctuationSpacing(text)

        // 6. Fix redundant whitespaces
        text = text.replace(Regex("[ \\t]+"), " ").trim()

        return text
    }

    /**
     * Replaces standard space with Zero-Width Non-Joiner if preceded by a known Persian prefix pattern.
     */
    fun shouldConvertSpaceToHalfSpace(textBeforeCursor: String): Boolean {
        val trimmed = textBeforeCursor.trimEnd()
        val lastWord = trimmed.split(" ", "\n", "\t").lastOrNull() ?: return false

        // Check if last word is "می" or "نمی" or "بی"
        if (lastWord in ZWNJ_PREFIXES) return true

        // Check if word ends with letters that commonly take half-space before suffixes
        if (lastWord.endsWith("ه") && lastWord.length >= 3) {
            // e.g. خانه, رفته, بسته
            return false // We let the user type space, but fix when suffix arrives
        }

        return false
    }

    /**
     * Replaces Arabic characters with Persian standard Unicode equivalents.
     */
    fun normalizePersianCharacters(input: String): String {
        return input
            .replace('ي', 'ی')
            .replace('ى', 'ی')
            .replace('ك', 'ک')
            .replace('ة', 'ه')
            .replace('٤', '۴')
            .replace('٥', '۵')
            .replace('٦', '۶')
    }

    /**
     * Normalizes punctuation spacing according to Persian typography rules.
     */
    fun fixPunctuationSpacing(input: String): String {
        var result = input
        // Remove spaces before punctuation
        result = result.replace(Regex("\\s+([،؛؟!:.,])"), "$1")
        // Add single space after punctuation if followed by text (except digits/decimals/urls)
        result = result.replace(Regex("([،؛؟!:!])([\\u0600-\\u06FFA-Za-z])"), "$1 $2")
        // Fix quotes « »
        result = result.replace(Regex("«\\s+"), "«")
        result = result.replace(Regex("\\s+»"), "»")
        return result
    }

    /**
     * Detects repeated consecutive words (e.g. "خیلی خیلی")
     */
    fun detectRepeatedWords(input: String): List<String> {
        val words = input.split(Regex("\\s+"))
        val repeats = mutableListOf<String>()
        for (i in 0 until words.size - 1) {
            if (words[i].length > 1 && words[i].equals(words[i + 1], ignoreCase = true)) {
                repeats.add(words[i])
            }
        }
        return repeats.distinct()
    }
}
