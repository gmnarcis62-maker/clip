package com.example.themes

import androidx.compose.ui.graphics.Color

object ThemeManager {

    val PersianVelvetTheme = KeyboardTheme(
        id = "turquoise",
        namePersian = "فیروزه‌ای مخملی ایرانی",
        description = "اصیل، چشم‌نواز با بافت مات مخملی فیروزه‌ای و لاجوردی",
        isPremium = false,
        backgroundColor = Color(0xFF0F1720),
        surfaceColor = Color(0xFF17222D),
        keyBackgroundColor = Color(0xFF232F3D),
        keyGradientBottom = Color(0xFF232F3D),
        keyTextColor = Color(0xFFF8FAFC),
        keySubTextColor = Color(0xFF8A9BB0),
        specialKeyBackgroundColor = Color(0xFF1A2532),
        specialKeyTextColor = Color(0xFF5EEAD4),
        accentColor = Color(0xFF14B8A6),
        accentTextColor = Color(0xFF042420),
        suggestionBarBackgroundColor = Color(0xFF131E28),
        suggestionTextColor = Color(0xFFE2E8F0),
        suggestionHighlightColor = Color(0xFF5EEAD4),
        keyBorderColor = Color(0x1A14B8A6),
        keyTopHighlightColor = Color(0x10FFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val DarkVelvetTheme = KeyboardTheme(
        id = "onyx",
        namePersian = "زغالی مخملی مات",
        description = "طراحی تاریک مات، کم‌مصرف و فوق‌العاده شیک با حس ابریشمی",
        isPremium = false,
        backgroundColor = Color(0xFF111114),
        surfaceColor = Color(0xFF1A1B20),
        keyBackgroundColor = Color(0xFF262830),
        keyGradientBottom = Color(0xFF262830),
        keyTextColor = Color(0xFFF1F3F9),
        keySubTextColor = Color(0xFF8890A0),
        specialKeyBackgroundColor = Color(0xFF1C1E26),
        specialKeyTextColor = Color(0xFFE8A33D),
        accentColor = Color(0xFFD97706),
        accentTextColor = Color(0xFF1A0F00),
        suggestionBarBackgroundColor = Color(0xFF16171C),
        suggestionTextColor = Color(0xFFDDE1EB),
        suggestionHighlightColor = Color(0xFFE8A33D),
        keyBorderColor = Color(0x14FFFFFF),
        keyTopHighlightColor = Color(0x0CFFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val AmoledVelvetTheme = KeyboardTheme(
        id = "amoled",
        namePersian = "مشکی خالص امولد مخملی",
        description = "مشکی مطلق 100% با کلیدهای زغالی عمیق جهت بهینه‌سازی باتری",
        isPremium = true,
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF080809),
        keyBackgroundColor = Color(0xFF161718),
        keyGradientBottom = Color(0xFF161718),
        keyTextColor = Color(0xFFFFFFFF),
        keySubTextColor = Color(0xFF7A7D82),
        specialKeyBackgroundColor = Color(0xFF0D0E0F),
        specialKeyTextColor = Color(0xFF2DD4BF),
        accentColor = Color(0xFF14B8A6),
        accentTextColor = Color(0xFF000000),
        suggestionBarBackgroundColor = Color(0xFF070808),
        suggestionTextColor = Color(0xFFE0E0E0),
        suggestionHighlightColor = Color(0xFF2DD4BF),
        keyBorderColor = Color(0x142DD4BF),
        keyTopHighlightColor = Color(0x0AFFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val RoyalVelvetTheme = KeyboardTheme(
        id = "golden",
        namePersian = "طلایی سلطنتی مخملی",
        description = "شکوه طلا بر بستر زغالی و بافت مخمل اشرافی",
        isPremium = true,
        backgroundColor = Color(0xFF14120D),
        surfaceColor = Color(0xFF1E1B14),
        keyBackgroundColor = Color(0xFF2D281C),
        keyGradientBottom = Color(0xFF2D281C),
        keyTextColor = Color(0xFFFDF8EB),
        keySubTextColor = Color(0xFFB0A17A),
        specialKeyBackgroundColor = Color(0xFF211D14),
        specialKeyTextColor = Color(0xFFE5B94E),
        accentColor = Color(0xFFD4A23A),
        accentTextColor = Color(0xFF1A1305),
        suggestionBarBackgroundColor = Color(0xFF181510),
        suggestionTextColor = Color(0xFFFFF0D0),
        suggestionHighlightColor = Color(0xFFE5B94E),
        keyBorderColor = Color(0x1AD4A23A),
        keyTopHighlightColor = Color(0x10FFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val MidnightVelvetTheme = KeyboardTheme(
        id = "lapis",
        namePersian = "لاجوردی شب مخملی",
        description = "آبی عمیق شاهانه البرز با حس آرامش و ارگونومی بالا",
        isPremium = false,
        backgroundColor = Color(0xFF0A0F1A),
        surfaceColor = Color(0xFF131B2B),
        keyBackgroundColor = Color(0xFF202C44),
        keyGradientBottom = Color(0xFF202C44),
        keyTextColor = Color(0xFFF8FAFC),
        keySubTextColor = Color(0xFF8FA0B8),
        specialKeyBackgroundColor = Color(0xFF16202F),
        specialKeyTextColor = Color(0xFF60A5FA),
        accentColor = Color(0xFF3B82F6),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFF101724),
        suggestionTextColor = Color(0xFFE2E8F0),
        suggestionHighlightColor = Color(0xFF60A5FA),
        keyBorderColor = Color(0x143B82F6),
        keyTopHighlightColor = Color(0x0CFFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val RubyVelvetTheme = KeyboardTheme(
        id = "ruby",
        namePersian = "یاقوتی درباری مخملی",
        description = "جذابیت و گرمای رنگ یاقوت سرخ با بافت مخمل درباری",
        isPremium = true,
        backgroundColor = Color(0xFF1A0C11),
        surfaceColor = Color(0xFF26111A),
        keyBackgroundColor = Color(0xFF3A1927),
        keyGradientBottom = Color(0xFF3A1927),
        keyTextColor = Color(0xFFFFF0F5),
        keySubTextColor = Color(0xFFCE8FA1),
        specialKeyBackgroundColor = Color(0xFF28101A),
        specialKeyTextColor = Color(0xFFFB7185),
        accentColor = Color(0xFFE11D48),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFF210D15),
        suggestionTextColor = Color(0xFFFFE4E6),
        suggestionHighlightColor = Color(0xFFFB7185),
        keyBorderColor = Color(0x14E11D48),
        keyTopHighlightColor = Color(0x0CFFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val MinimalVelvetTheme = KeyboardTheme(
        id = "minimal",
        namePersian = "مینیمال روشن مخملی",
        description = "طراحی روشن، تمیز، چشم‌نواز و فوق‌العاده خوانا با بافت نرم",
        isPremium = false,
        backgroundColor = Color(0xFFF1F3F6),
        surfaceColor = Color(0xFFE6E9EF),
        keyBackgroundColor = Color(0xFFFFFFFF),
        keyGradientBottom = Color(0xFFFFFFFF),
        keyTextColor = Color(0xFF0F172A),
        keySubTextColor = Color(0xFF64748B),
        specialKeyBackgroundColor = Color(0xFFDDE2EA),
        specialKeyTextColor = Color(0xFF0F172A),
        accentColor = Color(0xFF0EA5E9),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFFE6E9EF),
        suggestionTextColor = Color(0xFF1E293B),
        suggestionHighlightColor = Color(0xFF0284C7),
        keyBorderColor = Color(0x0F000000),
        keyTopHighlightColor = Color(0x30FFFFFF),
        keyShadowElevationDp = 0f,
        isDark = false
    )

    val CyberVelvetTheme = KeyboardTheme(
        id = "cyber",
        namePersian = "سایبر نئون مخملی",
        description = "طراحی فیوچریستیک با نورهای نئونی ارغوانی و فیروزه‌ای",
        isPremium = true,
        backgroundColor = Color(0xFF0A0814),
        surfaceColor = Color(0xFF14101F),
        keyBackgroundColor = Color(0xFF201A35),
        keyGradientBottom = Color(0xFF201A35),
        keyTextColor = Color(0xFF7DF9FF),
        keySubTextColor = Color(0xFFE879F9),
        specialKeyBackgroundColor = Color(0xFF18132B),
        specialKeyTextColor = Color(0xFFE879F9),
        accentColor = Color(0xFFEC4899),
        accentTextColor = Color(0xFFFFFFFF),
        suggestionBarBackgroundColor = Color(0xFF100C1B),
        suggestionTextColor = Color(0xFFE0E7FF),
        suggestionHighlightColor = Color(0xFF7DF9FF),
        keyBorderColor = Color(0x2A7DF9FF),
        keyTopHighlightColor = Color(0x1A7DF9FF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val GlassVelvetTheme = KeyboardTheme(
        id = "glass",
        namePersian = "شیشه‌ای کریستال مخملی",
        description = "افکت شیشه مات مدرن با حاشیه‌های کریستالی نورانی و بافت مخملی",
        isPremium = true,
        backgroundColor = Color(0xFF101625),
        surfaceColor = Color(0xFF182032),
        keyBackgroundColor = Color(0xFF2A3548),
        keyGradientBottom = Color(0xFF2A3548),
        keyTextColor = Color(0xFFFFFFFF),
        keySubTextColor = Color(0xFFA0B2CC),
        specialKeyBackgroundColor = Color(0xFF1E293B),
        specialKeyTextColor = Color(0xFF7DD3FC),
        accentColor = Color(0xFF38BDF8),
        accentTextColor = Color(0xFF0C1929),
        suggestionBarBackgroundColor = Color(0xFF151D2E),
        suggestionTextColor = Color(0xFFF0F9FF),
        suggestionHighlightColor = Color(0xFF7DD3FC),
        keyBorderColor = Color(0x20FFFFFF),
        keyTopHighlightColor = Color(0x14FFFFFF),
        keyShadowElevationDp = 0f,
        isDark = true
    )

    val EmeraldVelvetTheme = KeyboardTheme(
        id = "emerald",
        namePersian = "زمردی ایرانی مخملی",
        description = "ترکیب زمردی آرامش‌بخش با لایه‌های نعنایی و جنگلی با حس مخملین",
        isPremium = true,
        backgroundColor = Color(0xFF081510),
        surfaceColor = Color(0xFF0E2119),
        keyBackgroundColor = Color(0xFF173225),
        keyGradientBottom = Color(0xFF173225),
        keyTextColor = Color(0xFFECFDF5),
        keySubTextColor = Color(0xFF6EE7B7),
        specialKeyBackgroundColor = Color(0xFF0D2018),
        specialKeyTextColor = Color(0xFF34D399),
        accentColor = Color(0xFF10B981),
        accentTextColor = Color(0xFF062117),
        suggestionBarBackgroundColor = Color(0xFF0B1C15),
        suggestionTextColor = Color(0xFFD1FAE5),
        suggestionHighlightColor = Color(0xFF34D399),
        keyBorderColor = Color(0x1434D399),
        keyTopHighlightColor = Color(0x0CFFFFFF),
        keyShadowElevationDp = 0f,
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