package com.example.domain.smart

import java.text.DecimalFormat

object PersianNumberIntelligence {

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val ARABIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    private val ENGLISH_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    private val UNITS = arrayOf(
        "", "یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه",
        "ده", "یازده", "دوازده", "سیزده", "چهارده", "پانزده", "شانزده", "هفده", "هجده", "نوزده"
    )

    private val TENS = arrayOf(
        "", "", "بیست", "سی", "چهل", "پنجاه", "شصت", "هفتاد", "هشتاد", "نود"
    )

    private val HUNDREDS = arrayOf(
        "", "صد", "دویست", "سیصد", "چهارصد", "پانصد", "ششصد", "هفتصد", "هشتصد", "نهصد"
    )

    private val SCALES = arrayOf(
        "", "هزار", "میلیون", "میلیارد", "تریلیون"
    )

    /**
     * Converts any digits in the string to Persian digits (۰-۹).
     */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            val idx = ENGLISH_DIGITS.indexOf(ch)
            if (idx != -1) {
                sb.append(PERSIAN_DIGITS[idx])
            } else {
                val arIdx = ARABIC_DIGITS.indexOf(ch)
                if (arIdx != -1) {
                    sb.append(PERSIAN_DIGITS[arIdx])
                } else {
                    sb.append(ch)
                }
            }
        }
        return sb.toString()
    }

    /**
     * Converts any Persian/Arabic digits in the string to English digits (0-9).
     */
    fun toEnglishDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            val faIdx = PERSIAN_DIGITS.indexOf(ch)
            if (faIdx != -1) {
                sb.append(ENGLISH_DIGITS[faIdx])
            } else {
                val arIdx = ARABIC_DIGITS.indexOf(ch)
                if (arIdx != -1) {
                    sb.append(ENGLISH_DIGITS[arIdx])
                } else {
                    sb.append(ch)
                }
            }
        }
        return sb.toString()
    }

    /**
     * Formats a number with 3-digit comma separators in Persian (e.g. ۱۲۵,۰۰۰).
     */
    fun formatWithCommas(input: String, usePersianDigits: Boolean = true): String {
        val clean = toEnglishDigits(input).replace(",", "").trim()
        val num = clean.toLongOrNull() ?: return input
        val formatter = DecimalFormat("#,###")
        val formatted = formatter.format(num)
        return if (usePersianDigits) toPersianDigits(formatted) else formatted
    }

    /**
     * Converts a number to Persian words.
     * e.g. 125000 -> صد و بیست و پنج هزار
     */
    fun numberToPersianWords(input: String): String {
        val clean = toEnglishDigits(input).replace(",", "").trim()
        val num = clean.toLongOrNull() ?: return ""

        if (num == 0L) return "صفر"
        if (num < 0) return "منفی " + numberToPersianWords((-num).toString())

        var temp = num
        val groups = mutableListOf<Int>()
        while (temp > 0) {
            groups.add((temp % 1000).toInt())
            temp /= 1000
        }

        val parts = mutableListOf<String>()
        for (i in groups.indices.reversed()) {
            val groupVal = groups[i]
            if (groupVal != 0) {
                val groupText = convertThreeDigitGroup(groupVal)
                val scale = if (i < SCALES.size) SCALES[i] else ""
                if (scale.isNotEmpty()) {
                    parts.add("$groupText $scale")
                } else {
                    parts.add(groupText)
                }
            }
        }

        return parts.joinToString(" و ")
    }

    private fun convertThreeDigitGroup(num: Int): String {
        val parts = mutableListOf<String>()
        val h = num / 100
        val remainder = num % 100

        if (h > 0) {
            parts.add(HUNDREDS[h])
        }

        if (remainder in 1..19) {
            parts.add(UNITS[remainder])
        } else if (remainder >= 20) {
            val t = remainder / 10
            val u = remainder % 10
            parts.add(TENS[t])
            if (u > 0) {
                parts.add(UNITS[u])
            }
        }

        return parts.joinToString(" و ")
    }

    /**
     * Formats number as Persian currency (Toman & Rial).
     */
    fun formatAsToman(input: String): String {
        val withCommas = formatWithCommas(input, usePersianDigits = true)
        return "$withCommas تومان"
    }

    fun formatAsRial(input: String): String {
        val withCommas = formatWithCommas(input, usePersianDigits = true)
        return "$withCommas ریال"
    }
}
