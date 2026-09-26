package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.components.SectionTitle
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryGold

data class FeatureHighlight(val title: String, val description: String, val icon: ImageVector)

@Composable
fun AppIntroScreen(
    onBackClick: () -> Unit
) {
    val highlights = listOf(
        FeatureHighlight("تایپ سریع و استاندارد فارسی", "چیدمان اصیل فارسی با پشتیبانی کامل از حروف «گ، چ، پ، ژ» و نیم‌فاصله راحت", Icons.Default.Keyboard),
        FeatureHighlight("کلیپ‌بورد هوشمند و پاسخ‌های سریع", "ذخیره متن‌های کپی‌شده، سنجاق کردن پیام‌های پرکاربرد و دسترسی مستقیم از داخل کیبورد", Icons.Default.ContentPaste),
        FeatureHighlight("پیشنهاد هوشمند و اصلاح خودکار", "شناسایی کلمات مناسب در حین تایپ و رفع خودکار غلط‌های املایی رایج فارسی", Icons.Default.AutoAwesome),
        FeatureHighlight("پوسته‌های چشم‌نواز و متنوع", "۱۰ تم لوکس شامل فیروزه‌ای اصیل، لاجوردی، امولد، سایبر نئون، طلایی و شیشه‌ای", Icons.Default.Palette),
        FeatureHighlight("شکلک‌ها و ایموجی‌های کامل", "دسترسی آسان و طبقه‌بندی شده به صدها شکلک و احساسات متنوع", Icons.Default.EmojiEmotions),
        FeatureHighlight("حفظ کامل حریم خصوصی", "پردازش ۱۰۰٪ آفلاین و محلی بدون ارسال فشرده کلیدها یا داده‌های شخصی به هیچ سروری", Icons.Default.Security)
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "معرفی و راهنمای مرسانا",
                    subtitle = "ویژگی‌ها و قابلیت‌های کاربردی",
                    onBackClick = onBackClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .testTag("app_intro_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Mission Statement Card
                item {
                    PersianCard(borderColor = PrimaryCyan) {
                        Text(
                            text = "درباره صفحه‌کلید فارسی مرسانا",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "مرسانا یک صفحه‌کلید فارسی هوشمند، مدرن و حرفه‌ای برای کاربران ایرانی است؛ ساخته شده برای تایپ سریع‌تر، راحت‌تر و مجهز به دستیار هوش مصنوعی پیشرفته.\n\nبا مرسانا می‌توانید به امکاناتی مانند تایپ سریع، هوش مصنوعی ویرایش متن، تبدیل تصویر به هنر متنی، پیشنهاد هوشمند کلمات، کلیپبورد پیشرفته، ایموجی، پوسته‌های مخملی متنوع و تنظیمات شخصی‌سازی دسترسی داشته باشید.\n\nهدف ما ساخت یک تجربه تایپ فوق‌العاده روان، زیبا و متناسب با نیاز کاربران فارسی‌زبان است.",
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                item {
                    SectionTitle(title = "ویژگی‌های برجسته", icon = Icons.Default.AutoAwesome)
                }

                items(highlights.size) { index ->
                    val item = highlights[index]
                    PersianCard {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryCyan.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(item.icon, null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
