package com.example.keyboard

enum class KeyboardMode {
    TEXT,
    NUMBERS,
    SYMBOLS,
    BIG_NUMBERS
}

enum class KeyboardLanguage {
    PERSIAN,
    ENGLISH
}

object KeyboardLayouts {

    fun getPersianRows(
        isShifted: Boolean = false,
        usePersianNumbers: Boolean = true,
        showHalfSpace: Boolean = true
    ): List<List<KeyItem>> {
        val persianDigits = listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰")
        val englishDigits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val digits = if (usePersianNumbers) persianDigits else englishDigits

        val numberRow = digits.map { digit ->
            KeyItem(label = digit, output = digit, weight = 1.0f)
        }

        if (isShifted) {
            return listOf(
                numberRow,
                listOf("ً", "ٌ", "ٍ", "َ", "ُ", "ِ", "ّ", "ْ", "ء", "«", "»", "﷼", "٪").map {
                    KeyItem(label = it, output = it)
                },
                listOf("ؤ", "إ", "أ", "آ", "ة", "ك", "ي", "ى", "؛", "،", ":", "!", "؟").map {
                    KeyItem(label = it, output = it)
                },
                listOf(
                    KeyItem(label = "⇧", type = KeyType.SHIFT, weight = 1.3f),
                    KeyItem(label = "ژ", output = "ژ"),
                    KeyItem(label = "ئ", output = "ئ"),
                    KeyItem(label = "ـ", output = "ـ"),
                    KeyItem(label = "[", output = "["),
                    KeyItem(label = "]", output = "]"),
                    KeyItem(label = "{", output = "{"),
                    KeyItem(label = "}", output = "}"),
                    KeyItem(label = "،", output = "،"),
                    KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.3f)
                ),
                getBottomRow(isPersian = true)
            )
        }

        val row1Letters = listOf("ج", "ح", "خ", "ه", "ع", "غ", "ف", "ق", "ث", "ص", "ض")
        val row1 = row1Letters.map { char ->
            val popups = when (char) {
                "ه" -> listOf("ة", "ۀ", "ه")
                "ج" -> listOf("چ", "[", "{", "ج")
                "ص" -> listOf("ض", "ص")
                "ع" -> listOf("غ", "ع")
                "ف" -> listOf("ق", "ف")
                else -> listOf(char)
            }
            KeyItem(label = char, output = char, popupOptions = popups, weight = 1.0f)
        }

        val row2Letters = listOf("گ", "ک", "م", "ن", "ت", "ا", "ل", "ب", "ی", "س", "ش")
        val row2 = row2Letters.map { char ->
            val popups = when (char) {
                "ا" -> listOf("آ", "أ", "إ", "ء", "ا")
                "ی" -> listOf("ئ", "ي", "ى", "ی")
                "ب" -> listOf("پ", "ب")
                "ک" -> listOf("ك", "ک")
                "ت" -> listOf("ة", "ت")
                "ل" -> listOf("لا", "ل")
                "گ" -> listOf("ك", "گ")
                "س" -> listOf("ش", "س")
                else -> emptyList()
            }
            KeyItem(label = char, output = char, popupOptions = popups, weight = 1.0f)
        }

        val row3 = listOf(
            KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.0f),
            KeyItem(label = "چ", output = "چ", popupOptions = listOf("ج", "چ")),
            KeyItem(label = "و", output = "و", popupOptions = listOf("ؤ", "و")),
            KeyItem(label = "پ", output = "پ", popupOptions = listOf("ب", "پ")),
            KeyItem(label = "د", output = "د", popupOptions = listOf("ذ", "د")),
            KeyItem(label = "ذ", output = "ذ", popupOptions = listOf("د", "ذ")),
            KeyItem(label = "ر", output = "ر"),
            KeyItem(label = "ز", output = "ز", popupOptions = listOf("ژ", "ز")),
            KeyItem(label = "ژ", output = "ژ", popupOptions = listOf("ز", "ژ")),
            KeyItem(label = "ط", output = "ط", popupOptions = listOf("ظ", "ط")),
            KeyItem(label = "ظ", output = "ظ", popupOptions = listOf("ط", "ظ"))
        )

        return listOf(
            numberRow,
            row1,
            row2,
            row3,
            getBottomRow(isPersian = true)
        )
    }

    fun getEnglishRows(
        isShifted: Boolean = false,
        isCapsLock: Boolean = false
    ): List<List<KeyItem>> {
        val uppercase = isShifted || isCapsLock

        val numberRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map { num ->
            KeyItem(label = num, output = num, weight = 1.0f)
        }

        val row1Chars = if (uppercase) listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
        else listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")

        val row2Chars = if (uppercase) listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
        else listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")

        val row3Chars = if (uppercase) listOf("Z", "X", "C", "V", "B", "N", "M")
        else listOf("z", "x", "c", "v", "b", "n", "m")

        val row1 = row1Chars.map { KeyItem(label = it, output = it) }
        val row2 = row2Chars.map { KeyItem(label = it, output = it) }

        val row3 = mutableListOf<KeyItem>()
        val shiftLabel = if (isCapsLock) "⇪" else "⇧"
        row3.add(KeyItem(label = shiftLabel, type = KeyType.SHIFT, weight = 1.35f))
        row3Chars.forEach { row3.add(KeyItem(label = it, output = it)) }
        row3.add(KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.35f))

        return listOf(
            numberRow,
            row1,
            row2,
            row3,
            getBottomRow(isPersian = false)
        )
    }

    fun getNumbersRows(
        usePersianDigits: Boolean = true,
        isPersian: Boolean = true
    ): List<List<KeyItem>> {
        val d = if (usePersianDigits) {
            listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰")
        } else {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        }

        val row1 = d.map { KeyItem(label = it, output = it) }

        val row2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/").map {
            KeyItem(label = it, output = it)
        }

        val questionMark = if (isPersian) "؟" else "?"
        val row3 = listOf(
            KeyItem(label = "=#\\", type = KeyType.MODE_SWITCH, weight = 1.3f),
            KeyItem(label = "*", output = "*"),
            KeyItem(label = "\"", output = "\""),
            KeyItem(label = "'", output = "'"),
            KeyItem(label = ":", output = ":"),
            KeyItem(label = ";", output = ";"),
            KeyItem(label = "!", output = "!"),
            KeyItem(label = questionMark, output = questionMark),
            KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.3f)
        )

        val textSwitchLabel = if (isPersian) "حروف" else "ABC"
        val commaChar = if (isPersian) "،" else ","
        val bottomRow = listOf(
            KeyItem(label = textSwitchLabel, type = KeyType.MODE_SWITCH, weight = 1.3f),
            KeyItem(label = "Tab", type = KeyType.TAB, weight = 0.9f),
            KeyItem(label = commaChar, output = commaChar, weight = 0.8f, popupOptions = if (isPersian) listOf("،", ",", "؛") else listOf(",", "،", ";")),
            KeyItem(label = if (isPersian) "فاصله" else "Space", type = KeyType.SPACE, weight = 3.2f),
            KeyItem(label = ".", output = ".", weight = 0.8f, popupOptions = listOf(".", "…", ":", "!")),
            KeyItem(label = "↵", type = KeyType.ENTER, weight = 1.4f)
        )

        return listOf(row1, row2, row3, bottomRow)
    }

    fun getSymbolsRows(
        usePersianDigits: Boolean = true,
        isPersian: Boolean = true
    ): List<List<KeyItem>> {
        val row1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆").map {
            KeyItem(label = it, output = it)
        }
        val row2 = listOf("£", "€", "¥", "﷼", "¢", "^", "°", "=", "{", "}").map {
            KeyItem(label = it, output = it)
        }
        val numSwitchLabel = if (usePersianDigits) "۱۲۳" else "123"
        val row3 = listOf(
            KeyItem(label = numSwitchLabel, type = KeyType.MODE_SWITCH, weight = 1.3f),
            KeyItem(label = "\\", output = "\\"),
            KeyItem(label = "_", output = "_"),
            KeyItem(label = "©", output = "©"),
            KeyItem(label = "®", output = "®"),
            KeyItem(label = "[", output = "["),
            KeyItem(label = "]", output = "]"),
            KeyItem(label = "<", output = "<"),
            KeyItem(label = ">", output = ">"),
            KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        val textSwitchLabel = if (isPersian) "حروف" else "ABC"
        val bottomRow = listOf(
            KeyItem(label = textSwitchLabel, type = KeyType.MODE_SWITCH, weight = 1.3f),
            KeyItem(label = "Tab", type = KeyType.TAB, weight = 0.9f),
            KeyItem(label = if (isPersian) "«" else "<", output = if (isPersian) "«" else "<", weight = 0.8f),
            KeyItem(label = if (isPersian) "فاصله" else "Space", type = KeyType.SPACE, weight = 3.2f),
            KeyItem(label = if (isPersian) "»" else ">", output = if (isPersian) "»" else ">", weight = 0.8f),
            KeyItem(label = "↵", type = KeyType.ENTER, weight = 1.4f)
        )
        return listOf(row1, row2, row3, bottomRow)
    }

    fun getBigNumbersRows(
        usePersianDigits: Boolean = true
    ): List<List<KeyItem>> {
        val d = if (usePersianDigits) {
            listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰")
        } else {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        }

        val row1 = listOf(d[0], d[1], d[2]).map { KeyItem(label = it, output = it, weight = 1f) }
        val row2 = listOf(d[3], d[4], d[5]).map { KeyItem(label = it, output = it, weight = 1f) }
        val row3 = listOf(d[6], d[7], d[8]).map { KeyItem(label = it, output = it, weight = 1f) }
        val row4 = listOf(
            KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1f),
            KeyItem(label = d[9], output = d[9], weight = 1f),
            KeyItem(label = "↵", type = KeyType.ENTER, weight = 1f)
        )

        return listOf(row1, row2, row3, row4)
    }

    /**
     * ✅ ردیف پایین
     * - فارسی: ۱۲۳ | EN | 😀 | . | فاصله (نگه‌داشتن = نیم‌فاصله) | ، | ↵
     * - انگلیسی: ?123 | فا | 😀 | Space | . | ↵
     */
    private fun getBottomRow(isPersian: Boolean): List<KeyItem> {
        val row = mutableListOf<KeyItem>()
        row.add(KeyItem(label = if (isPersian) "۱۲۳" else "?123", type = KeyType.MODE_SWITCH, weight = 1.15f))
        row.add(KeyItem(label = if (isPersian) "EN" else "فا", type = KeyType.LANG_SWITCH, weight = 0.95f))
        row.add(KeyItem(label = "😀", type = KeyType.EMOJI, weight = 0.95f))

        if (isPersian) {
            row.add(KeyItem(
                label = ".",
                output = ".",
                weight = 1.0f,
                popupOptions = listOf(".", "،", "…", ":", "!")
            ))
            // ✅ Space — نگه‌داشتن روی آن، نیم‌فاصله درج می‌کند
            row.add(KeyItem(
                label = "فاصله",
                subLabel = "نیم‌فاصله",
                type = KeyType.SPACE,
                weight = 3.6f
            ))
        } else {
            row.add(KeyItem(
                label = "Space",
                type = KeyType.SPACE,
                weight = 4.4f
            ))
        }

        row.add(KeyItem(
            label = if (isPersian) "،" else ".",
            output = if (isPersian) "،" else ".",
            weight = 0.8f,
            popupOptions = listOf("،", ".", "؟", "!", "؛", ":")
        ))
        row.add(KeyItem(label = "↵", type = KeyType.ENTER, weight = 1.35f))
        return row
    }
}