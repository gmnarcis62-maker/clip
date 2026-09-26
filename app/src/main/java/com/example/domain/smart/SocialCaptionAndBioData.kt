package com.example.domain.smart

data class CaptionTemplate(
    val categoryId: String,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList()
)

data class BioTemplate(
    val typeName: String,
    val headline: String,
    val quote: String,
    val separator: String,
    val details: String,
    val callToAction: String
) {
    fun format(): String {
        return "$headline\n$separator\n$quote\n$details\n$callToAction".trim()
    }
}

data class SeparatorCategory(
    val name: String,
    val icon: String,
    val items: List<String>
)

object SocialCaptionAndBioData {

    val CAPTION_CATEGORIES = listOf(
        "romantic" to "❤️ عاشقانه",
        "elegant" to "✨ باکلاس و شیک",
        "cool" to "😎 خاص و خفن",
        "trendy" to "🔥 ترند و داغ",
        "funny" to "😂 طنز و خودمانی",
        "motivation" to "💪 انگیزشی و هدف",
        "daily" to "🌸 روزمرگی و حال خوب",
        "travel" to "✈️ سفر و ماجراجویی",
        "birthday" to "🎂 تولد و مناسبت",
        "business" to "💼 کسب‌وکار و بیزینس",
        "sales" to "🛍️ فروش و تخفیف",
        "food" to "🍔 کافه و رستوران",
        "fashion" to "👗 مد و استایل"
    )

    val CAPTIONS = listOf(
        CaptionTemplate(
            categoryId = "romantic",
            title = "آرامش در کنار تو",
            content = "در جهان هر چه خوب است، انعکاسی از چشمان توست... ❤️\nبمان برایم که جز تو تمنایی ندارم.",
            tags = listOf("#عاشقانه", "#ارامش", "#عشق")
        ),
        CaptionTemplate(
            categoryId = "romantic",
            title = "نبض زندگی",
            content = "هر لحظه که با تو می‌گذرد، یعنی زندگی به زیباترین شکل ممکن جریان دارد. ✨💍",
            tags = listOf("#عشق_ابدی", "#زندگی")
        ),
        CaptionTemplate(
            categoryId = "elegant",
            title = "سکوت پرمفهوم",
            content = "اصالت یعنی در دنیایی پر از هیاهو، بتوانی با وقار و سکوتت بدرخشی. 💎⚜️",
            tags = listOf("#اصالت", "#وقار", "#لایف_استایل")
        ),
        CaptionTemplate(
            categoryId = "cool",
            title = "مسیر مستقل",
            content = "من قوانین بازی رو عوض نمی‌کنم، بازی رو از اول جوری می‌سازم که قواعدش با من باشه. ⚡🕶️",
            tags = listOf("#قدرت", "#اعتماد_به_نفس")
        ),
        CaptionTemplate(
            categoryId = "trendy",
            title = "انرژی امروز",
            content = "امروز روی ریتم انرژی مثبت و دستاوردهای بزرگ تنظیم شده! بفرست واسه اونی که باید ببینه 🔥🚀",
            tags = listOf("#ترند", "#وایرال", "#انرژی_مثبت")
        ),
        CaptionTemplate(
            categoryId = "funny",
            title = "شنبه‌های همیشگی",
            content = "روابط عمومی بدنم اعلام کرده تا اطلاع ثانوی به علت کمبود قهوه پاسخگوی هیچ تماسی نیست! 😂☕",
            tags = listOf("#طنز", "#قهوه", "#حال_خوب")
        ),
        CaptionTemplate(
            categoryId = "motivation",
            title = "شروع دوباره",
            content = "رویاهات تاریخ انقضا ندارن. یک نفس عمیق بکش و دوباره پرقدرت شروع کن. غیرممکن‌ها فقط تو ذهن کساییه که دست از تلاش کشیدن. 💪🎯",
            tags = listOf("#انگیزه", "#موفقیت", "#هدف")
        ),
        CaptionTemplate(
            categoryId = "daily",
            title = "شکرگزاری روزانه",
            content = "صبح یعنی لبخند بزنی به معجزه یک روز تازه... خدا هست و دلت گرم باشد. 🌸☀️",
            tags = listOf("#روزمرگی", "#شکرگزاری")
        ),
        CaptionTemplate(
            categoryId = "travel",
            title = "ماجراجویی نو",
            content = "سفر کردن تنها چیزیه که براش پول خرج می‌کنی اما پولدارتر و باتجربه‌تر برمی‌گردی! 🌍✈️🎒",
            tags = listOf("#سفر", "#ایرانگردی", "#جهانگردی")
        ),
        CaptionTemplate(
            categoryId = "birthday",
            title = "زادروز فرخنده",
            content = "یک سال بزرگ‌تر، یک سال باتجربه‌تر و سپاسگزار تمام موهبت‌ها... تولدم مبارک! 🎂🎉✨",
            tags = listOf("#تولد", "#تبریک")
        ),
        CaptionTemplate(
            categoryId = "business",
            title = "تخصص و کیفیت",
            content = "اعتماد شما، بزرگ‌ترین سرمایه و تخصص ما بالاترین تعهد ماست. جهت مشاوره و همکاری با ما در ارتباط باشید. 💼📊",
            tags = listOf("#کسب_و_کار", "#برندینگ")
        ),
        CaptionTemplate(
            categoryId = "sales",
            title = "جشنواره تخفیف",
            content = "💥 فرصت طلایی فقط تا پایان این هفته! تخفیف ویژه برای همراهان همیشگی. لینک سفارش در بایو. 🛍️🛒",
            tags = listOf("#تخفیف_ویژه", "#حراج", "#خرید_آنلاین")
        ),
        CaptionTemplate(
            categoryId = "food",
            title = "طعم اصیل",
            content = "یه فنجان قهوه داغ و طعم دلچسبی که خستگی روز رو از تنت بیرون می‌کنه... نوش جان! ☕🍰",
            tags = listOf("#کافه", "#رستوران", "#قهوه")
        ),
        CaptionTemplate(
            categoryId = "fashion",
            title = "استایل مینیمال",
            content = "شیک‌پوشی یعنی سادگی در اوج هماهنگی و تناسب... استایل امروز چطور شده؟ 👗✨",
            tags = listOf("#استایل", "#مد", "#فشن")
        )
    )

    val BIO_PRESETS = listOf(
        BioTemplate(
            typeName = "شخصی و احساسی",
            headline = "🕊️ آرامش در حوالی سادگی",
            quote = "«در سکوت رشد کن و بگذار موفقیتت صدا کند»",
            separator = "────────────",
            details = "📍 تهران | عکاس و نویسنده\n☕ عاشق قهوه و کتاب",
            callToAction = "📩 ارتباط و همکاری دایرکت"
        ),
        BioTemplate(
            typeName = "کسب‌وکار و بیزینس",
            headline = "💼 خدمات تخصصی طراحی و توسعه",
            quote = "ایده‌های شما را به واقعیت دیجیتال تبدیل می‌کنیم",
            separator = "━━━━━━━",
            details = "🌟 بیش از ۵ سال سابقه موفق\n⚡ مشاوره رایگان کسب‌وکار",
            callToAction = "🔗 جهت ثبت سفارش کلیک کنید👇"
        ),
        BioTemplate(
            typeName = "مینیمال و دارک",
            headline = "𝓘𝓷𝓯𝓲𝓷𝓲𝓽𝔂 🖤",
            quote = "Lost in my own thoughts.",
            separator = "• • • • • • • • •",
            details = "Digital Creator | Architect",
            callToAction = "Telegram: @my_channel"
        ),
        BioTemplate(
            typeName = "انگیزشی و ورزشی",
            headline = "🔥 ساختن آینده، همین امروز",
            quote = "درد امروز، قدرت فردای توست!",
            separator = "✦ ───── ✦",
            details = "🏋️‍♂️ مربی فیتنس و تغذیه\n🏆 دارنده مدال قهرمانی",
            callToAction = "📋 دریافت برنامه تمرینی آنلاین"
        )
    )

    val SEPARATORS = listOf(
        SeparatorCategory(
            name = "مینیمال و خطی",
            icon = "─",
            items = listOf(
                "────────────",
                "━━━━━━━━━━━━",
                "┈┈┈┈┈┈┈┈┈┈┈┈",
                "····················",
                "--------------------",
                "–—––—––—––—"
            )
        ),
        SeparatorCategory(
            name = "ستاره و درخشان",
            icon = "✦",
            items = listOf(
                "✦ ────────── ✦",
                "✧･ﾟ: *✧･ﾟ:* *:･ﾟ✧*:･ﾟ✧",
                "⋆｡°✩ ──────── ✩°｡⋆",
                "★彡 ━━━━━━━ 彡★",
                "✧ ════════ ✧",
                "·.¸¸.·♩♪♫ ♫♪♩·.¸¸.·"
            )
        ),
        SeparatorCategory(
            name = "عاشقانه و قلب",
            icon = "♡",
            items = listOf(
                "♡ ────────── ♡",
                "♥ ━━━━━━━━━━ ♥",
                "ღ(¯`◕‿◕´¯) ───── (¯`◕‿◕´¯)ღ",
                "ஐ═══════════ஐ",
                "♥╣ ════════ ╠♥"
            )
        ),
        SeparatorCategory(
            name = "لوکس و تشریفاتی",
            icon = "◆",
            items = listOf(
                "◆ ────────── ◆",
                "╭──────────╮\n╰──────────╯",
                "╔══════════╗\n╚══════════╝",
                "❖ ══════════ ❖",
                "⚜️ ──────── ⚜️",
                "░▒▓█ ━━━━━━ █▓▒░"
            )
        ),
        SeparatorCategory(
            name = "گل و طبیعت",
            icon = "🌸",
            items = listOf(
                "🌸 ────────── 🌸",
                "🌿 ━━━━━━━━━━ 🌿",
                "❀ ══════════ ❀",
                "•.¸¸.•*´¨`*•.¸¸.•",
                "🍃 ────────── 🍃"
            )
        )
    )
}
