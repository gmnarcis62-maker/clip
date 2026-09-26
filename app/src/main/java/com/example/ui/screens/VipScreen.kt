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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
    val vipPrice by billingManager.vipPrice.collectAsState()
    val vipTitle by billingManager.vipTitle.collectAsState()

    var isCheckingRestore by remember { mutableStateOf(false) }
    var lastShownMsg by remember { mutableStateOf<String?>(null) }

    // Show status messages exactly once per change
    LaunchedEffect(statusMsg) {
        val msg = statusMsg
        if (!msg.isNullOrBlank() && msg != lastShownMsg) {
            lastShownMsg = msg
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    // Refresh price and connection status whenever the screen opens
    LaunchedEffect(Unit) {
        billingManager.bindToBillingService()
        billingManager.fetchVipProductDetails()
    }

    val perks = listOf(
        VipPerk(
            title = "پوسته‌های اختصاصی و نامحدود",
            description = "دسترسی کامل به همه پوسته‌های Velvet و طرح‌های ویژه و لوکس برای کیبورد.",
            icon = Icons.Default.Palette
        ),
        VipPerk(
            title = "تاریخچه نامحدود کلیپ‌بورد",
            description = "ذخیره بی‌نهایت متن کپی‌شده بدون محدودیت زمانی و تعدادی در حافظه دستگاه.",
            icon = Icons.Default.ContentPaste
        ),
        VipPerk(
            title = "هوش مصنوعی نامحدود",
            description = "استفاده نامحدود از تمام قابلیت‌های هوش مصنوعی مرسانا شامل بازنویسی، ترجمه و خلاصه‌سازی.",
            icon = Icons.Default.AutoAwesome
        ),
        VipPerk(
            title = "حذف کامل تبلیغات",
            description = "تجربه‌ای بدون تبلیغات و مزاحمت با عملکرد سریع‌تر و روان‌تر.",
            icon = Icons.Default.LockOpen
        ),
        VipPerk(
            title = "پشتیبانی اختصاصی VIP",
            description = "پشتیبانی سریع و اختصاصی از کاربران VIP و دسترسی زودتر به قابلیت‌های جدید.",
            icon = Icons.Default.Star
        )
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "اشتراک ویژه (VIP)",
                    subtitle = "دسترسی نامحدود به تمام قابلیت‌های مرسانا",
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
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E1A11),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            Brush.linearGradient(listOf(SecondaryGold, VipGold))
                        )
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
                                text = if (isVip)
                                    "اشتراک ویژه شما (VIP) فعال است.\nاز تمام امکانات بدون محدودیت لذت ببرید."
                                else
                                    "با خرید اشتراک ویژه، تمام قابلیت‌های حرفه‌ای کیبورد هوشمند مرسانا را فعال کنید.",
                                color = if (isVip) Color(0xFF10B981) else Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "مزایای اشتراک ویژه",
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
                                Text(
                                    perk.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    perk.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    if (!isVip) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Live status banner while connecting / purchasing
                            if (billingStatus == BillingStatus.PURCHASING ||
                                billingStatus == BillingStatus.CONNECTING
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = VipGold.copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = VipGold,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = statusMsg ?: "در حال ارتباط با سرویس مایکت...",
                                            color = VipGold,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // If disconnected — show a retry connection button
                            if (billingStatus == BillingStatus.DISCONNECTED ||
                                billingStatus == BillingStatus.FAILED ||
                                billingStatus == BillingStatus.IDLE
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        billingManager.bindToBillingService()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "اتصال دوباره به مایکت",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Price card
                            if (vipPrice != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = VipGold.copy(alpha = 0.10f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, VipGold.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = vipTitle ?: "اشتراک ویژه مرسانا",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = vipPrice ?: "",
                                            color = VipGold,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "قیمت لحظه‌ای از مایکت",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            // Buy button
                            Button(
                                onClick = {
                                    val activity = context as? Activity
                                    if (activity == null) {
                                        Toast.makeText(
                                            context,
                                            "خطا: Activity پیدا نشد",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }
                                    // If not connected, try to bind first
                                    if (billingStatus == BillingStatus.DISCONNECTED ||
                                        billingStatus == BillingStatus.FAILED
                                    ) {
                                        billingManager.bindToBillingService()
                                    }
                                    billingManager.initiatePurchase(activity)
                                },
                                // Only disable while actively purchasing
                                enabled = billingStatus != BillingStatus.PURCHASING,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("btn_buy_vip"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VipGold,
                                    contentColor = Color(0xFF1E1A11),
                                    disabledContainerColor = VipGold.copy(alpha = 0.4f),
                                    disabledContentColor = Color(0xFF1E1A11).copy(alpha = 0.6f)
                                )
                            ) {
                                Icon(Icons.Default.Star, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        billingStatus == BillingStatus.PURCHASING -> "در حال پردازش خرید..."
                                        vipPrice != null -> "خرید اشتراک ویژه — $vipPrice"
                                        else -> "خرید اشتراک ویژه از مایکت"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Restore button
                            OutlinedButton(
                                onClick = {
                                    if (!isCheckingRestore) {
                                        isCheckingRestore = true
                                        billingManager.restorePurchases { _, _ ->
                                            isCheckingRestore = false
                                        }
                                    }
                                },
                                enabled = !isCheckingRestore,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_restore_vip"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isCheckingRestore) "در حال بررسی..." else "بازیابی خرید قبلی",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "شناسه محصول: ${MyketBillingManager.SKU_VIP_PRO}\n" +
                                        "پرداخت از طریق برنامه مایکت انجام می‌شود و پس از تأیید، اشتراک شما بلافاصله فعال می‌گردد.",
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
                                    text = "اشتراک ویژه شما فعال است. از تمام امکانات لذت ببرید.",
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