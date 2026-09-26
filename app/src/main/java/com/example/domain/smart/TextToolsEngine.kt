package com.example.domain.smart

object TextToolsEngine {

    data class TextStats(
        val charCount: Int,
        val charCountWithoutSpaces: Int,
        val wordCount: Int,
        val lineCount: Int,
        val paragraphCount: Int
    )

    fun getStats(text: String): TextStats {
        if (text.isEmpty()) {
            return TextStats(0, 0, 0, 0, 0)
        }
        val charCount = text.length
        val charCountWithoutSpaces = text.count { !it.isWhitespace() }
        val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val lines = text.split("\n")
        val paragraphs = text.split(Regex("\n\n+")).filter { it.isNotBlank() }

        return TextStats(
            charCount = charCount,
            charCountWithoutSpaces = charCountWithoutSpaces,
            wordCount = words.size,
            lineCount = lines.size,
            paragraphCount = paragraphs.size
        )
    }

    fun removeExtraSpaces(text: String): String {
        return text.lines().joinToString("\n") { line ->
            line.trim().replace(Regex("[ \\t]+"), " ")
        }
    }

    fun removeEmptyLines(text: String): String {
        return text.lines().filter { it.isNotBlank() }.joinToString("\n")
    }

    fun sortLinesAscending(text: String): String {
        return text.lines().sortedWith(CollatorPersianComparator).joinToString("\n")
    }

    fun sortLinesDescending(text: String): String {
        return text.lines().sortedWith(CollatorPersianComparator.reversed()).joinToString("\n")
    }

    fun reverseText(text: String): String {
        return text.reversed()
    }

    fun toUpperCase(text: String): String {
        return text.uppercase()
    }

    fun toLowerCase(text: String): String {
        return text.lowercase()
    }

    fun toTitleCase(text: String): String {
        return text.split(Regex("\\s+")).joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    fun removeDuplicateLines(text: String): String {
        return text.lines().distinct().joinToString("\n")
    }

    private object CollatorPersianComparator : Comparator<String> {
        override fun compare(o1: String, o2: String): Int {
            return java.text.Collator.getInstance(java.util.Locale("fa", "IR")).compare(o1, o2)
        }
    }
}
