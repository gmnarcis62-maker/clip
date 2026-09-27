package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryGold

object AboutConstants {
    const val MYKET_DEV_PAGE = "https://myket.ir/developer/dev-36089"
    const val MYKET_APP_SCHEME = "myket://comment?id=red.line.clipbord"
    const val MYKET_APP_FALLBACK = "https://myket.ir/app/red.line.clipbord"
    const val SUPPORT_EMAIL = "gmnarcis@gmail.com"
}

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToIntro: () -> Unit
) {
    val context = LocalContext.current

    fun openMyketReview() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(AboutConstants.MYKET_APP_SCHEME)
                setPackage("ir.mservices.market")
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(AboutConstants.MYKET_APP_FALLBACK))
                )
            }
        } catch (_: Exception) {
            try {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(AboutConstants.MYKET_APP_FALLBACK))
                )
            } catch (_: Exception) {
                Toast.makeText(context, "امکان باز کردن صفحه مایکت وجود ندارد.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openDeveloperPage() {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(AboutConstants.MYKET_DEV_PAGE))
            )
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در باز کردن مرورگر: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSupportEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${AboutConstants.SUPPORT_EMAIL}")
                putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی و بازخورد برنامه مرسانا")
                putExtra(Intent.EXTRA_TEXT, "سلام و احترام،\n\nنظر یا مشکل خود را اینجا بنویسید...\n\n")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "هیچ برنامه ایمیلی روی دستگاه یافت نشد.", Toast.LENGTH_SHORT).show()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "درباره ما",
                    subtitle = "اطلاعات تیم توسعه و پشتیبانی",
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
                    .testTag("about_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF1E293B),
                                            Color(0xFF0F172A)
                                        )
                                    )
                                )
                                .border(2.dp, PrimaryCyan, RoundedCornerShape(22.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = "مرسانا",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "مرسانا",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "کیبورد هوشمند فارسی",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryCyan
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "نسخه ۱.۰ | شماره ساخت: ۱۰۰",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // App Description Card — با فونت خوانا و متن دوستانه
                item {
                    PersianCard(borderColor = PrimaryCyan.copy(alpha = 0.4f)) {
                        Text(
                            text = "درباره‌ی مرسانا",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "مرسانا یک کیبورد هوشمند فارسی است که برای راحتی، سرعت و لذت تایپ روزمره‌ی شما طراحی شده. " +
                                    "با ترکیب هوش مصنوعی پیشرفته، دیکشنری فارسی، کلیپ‌بورد نامحدود، ماشین حساب، تبدیل واحد، " +
                                    "شکلک‌ها، ایموجی و ده‌ها ابزار کاربردی دیگر، تایپ کردن برای شما ساده‌تر، دقیق‌تر و لذت‌بخش‌تر می‌شود.\n\n" +
                                    "همه‌ی امکانات مرسانا به‌صورت رایگان در اختیار شماست تا بدون هیچ محدودیتی از کیبورد خود لذت ببرید.",
                            fontSize = 13.5.sp,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // Developer Info
                item {
                    PersianCard(borderColor = SecondaryGold.copy(alpha = 0.4f)) {
                        Text(
                            text = "توسعه‌دهنده",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "تیم نرم‌افزاری ردلاین سافت البرز",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "مدیریت و سرپرست تیم",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "مهندس مهدی رضایی",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryGold
                        )
                    }
                }

                // Action Buttons
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { openMyketReview() },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_myket_rate"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryGold,
                                contentColor = Color(0xFF1E1A11)
                            )
                        ) {
                            Icon(Icons.Default.Star, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ثبت نظر و ۵ ستاره در مایکت",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = { openDeveloperPage() },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_other_apps"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryCyan,
                                contentColor = Color(0xFF07211E)
                            )
                        ) {
                            Icon(Icons.Default.Shop, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "دیگر برنامه‌های ما در مایکت",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { sendSupportEmail() },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("btn_support_email"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Email, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ارتباط با پشتیبانی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToIntro,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_app_intro"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "معرفی و راهنمای کامل مرسانا",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToPrivacy,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_privacy_policy"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Security, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "حریم خصوصی و امنیت داده‌ها",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Footer
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ساخته‌شده با ❤️ در ایران",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}