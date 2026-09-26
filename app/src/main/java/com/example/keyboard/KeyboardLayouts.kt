package com.example.keyboard

enum class KeyboardMode {
    TEXT,
    NUMBERS,
    SYMBOLS
}

enum class KeyboardLanguage {
    PERSIAN,
    ENGLISH
}

object KeyboardLayouts {

    // Persian Primary Layout (with dedicated number row at top)
    fun getPersianRows(
        isShifted: Boolean = false,
        usePersianNumbers: Boolean = true,
        showHalfSpace: Boolean = true
    ): List<List<KeyItem>> {
        val persianDigits = listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰")
        val englishDigits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val digits = if (usePersianNumbers) persianDigits else englishDigits

        // Top number row with backspace at the end
        val numberRow = digits.map { digit ->
            KeyItem(label = digit, output = digit, weight = 1.0f)
        } + listOf(
            KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.3f)
        )

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
                    KeyItem(label = ".", output = ".")
                ),
                getBottomRow(isPersian = true, showHalfSpace = showHalfSpace)
            )
        }

        // Row 1: ض ص ث ق ف غ ع ه خ ح ج چ (12 letters)
        val row1Letters = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "چ")
        val row1 = row1Letters.map { char ->
            val popups = when (char) {
                "ه" -> listOf("ة", "ۀ", "ه")
                "ج" -> listOf("[", "{", "ج")
                "چ" -> listOf("]", "}", "چ")
                "ص" -> listOf("ض", "ص")
                "ع" -> listOf("غ", "ع")
                "ف" -> listOf("ق", "ف")
                else -> listOf(char)
            }
            KeyItem(
                label = char,
                output = char,
                popupOptions = popups,
                weight = 1.0f
            )
        }

        // Row 2: ش س ی ب ل ا ت ن م ک گ (11 letters)
        val row2Letters = listOf("ش", "س", "ی", "ب", "ل", "ا", "ت", "ن", "م", "ک", "گ")
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
            KeyItem(
                label = char,
                output = char,
                popupOptions = popups,
                weight = 1.0f
            )
        }

        // Row 3: Shift + ظ ط ز ر د ذ ژ پ و (no backspace here — it's on the number row)
        val row3 = mutableListOf<KeyItem>()
        row3.add(KeyItem(label = "⇧", type = KeyType.SHIFT, weight = 1.5f))

        val row3Letters = listOf(
            KeyItem(label = "ظ", output = "ظ", popupOptions = listOf("ط", "ظ")),
            KeyItem(label = "ط", output = "ط", popupOptions = listOf("ظ", "ط")),
            KeyItem(label = "ز", output = "ز", popupOptions = listOf("ژ", "ز")),
            KeyItem(label = "ر", output = "ر"),
            KeyItem(label = "د", output = "د", popupOptions = listOf("ذ", "د")),
            KeyItem(label = "ذ", output = "ذ"),
            KeyItem(label = "ژ", output = "ژ"),
            KeyItem(label = "پ", output = "پ", popupOptions = listOf("پ", "ب")),
            KeyItem(label = "و", output = "و", popupOptions = listOf("ؤ", "و"))
        )
        row3.addAll(row3Letters)

        return listOf(
            numberRow,
            row1,
            row2,
            row3,
            getBottomRow(isPersian = true, showHalfSpace = showHalfSpace)
        )
    }

    // English Primary Layout (QWERTY Desktop Style)
    fun getEnglishRows(
        isShifted: Boolean = false,
        isCapsLock: Boolean = false
    ): List<List<KeyItem>> {
        val uppercase = isShifted || isCapsLock
        val row1Chars = if (uppercase) listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
        else listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")

        val row2Chars = if (uppercase) listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
        else listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")

        val row3Chars = if (uppercase) listOf("Z", "X", "C", "V", "B", "N", "M")
        else listOf("z", "x", "c", "v", "b", "n", "m")

        val row1 = row1Chars.mapIndexed { index, char ->
            val num = "${(index + 1) % 10}"
            KeyItem(
                label = char,
                subLabel = num,
                output = char,
                popupOptions = listOf(num, char)
            )
        }

        val row2 = row2Chars.map { KeyItem(label = it, output = it) }

        val row3 = mutableListOf<KeyItem>()
        val shiftLabel = if (isCapsLock) "⇪" else "⇧"
        row3.add(KeyItem(label = shiftLabel, type = KeyType.SHIFT, weight = 1.35f))
        row3Chars.forEach { row3.add(KeyItem(label = it, output = it)) }
        row3.add(KeyItem(label = "⌫", type = KeyType.BACKSPACE, weight = 1.35f))

        return listOf(
            row1,
            row2,
            row3,
            getBottomRow(isPersian = false, showHalfSpace = false)
        )
    }

    // Numbers & Desktop Keypad Style Layout
    fun getNumbersRows(
        usePersianDigits: Boolean = true,
        isPersian: Boolean = true
    ): List<List<KeyItem>> {
        val d = if (usePersianDigits) {
            listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰")
        } else {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        }

        // Row 1: 1 2 3 4 5 6 7 8 9 0
        val row1 = d.map { KeyItem(label = it, output = it) }

        // Row 2: @ # $ % & - + ( ) /
        val row2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/").map {
            KeyItem(label = it, output = it)
        }

        // Row 3: =#\ * " ' : ; ! ؟ (or ?) Backspace
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

        // Bottom Row
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

    // Extended Desktop Symbols Layout
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

    private fun getBottomRow(isPersian: Boolean, showHalfSpace: Boolean): List<KeyItem> {
        val row = mutableListOf<KeyItem>()
        row.add(KeyItem(label = if (isPersian) "۱۲۳" else "?123", type = KeyType.MODE_SWITCH, weight = 1.15f))
        row.add(KeyItem(label = if (isPersian) "EN" else "فا", type = KeyType.LANG_SWITCH, weight = 0.95f))
        row.add(KeyItem(label = "😀", type = KeyType.EMOJI, weight = 0.95f))

        if (isPersian && showHalfSpace) {
            row.add(KeyItem(
                label = "نیم‌فاصله",
                subLabel = "‌",
                output = "\u200C",
                type = KeyType.HALF_SPACE,
                weight = 1.4f
            ))
            row.add(KeyItem(
                label = "فاصله",
                type = KeyType.SPACE,
                weight = 3.2f
            ))
        } else {
            row.add(KeyItem(
                label = if (isPersian) "فاصله" else "Space",
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