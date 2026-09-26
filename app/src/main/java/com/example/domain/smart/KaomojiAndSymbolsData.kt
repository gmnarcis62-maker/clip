package com.example.domain.smart

data class KaomojiCategory(
    val title: String,
    val items: List<String>
)

data class SymbolCategory(
    val title: String,
    val symbols: List<String>
)

object KaomojiAndSymbolsData {

    val KAOMOJI_CATEGORIES = listOf(
        KaomojiCategory(
            title = "خوشحال و شاد",
            items = listOf(
                "(｡♥‿♥｡)", "(◕‿◕)", "(◠‿◠)", "(•‿•)",
                "(づ｡◕‿‿◕｡)づ", "\\(★ω★)/", "(✿◠‿◠)",
                "(^‿^)", "٩(◕‿◕)۶", "(≧◡≦)", "(⌒‿⌒)"
            )
        ),
        KaomojiCategory(
            title = "تعجب و شانه بالا انداختن",
            items = listOf(
                "¯\\_(ツ)_/¯", "(⊙_⊙)", "(O_O)", "(°ロ°)",
                "┐(‘～` )┌", "(・_・;)", "Σ(°ロ°)", "(°o°:)"
            )
        ),
        KaomojiCategory(
            title = "محبت و عشق",
            items = listOf(
                "(♥ω♥*)", "(♡‿♡)", "(づ￣ ³￣)づ", "(´ε｀ )♡",
                "( ˘ ³˘)♥", "(´♡‿♡`)", "(⁄ ⁄•⁄ω⁄•⁄ ⁄)"
            )
        ),
        KaomojiCategory(
            title = "ناراحت و خسته",
            items = listOf(
                "(╥﹏╥)", "(T_T)", "(╯_╰)", "(ノ_<。)",
                "(;_;)", "(｡•́︿•̀｡)", "(◞‸◟)"
            )
        ),
        KaomojiCategory(
            title = "حیوانات بامزه",
            items = listOf(
                "(=^･ω･^=)", "(=^･ｪ･^=)", "ʕ•ᴥ•ʔ", "(ᵔᴥᵔ)",
                "₍ᐢ. ̫ .ᐢ₎", "(^・x・^)", "૮ ˶ᵔ ᵕ ᵔ˶ ა"
            )
        )
    )

    val SYMBOL_CATEGORIES = listOf(
        SymbolCategory(
            title = "فارسی و اسلامی",
            symbols = listOf(
                "﷼", "«", "»", "ـ", "؛", "،", "؟", "٪", "٫", "٬",
                "﷽", "ﷺ", "ﷻ", "ؑ", "ؒ", "ؓ", "ؐ"
            )
        ),
        SymbolCategory(
            title = "ریاضی و هندسی",
            symbols = listOf(
                "+", "-", "×", "÷", "=", "≠", "≈", "±", "√", "π", "∞",
                "%", "‰", "°", "∆", "∑", "∫", "≤", "≥", "<", ">"
            )
        ),
        SymbolCategory(
            title = "نشانه‌ها و ستاره‌ها",
            symbols = listOf(
                "✓", "✔", "✕", "✖", "★", "☆", "✦", "✧", "♥", "♡",
                "♦", "♠", "♣", "✪", "❂", "❋", "✿", "❀", "※", "•"
            )
        ),
        SymbolCategory(
            title = "فلش‌ها و جهت‌ها",
            symbols = listOf(
                "→", "←", "↑", "↓", "↔", "↕", "⇒", "⇐", "⇑", "⇓",
                "➔", "➜", "▲", "▼", "►", "◄", "▶", "◀"
            )
        ),
        SymbolCategory(
            title = "ارزهای بین‌المللی",
            symbols = listOf(
                "$", "€", "£", "¥", "₩", "₽", "฿", "₺", "₫", "₴", "¢"
            )
        )
    )
}
