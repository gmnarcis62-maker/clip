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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "حریم خصوصی و امنیت",
                    subtitle = "تعهد ما به حفظ امنیت و اطلاعات شما",
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
                    .testTag("privacy_policy_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Guarantee Badge
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = "حریم خصوصی شما اولویت اول ماست",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "مرسانا هیچ‌گونه اطلاعات شخصی یا کلیدهای تایپ‌شده را به سروری ارسال نمی‌کند.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 1. Local Processing
                item {
                    PersianCard {
                        SectionTitle(title = "۱. پردازش ۱۰۰٪ آفلاین و محلی", icon = Icons.Default.Security)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "تمام امکانات تایپ، پیش‌بینی کلمات، اصلاح خودکار و مدیریت کلیپ‌بورد به صورت مستقیم و درون گوشی شما پردازش می‌شوند و نیازی به اینترنت برای تایپ وجود ندارد.",
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // 2. Passwords Protection
                item {
                    PersianCard {
                        SectionTitle(title = "۲. امنیت فیلدهای رمز عبور", icon = Icons.Default.Lock)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "هنگامی که در فیلدهای پسورد و رمز عبور بانکی یا حساب‌های کاربری تایپ می‌کنید، سیستم پیش‌بینی کلمات و پیشنهاد خودکار به طور کامل غیرفعال می‌شود تا هیچ اثری از گذرواژه‌های شما ذخیره نگردد.",
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // 3. Local Clipboard Storage
                item {
                    PersianCard {
                        SectionTitle(title = "۳. پایگاه داده امن کلیپ‌بورد", icon = Icons.Default.CheckCircle)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "متن‌های کلیپ‌بورد و پیام‌های آماده شما صرفاً درون حافظه داخلی و محافظت‌شده پایگاه‌داده محلی SQLite دستگاه ذخیره شده و فقط از طریق برنامه خود شما در دسترس است.",
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}
