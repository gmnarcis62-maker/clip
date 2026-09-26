package com.example.themes

import androidx.compose.ui.graphics.Color

object ThemeManager {

    val PersianVelvetTheme = KeyboardTheme(
        id = "turquoise",
        namePersian = "فیروزه‌ای مخملی ایرانی",
        description = "اصیل، چشم‌نواز با بافت مات مخملی فیروزه‌ای و لاجوردی",
        isPremium = false,
        backgroundColor = Color(0xFF101722),
        surfaceColor = Color(0xFF172232),
        keyBackgroundColor = Color(0xFF202E42),
        keyGradientBottom = Color(0xFF192535),
        keyTextColor = Color(0xFFFFFFFF),
        keySubTextColor = Color(0xFF8DA4C4),
        specialKeyBackgroundColor = Color(0xFF162232),
        specialKeyTextColor = Color(0xFF00E5D0),
        accentColor = Color(0xFF00D2BE),
        accentTextColor = Color(0xFF061E1A),
        suggestionBarBackgroundColor = Color(0xFF131E2C),
        suggestionTextColor = Color(0xFFE2E8F0),
        suggestionHighlightColor = Color(0xFF00E5D0),
        keyBorderColor = Color(0x3300D2BE),
        keyTopHighlightColor = Color(0x2E00D2BE),
        keyShadowElevationDp = 2.5f,
        isDark = true
    )

    val DarkVelvetTheme = KeyboardTheme(
        id = "onyx",
        namePersian = "زغالی مخملی مات",
        description = "طراحی تاریک مات، کم‌مصرف و فوق‌العاده شیک با حس ابریشمی",
        isPremium = false,
        backgroundColor = Color(0xFF121316),
        surfaceColor = Color(0xFF1A1C22),
        keyBackgroundColor = Color(0xFF282B34),
        keyGradientBottom = Color(0xFF20232B),
        keyTextColor = Color(0xFFF1F3F9),
        keySubTextColor = Color(0xFF888F9E),
        specialKeyBackgroundColor = Color(0xFF1C1E26),
        specialKeyTextColor = Color(0xFFFFB800),
        accentColor = Color(0xFFFFB800),
        accentTextColor = Color(0xFF121212),
        suggestionBarBackgroundColor = Color(0xFF171920),
        suggestionTextColor = Color(0xFFDDE1EB),
        suggestionHighlightColor = Color(0xFFFFB800),
        keyBorderColor = Color(0x22FFFFFF),
        keyTopHighlightColor = Color(0x1FFFFFFF),
        keyShadowElevationDp = 2.5f,
        isDark = true
    )

    val AmoledVelvetTheme = KeyboardTheme(
        id = "amoled",
        namePersian = "مشکی خالص امولد مخملی",
        description = "مشکی مطلق 100% با کلیدهای زغالی عمیق جهت بهینه‌سازی باتری",
        isPremium = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF090909),
        keyBackgroundColor = Color(0xFF181818),
        keyGradientBottom = Color(0xFF101010),
        keyTextColor = Color(0xFFFFFFFF),
        keySubTextColor = Color(0xFF7A7A7A),
        specialKeyBackgroundColor = Color(0xFF0E0E0E),
        specialKeyTextColor = Color(0xFF00FFCC),
        accentColor = Color(0xFF00FFCC),
        accentTextColor = Color(0xFF000000),
        suggestionBarBackgroundColor = Color(0xFF080808),
        suggestionTextColor = Color(0xFFE0E0E0),
        suggestionHighlightColor = Color(0xFF00FFCC),
        keyBorderColor = Color(0x3300FFCC),
        keyTopHighlightColor = Color(0x2400FFCC),
        keyShadowElevationDp = 1.5f,
        isDark = true
    )

    val RoyalVelvetTheme = KeyboardTheme(
        id = "golden",
        namePersian = "طلایی سلطنتی مخملی",
        description = "شکوه طلا بر بستر زغالی و بافت مخمل اشرافی",
        isPremium = true,
        backgroundColor = Color(0xFF16140F),
        surfaceColor = Color(0xFF221F17),
        keyBackgroundColor = Color(0xFF332D20),
        keyGradientBottom = Color(0xFF292419),
        keyTextColor = Color(0xFFFFF8E7),
        keySubTextColor = Color(0xFFB8A680),
        specialKeyBackgroundColor = Color(0xFF242016),
        specialKeyTextColor = Color(0xFFFFD700),
        accentColor = Color(0xFFFFC72C),
        accentTextColor = Color(0xFF1A1408),
        suggestionBarBackgroundColor = Color(0xFF1B1812),
        suggestionTextColor = Color(0xFFFFF0D0),
        suggestionHighlightColor = Color(0xFFFFD700),
        keyBorderColor = Color(0x40FFD700),
        keyTopHighlightColor = Color(0x33FFD700),
        keyShadowElevationDp = 3f,
        isDark = true
    )

    val MidnightVelvetTheme = KeyboardTheme(
        id = "lapis",
        namePersian = "لاجوردی شب مخملی",
        description = "آبی عمیق شاهانه البرز با حس آرامش و ارگونومی بالا",
        isPremium = false,
        backgroundColor = Color(0xFF0B111E),
        surfaceColor = Color(0xFF141D30),
        keyBackgroundColor = Color(0xFF23324E),
        keyGradientBottom = Color(0xFF1B273E),
        keyTextColor = Color(0xFFF8FAFC),
        keySubTextColor = Color(0xFF94A3B8),
        specialKeyBackgroundColor = Color(0xFF172238),
        specialKeyTextColor = Color(0xFF60A5FA),
        accentColor = Color(0xFF3B82F6),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFF11192B),
        suggestionTextColor = Color(0xFFE2E8F0),
        suggestionHighlightColor = Color(0xFF60A5FA),
        keyBorderColor = Color(0x263B82F6),
        keyTopHighlightColor = Color(0x2460A5FA),
        keyShadowElevationDp = 2.5f,
        isDark = true
    )

    val RubyVelvetTheme = KeyboardTheme(
        id = "ruby",
        namePersian = "یاقوتی درباری مخملی",
        description = "جذابیت و گرمای رنگ یاقوت سرخ با بافت مخمل درباری",
        isPremium = true,
        backgroundColor = Color(0xFF1C0D13),
        surfaceColor = Color(0xFF2A141D),
        keyBackgroundColor = Color(0xFF3E1C2B),
        keyGradientBottom = Color(0xFF301421),
        keyTextColor = Color(0xFFFFF0F5),
        keySubTextColor = Color(0xFFD694A8),
        specialKeyBackgroundColor = Color(0xFF2B121C),
        specialKeyTextColor = Color(0xFFFF4D79),
        accentColor = Color(0xFFE11D48),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFF240E17),
        suggestionTextColor = Color(0xFFFFE4E6),
        suggestionHighlightColor = Color(0xFFFF4D79),
        keyBorderColor = Color(0x33FF4D79),
        keyTopHighlightColor = Color(0x29FF4D79),
        keyShadowElevationDp = 2.5f,
        isDark = true
    )

    val MinimalVelvetTheme = KeyboardTheme(
        id = "minimal",
        namePersian = "مینیمال روشن مخملی",
        description = "طراحی روشن، تمیز، چشم‌نواز و فوق‌العاده خوانا با بافت نرم",
        isPremium = false,
        backgroundColor = Color(0xFFECEFF3),
        surfaceColor = Color(0xFFDFE4EC),
        keyBackgroundColor = Color(0xFFFFFFFF),
        keyGradientBottom = Color(0xFFF5F7FA),
        keyTextColor = Color(0xFF0F172A),
        keySubTextColor = Color(0xFF64748B),
        specialKeyBackgroundColor = Color(0xFFD0D7E2),
        specialKeyTextColor = Color(0xFF0F172A),
        accentColor = Color(0xFF0284C7),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFFDFE4EB),
        suggestionTextColor = Color(0xFF1E293B),
        suggestionHighlightColor = Color(0xFF0284C7),
        keyBorderColor = Color(0x1A000000),
        keyTopHighlightColor = Color(0x44FFFFFF),
        keyShadowElevationDp = 2f,
        isDark = false
    )

    val CyberVelvetTheme = KeyboardTheme(
        id = "cyber",
        namePersian = "سایبر نئون مخملی",
        description = "طراحی فیوچریستیک با نورهای نئونی ارغوانی و فیروزه‌ای",
        isPremium = true,
        backgroundColor = Color(0xFF0B0916),
        surfaceColor = Color(0xFF161228),
        keyBackgroundColor = Color(0xFF231C3E),
        keyGradientBottom = Color(0xFF1A1430),
        keyTextColor = Color(0xFF00FFFF),
        keySubTextColor = Color(0xFFFF007F),
        specialKeyBackgroundColor = Color(0xFF16112C),
        specialKeyTextColor = Color(0xFFFF007F),
        accentColor = Color(0xFFFF007F),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFF120D22),
        suggestionTextColor = Color(0xFFE0E7FF),
        suggestionHighlightColor = Color(0xFF00FFFF),
        keyBorderColor = Color(0x5500FFFF),
        keyTopHighlightColor = Color(0x3D00FFFF),
        keyShadowElevationDp = 3f,
        isDark = true
    )

    val GlassVelvetTheme = KeyboardTheme(
        id = "glass",
        namePersian = "شیشه‌ای کریستال مخملی",
        description = "افکت شیشه مات مدرن با حاشیه‌های کریستالی نورانی و بافت مخملی",
        isPremium = true,
        backgroundColor = Color(0xFF111726),
        surfaceColor = Color(0xFF182236),
        keyBackgroundColor = Color(0x80293959),
        keyGradientBottom = Color(0x601F2C46),
        keyTextColor = Color(0xFFFFFFFF),
        keySubTextColor = Color(0xFF9FB2D0),
        specialKeyBackgroundColor = Color(0x551E2B45),
        specialKeyTextColor = Color(0xFF7DD3FC),
        accentColor = Color(0xFF38BDF8),
        accentTextColor = Color(0xFF0C1929),
        suggestionBarBackgroundColor = Color(0x66162238),
        suggestionTextColor = Color(0xFFF0F9FF),
        suggestionHighlightColor = Color(0xFF38BDF8),
        keyBorderColor = Color(0x4DFFFFFF),
        keyTopHighlightColor = Color(0x38FFFFFF),
        keyShadowElevationDp = 2.5f,
        isDark = true
    )

    val EmeraldVelvetTheme = KeyboardTheme(
        id = "emerald",
        namePersian = "زمردی ایرانی مخملی",
        description = "ترکیب زمردی آرامش‌بخش با لایه‌های نعنایی و جنگلی با حس مخملین",
        isPremium = true,
        backgroundColor = Color(0xFF091611),
        surfaceColor = Color(0xFF0F221A),
        keyBackgroundColor = Color(0xFF173428),
        keyGradientBottom = Color(0xFF11281E),
        keyTextColor = Color(0xFFECFDF5),
        keySubTextColor = Color(0xFF6EE7B7),
        specialKeyBackgroundColor = Color(0xFF0E221A),
        specialKeyTextColor = Color(0xFF34D399),
        accentColor = Color(0xFF10B981),
        accentTextColor = Color(0xFF062117),
        suggestionBarBackgroundColor = Color(0xFF0C1D16),
        suggestionTextColor = Color(0xFFD1FAE5),
        suggestionHighlightColor = Color(0xFF34D399),
        keyBorderColor = Color(0x3334D399),
        keyTopHighlightColor = Color(0x2B34D399),
        keyShadowElevationDp = 2.5f,
        isDark = true
    )

    val ALL_THEMES = listOf(
        PersianVelvetTheme,
        DarkVelvetTheme,
        AmoledVelvetTheme,
        RoyalVelvetTheme,
        MidnightVelvetTheme,
        RubyVelvetTheme,
        MinimalVelvetTheme,
        CyberVelvetTheme,
        GlassVelvetTheme,
        EmeraldVelvetTheme
    )

    fun getThemeById(id: String): KeyboardTheme {
        return ALL_THEMES.find { it.id == id } ?: PersianVelvetTheme
    }
}
