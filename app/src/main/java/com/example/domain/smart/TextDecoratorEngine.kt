package com.example.domain.smart

data class DecoratedVariation(
    val title: String,
    val preview: String,
    val styleKey: String
)

object TextDecoratorEngine {

    enum class FrameCategory(val title: String) {
        HEARTS_AND_LOVE("عاشقانه و قلب"),
        STARS_AND_SHINE("ستاره و درخشان"),
        ELEGANT_LINES("خطوط ظریف و شیک"),
        LUXURY_BOX("کادر و قاب لوکس"),
        MINIMAL("مینیمال و ساده"),
        UNICODE_ENGLISH("فونت‌های انگلیسی یونیکد")
    }

    fun decorate(text: String): List<DecoratedVariation> {
        val trimmed = text.trim()
        val safeText = if (trimmed.isEmpty()) "زندگی زیباست" else trimmed
        val list = mutableListOf<DecoratedVariation>()

        // 1. Stars & Sparkles
        list.add(DecoratedVariation("ستاره الماس", "✦ $safeText ✦", "star_diamond"))
        list.add(DecoratedVariation("درخشان دوقلو", "✧･ﾟ: * $safeText *:･ﾟ✧", "sparkle_twin"))
        list.add(DecoratedVariation("بال‌های ستاره", "★彡 $safeText 彡★", "star_wings"))
        list.add(DecoratedVariation("فلکی درخشان", "⋆｡°✩ $safeText ✩°｡⋆", "celestial"))

        // 2. Love & Hearts
        list.add(DecoratedVariation("قلب سفید", "♡ $safeText ♡", "heart_white"))
        list.add(DecoratedVariation("قلب پر", "♥ $safeText ♥", "heart_solid"))
        list.add(DecoratedVariation("کمان قلبی", "♥╣ $safeText ╠♥", "heart_bow"))
        list.add(DecoratedVariation("پرواز قلب‌ها", "ღ(¯`◕‿◕´¯) $safeText (¯`◕‿◕´¯)ღ", "heart_fly"))

        // 3. Elegant Lines & Bars
        list.add(DecoratedVariation("خط دابل ضخیم", "━━━━ $safeText ━━━━", "thick_bar"))
        list.add(DecoratedVariation("خط ظریف و نقطه", "───── ೋ $safeText ೋ ─────", "fine_line"))
        list.add(DecoratedVariation("موج و منحنی", "〜〜〜 $safeText 〜〜〜", "wave_curve"))
        list.add(DecoratedVariation("نقطه پروانه‌ای", "•.¸¸.•*´¨`*•.¸¸.• $safeText •.¸¸.•*´¨`*•.¸¸.•", "dotted_butterfly"))

        // 4. Luxury Boxes & Brackets
        list.add(DecoratedVariation("قاب گوشه‌دار", "╭─ $safeText ─╮\n╰────────╯", "corner_frame"))
        list.add(DecoratedVariation("براکت لوزی", "⟦ ⟡ $safeText ⟡ ⟧", "diamond_bracket"))
        list.add(DecoratedVariation("بلوک یونیکد", "░▒▓█ $safeText █▓▒░", "block_unicode"))
        list.add(DecoratedVariation("تاج و سلطنتی", "👑 $safeText 👑", "crown_royal"))

        // 5. English Unicode Transforms (Only transforms Latin A-Z, a-z, leaving Persian intact)
        if (containsEnglish(safeText)) {
            list.add(DecoratedVariation("Bold Serif", toUnicodeFont(safeText, FontStyle.BOLD_SERIF), "eng_bold_serif"))
            list.add(DecoratedVariation("Italic Serif", toUnicodeFont(safeText, FontStyle.ITALIC_SERIF), "eng_italic_serif"))
            list.add(DecoratedVariation("Bold Italic", toUnicodeFont(safeText, FontStyle.BOLD_ITALIC), "eng_bold_italic"))
            list.add(DecoratedVariation("Script (شکسته)", toUnicodeFont(safeText, FontStyle.SCRIPT), "eng_script"))
            list.add(DecoratedVariation("Bold Script", toUnicodeFont(safeText, FontStyle.BOLD_SCRIPT), "eng_bold_script"))
            list.add(DecoratedVariation("Double-Struck (ریاضی)", toUnicodeFont(safeText, FontStyle.DOUBLE_STRUCK), "eng_double_struck"))
            list.add(DecoratedVariation("Monospace (ماشین تحریر)", toUnicodeFont(safeText, FontStyle.MONOSPACE), "eng_monospace"))
            list.add(DecoratedVariation("Circled (دایره‌ای)", toUnicodeFont(safeText, FontStyle.CIRCLED), "eng_circled"))
        }

        return list
    }

    private fun containsEnglish(text: String): Boolean {
        return text.any { (it in 'a'..'z') || (it in 'A'..'Z') }
    }

    enum class FontStyle {
        BOLD_SERIF,
        ITALIC_SERIF,
        BOLD_ITALIC,
        SCRIPT,
        BOLD_SCRIPT,
        DOUBLE_STRUCK,
        MONOSPACE,
        CIRCLED
    }

    fun toUnicodeFont(text: String, style: FontStyle): String {
        val sb = StringBuilder()
        for (ch in text) {
            val converted = when {
                ch in 'a'..'z' -> convertChar(ch - 'a', isUpper = false, style = style)
                ch in 'A'..'Z' -> convertChar(ch - 'A', isUpper = true, style = style)
                else -> ch.toString()
            }
            sb.append(converted)
        }
        return sb.toString()
    }

    private fun convertChar(index: Int, isUpper: Boolean, style: FontStyle): String {
        return when (style) {
            FontStyle.BOLD_SERIF -> {
                val codePoint = if (isUpper) 0x1D400 + index else 0x1D41A + index
                String(Character.toChars(codePoint))
            }
            FontStyle.ITALIC_SERIF -> {
                val codePoint = if (isUpper) 0x1D434 + index else 0x1D44E + index
                String(Character.toChars(codePoint))
            }
            FontStyle.BOLD_ITALIC -> {
                val codePoint = if (isUpper) 0x1D468 + index else 0x1D482 + index
                String(Character.toChars(codePoint))
            }
            FontStyle.SCRIPT -> {
                // Handle specific script codepoint exceptions in Unicode standard
                if (!isUpper) {
                    val codePoint = 0x1D4EA + index
                    String(Character.toChars(codePoint))
                } else {
                    val codePoint = 0x1D4D0 + index
                    String(Character.toChars(codePoint))
                }
            }
            FontStyle.BOLD_SCRIPT -> {
                val codePoint = if (isUpper) 0x1D4D0 + index else 0x1D4EA + index
                String(Character.toChars(codePoint))
            }
            FontStyle.DOUBLE_STRUCK -> {
                val codePoint = if (isUpper) 0x1D538 + index else 0x1D552 + index
                String(Character.toChars(codePoint))
            }
            FontStyle.MONOSPACE -> {
                val codePoint = if (isUpper) 0x1D670 + index else 0x1D68A + index
                String(Character.toChars(codePoint))
            }
            FontStyle.CIRCLED -> {
                val codePoint = if (isUpper) 0x24B6 + index else 0x24D0 + index
                String(Character.toChars(codePoint))
            }
        }
    }
}
