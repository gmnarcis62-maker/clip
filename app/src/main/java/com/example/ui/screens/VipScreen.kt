package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ClipbordApp
import com.example.billing.BillingStatus
import com.example.billing.MyketBillingManager
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.components.PersianGradientButton
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.VipGold

data class VipPerk(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun VipScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = ClipbordApp.instance
    val billingManager = app.billingManager
    val preferences = app.preferences

    val isVip by preferences.isVip.collectAsState(initial = false)
    val billingStatus by billingManager.billingStatus.collectAsState()
    val statusMsg by billingManager.statusMessage.collectAsState()

    var isCheckingRestore by remember { mutableStateOf(false) }

    val perks = listOf(
        VipPerk("بازگشایی همه ۱۰ پوسته لوکس", "دسترسی نامحدود به پوسته‌های امولد، سایبر نئون، طلایی، زمرد، یاقوت و شیشه‌ای", Icons.Default.Palette),
        VipPerk("کلیپبورد پیشرفته و نامحدود", "ذخیره و دسته‌بندی صدها پیام آماده و متن سریع بدون محدودیت", Icons.Default.ContentPaste),
        VipPerk("پیشنهاد هوشمند کلمات پیشرفته", "الگوریتم دقیق‌تر و افزایش سرعت تایپ تا ۳ برابر", Icons.Default.AutoAwesome),
        VipPerk("شخصی‌سازی نامحدود کیبورد", "تنظیم دقیق‌تر ارتفاع، ابعاد، ویبره و استایل‌های اختصاصی", Icons.Default.LockOpen),
        VipPerk("پشتیبانی VIP و ارتقای همیشگی", "دریافت آپدیت‌های آینده و اولویت در پاسخگویی پشتیبانی", Icons.Default.Star)
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "نسخه حرفه‌ای (VIP)",
                    subtitle = "خرید اشتراک دائمی از مایکت",
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
                    .testTag("vip_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Golden VIP Hero Badge
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E1A11),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Brush.linearGradient(listOf(SecondaryGold, VipGold)))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(VipGold.copy(alpha = 0.2f))
                                    .border(1.5.dp, VipGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "VIP",
                                    tint = VipGold,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "مرسانا پرو | Mersana Pro",
                                color = VipGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isVip) "حساب شما ویژه (VIP) است ✓\nتمام امکانات برای شما فعال می‌باشد." else "با ارتقا به نسخه حرفه‌ای از تمام قابلیت‌های ویژه کیبورد بدون محدودیت لذت ببرید.",
                                color = if (isVip) Color(0xFF10B981) else Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Perks List
                item {
                    Text(
                        text = "ویژگی‌ها و مزایای نسخه حرفه‌ای",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(perks.size) { index ->
                    val perk = perks[index]
                    PersianCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VipGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(perk.icon, null, tint = VipGold, modifier = Modifier.size(22.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(perk.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(perk.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Purchase Actions
                item {
                    if (!isVip) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        billingManager.initiatePurchase(activity)
                                    } else {
                                        Toast.makeText(context, "خطا در فراخوانی درگاه پرداخت", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("btn_buy_vip"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VipGold,
                                    contentColor = Color(0xFF1E1A11)
                                )
                            ) {
                                Icon(Icons.Default.Star, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "خرید نسخه حرفه‌ای از مایکت",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    isCheckingRestore = true
                                    billingManager.restorePurchases { success, message ->
                                        isCheckingRestore = false
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_restore_vip"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("بازیابی خرید قبلی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Text(
                                text = "شناسه محصول در مایکت: ${MyketBillingManager.SKU_VIP_PRO}\nپرداخت از طریق درگاه امن پرداخت درون‌برنامه‌ای مایکت انجام می‌شود.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "اشتراک ویژه شما فعال و دائمی است.",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
