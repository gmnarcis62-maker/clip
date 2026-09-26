package com.example.domain.smart

import android.content.Context
import android.content.SharedPreferences

data class ExpressionCategory(
    val id: String,
    val title: String,
    val icon: String,
    val items: List<String>
)

object ExpressionCenterData {

    private const val PREFS_NAME = "clipbord_expression_favorites"
    private const val KEY_FAVORITES = "fav_items"

    // 15 Comprehensive Categories
    val EMOJI_CATEGORIES = listOf(
        ExpressionCategory(
            id = "general",
            title = "چهره و احساس",
            icon = "😀",
            items = listOf(
                "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "☺️", "😊", "😇",
                "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛",
                "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐", "🤨", "😐", "😑"
            )
        ),
        ExpressionCategory(
            id = "love",
            title = "عشق و قلب",
            icon = "❤️",
            items = listOf(
                "❤️", "🩷", "🧡", "💛", "💚", "💙", "🩵", "💜", "🤎", "🖤", "🩶", "🤍",
                "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟",
                "💌", "💋", "💐", "🌹", "🥀", "🌺", "🌸", "🌷", "💍", "👩‍❤️‍👨", "💑", "👩‍❤️‍💋‍👨"
            )
        ),
        ExpressionCategory(
            id = "funny",
            title = "خنده و فان",
            icon = "😂",
            items = listOf(
                "😂", "🤣", "😆", "🤪", "😝", "🤡", "👻", "😹", "😹", "🙈", "🙉", "🙊",
                "😜", "🤭", "🤠", "🤖", "👽", "💩", "👾", "💃", "🕺", "🤸‍♂️", "🤹‍♀️"
            )
        ),
        ExpressionCategory(
            id = "trending",
            title = "ترند و داغ",
            icon = "🔥",
            items = listOf(
                "🔥", "✨", "💯", "🚀", "💥", "⚡", "🌟", "💫", "🎯", "🏆", "🥇", "👑",
                "💎", "💰", "💸", "📈", "🥂", "🍾", "🎉", "🎊", "🔮", "🧿", "🎬", "🎙️"
            )
        ),
        ExpressionCategory(
            id = "decoration",
            title = "تزیین و شاین",
            icon = "✨",
            items = listOf(
                "✨", "⭐", "🌟", "💫", "✦", "✧", "⋆", "˚", "｡", "°", "🫧", "🪞",
                "🎀", "🪄", "🪭", "🪅", "⚜️", "🕊️", "🎐", "🕯️", "🪔", "💎", "💍"
            )
        ),
        ExpressionCategory(
            id = "emotional",
            title = "احساسی و گریه",
            icon = "🥹",
            items = listOf(
                "🥹", "🥺", "😢", "😭", "😮‍💨", "😔", "😞", "😓", "😥", "💔", "🩹", "🌧️",
                "🌧️", "🥀", "🍂", "🫂", "🫠", "🕊️", "🕯️", "😿", "😣", "😩", "😫", "🥱"
            )
        ),
        ExpressionCategory(
            id = "cool",
            title = "خاص و خفن",
            icon = "😎",
            items = listOf(
                "😎", "🕶️", "😏", "🏎️", "🏍️", "🎸", "🎧", "🛹", "🚬", "🧊", "♟️", "🗡️",
                "🦾", "🥷", "⚡", "🌪️", "🐺", "🦅", "🦁", "🐉", "🖤", "🏁", "🔥", "👑"
            )
        ),
        ExpressionCategory(
            id = "minimal",
            title = "مینیمال و سفید",
            icon = "🤍",
            items = listOf(
                "🤍", "🩶", "⚪", "🔘", "▫️", "◽", "◻️", "⬜", "☁️", "🪨", "🌫️", "🧂",
                "🥛", "🕯️", "🕊️", "🥚", "🏐", "🦢", "📄", "🏳️", "◌", "◦", "·"
            )
        ),
        ExpressionCategory(
            id = "friendly",
            title = "دوستانه و صمیمی",
            icon = "🫶",
            items = listOf(
                "🫶", "🤝", "🙌", "👐", "🤲", "👏", "👋", "🫂", "☕", "🧋", "🍕", "🥪",
                "🥨", "🍻", "🧁", "🍩", "✌️", "🤞", "🤙", "👍", "👌", "🎈", "🎁"
            )
        ),
        ExpressionCategory(
            id = "meme",
            title = "میم و دارک",
            icon = "💀",
            items = listOf(
                "💀", "☠️", "🗿", "🤡", "👀", "👃", "🌚", "🌝", "👺", "👹", "🦇", "🕷️",
                "🕸️", "🪦", "⚰️", "☢️", "☣️", "💣", "🧨", "☕", "🍿", "🐸"
            )
        ),
        ExpressionCategory(
            id = "celebration",
            title = "جشن و تبریک",
            icon = "🎉",
            items = listOf(
                "🎉", "🎊", "🎈", "🎂", "🍰", "🧁", "🎁", "🏆", "🥇", "🥂", "🍾", "🥳",
                "🎆", "🎇", "🪩", "🏅", "🎖️", "💐", "🌺", "🌹", "🍰", "🍫", "🍬", "🍭"
            )
        ),
        ExpressionCategory(
            id = "luxury",
            title = "لوکس و طلایی",
            icon = "👑",
            items = listOf(
                "👑", "💎", "⚜️", "💍", "🥇", "🪙", "💰", "💵", "🏰", "🛥️", "🚁", "🍾",
                "🥂", "🍸", "🕶️", "💼", "💳", "🏛️", "🌟", "✨", "🐆", "⚜️"
            )
        ),
        ExpressionCategory(
            id = "energy",
            title = "انرژی و ورزش",
            icon = "⚡",
            items = listOf(
                "⚡", "🔥", "💪", "🏋️‍♂️", "🏃‍♂️", "🚴‍♂️", "🥊", "⚽", "🏀", "🎾", "🚀", "🎯",
                "🧗‍♂️", "🏄‍♂️", "🏎️", "🏆", "🥇", "💥", "☄️", "🌋", "🌪️", "🦁"
            )
        ),
        ExpressionCategory(
            id = "cute",
            title = "کیوت و بامزه",
            icon = "🌸",
            items = listOf(
                "🌸", "🐰", "🐱", "🐶", "🐻", "🐼", "🐥", "🐣", "🍓", "🍒", "🍑", "🧁",
                "🍭", "🎀", "🧸", "🌷", "🌼", "🌻", "🌈", "🍼", "🧃", "🍧", "🐾"
            )
        ),
        ExpressionCategory(
            id = "dark",
            title = "دارک و گوتیک",
            icon = "🖤",
            items = listOf(
                "🖤", "⬛", "◼️", "◾", "▪️", "⚫", "🕷️", "🕸️", "🦇", "🥀", "🌑", "🌘",
                "🌙", "💀", "☠️", "🗡️", "⛓️", "♟️", "🪦", "⚰️", "🕯️", "Raven", "🪶"
            )
        )
    )

    val KAOMOJI_CATEGORIES = listOf(
        ExpressionCategory(
            id = "happy",
            title = "شادی و لبخند",
            icon = "(•‿•)",
            items = listOf(
                "(｡♥‿♥｡)", "(◕‿◕)", "(◠‿◠)", "(•‿•)",
                "(づ｡◕‿‿◕｡)づ", "\\(★ω★)/", "(✿◠‿◠)",
                "(^‿^)", "٩(◕‿◕)۶", "(≧◡≦)", "(⌒‿⌒)",
                "(っ˘ω˘ς )", "(*^‿^*)", "o(≧▽≦)o"
            )
        ),
        ExpressionCategory(
            id = "love_km",
            title = "محبت و بغل",
            icon = "(♡‿♡)",
            items = listOf(
                "(♥ω♥*)", "(♡‿♡)", "(づ￣ ³￣)づ", "(´ε｀ )♡",
                "( ˘ ³˘)♥", "(´♡‿♡`)", "(⁄ ⁄•⁄ω⁄•⁄ ⁄)",
                "(っ˘з(˘⌣˘ ) ♡", "(♡ﾟ▽ﾟ♡)", "(ღ˘⌣˘ღ)"
            )
        ),
        ExpressionCategory(
            id = "shrug",
            title = "شانه بالا انداختن",
            icon = "¯\\_(ツ)_/¯",
            items = listOf(
                "¯\\_(ツ)_/¯", "┐(‘～` )┌", "╮(─▽─)╭",
                "┐(￣ヘ￣)┌", "╮(︶︿︶)╭", "┐(￣∀￣)┌"
            )
        ),
        ExpressionCategory(
            id = "sad",
            title = "غم و گریه",
            icon = "(╥﹏╥)",
            items = listOf(
                "(╥﹏╥)", "(T_T)", "(╯_╰)", "(ノ_<。)",
                "(;_;)", "(｡•́︿•̀｡)", "(◞‸◟)", "(ಥ﹏ಥ)",
                "(｡T ω T｡)", "((´д｀))"
            )
        ),
        ExpressionCategory(
            id = "animals",
            title = "حیوانات ملوس",
            icon = "ʕ•ᴥ•ʔ",
            items = listOf(
                "(=^･ω･^=)", "(=^･ｪ･^=)", "ʕ•ᴥ•ʔ", "(ᵔᴥᵔ)",
                "₍ᐢ. ̫ .ᐢ₎", "(^・x・^)", "૮ ˶ᵔ ᵕ ᵔ˶ ა",
                "ʕ-ᴥ-ʔ", "(=^‥^=)", "ฅ^•ﻌ•^ฅ"
            )
        ),
        ExpressionCategory(
            id = "battle",
            title = "انرژی و رزم",
            icon = "(ง'̀-'́)ง",
            items = listOf(
                "(ง'̀-'́)ง", "ᕦ(ò_óˇ)ᕤ", "(•̀o•́)ง", "ᕙ(`▽´)ᕗ",
                "٩(╬ʘ益ʘ╬)۶", "(ง •̀_•́)ง", "ᕦ( ͡° ͜ʖ ͡°)ᕤ"
            )
        )
    )

    fun getFavorites(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_FAVORITES, null) ?: emptySet()
        return set.toList()
    }

    fun toggleFavorite(context: Context, item: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_FAVORITES, null)?.toMutableSet() ?: mutableSetOf()
        val isNowFav = if (set.contains(item)) {
            set.remove(item)
            false
        } else {
            set.add(item)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, set).apply()
        return isNowFav
    }

    fun isFavorite(context: Context, item: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_FAVORITES, null) ?: return false
        return set.contains(item)
    }
}
