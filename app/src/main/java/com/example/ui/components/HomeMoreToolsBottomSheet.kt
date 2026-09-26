package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryCyan

data class HomeExtraTool(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMoreToolsBottomSheet(
    onDismissRequest: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToEmoji: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToUnitConverter: () -> Unit,
    onNavigateToTextTools: () -> Unit,
    onNavigateToKaomoji: () -> Unit,
    onNavigateToDateTime: () -> Unit,
    onNavigateToIntro: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val extraTools = listOf(
        HomeExtraTool(
            "دیکشنری هوشمند",
            "معنی، مترادف، ترجمه و مثال",
            Icons.Default.MenuBook,
            Color(0xFF10B981),
            onNavigateToDictionary
        ),
        HomeExtraTool(
            "مرکز ایموجی و اکسپرشن",
            "ایموجی، شکلک و نمادهای ویژه",
            Icons.Default.EmojiEmotions,
            Color(0xFFFFB800),
            onNavigateToEmoji
        ),
        HomeExtraTool(
            "ماشین حساب هوشمند",
            "محاسبه سریع و درج نتیجه",
            Icons.Default.Calculate,
            Color(0xFF06B6D4),
            onNavigateToCalculator
        ),
        HomeExtraTool(
            "تبدیل واحدها",
            "طول، وزن، دما و حجم",
            Icons.Default.SwapHoriz,
            Color(0xFF8B5CF6),
            onNavigateToUnitConverter
        ),
        HomeExtraTool(
            "جعبه ابزار متن",
            "شمارش، معکوس، مرتب‌سازی",
            Icons.Default.TextFields,
            Color(0xFFEC4899),
            onNavigateToTextTools
        ),
        HomeExtraTool(
            "شکلک و نمادها",
            "Kaomoji و نشانه‌های ویژه",
            Icons.Default.Mood,
            Color(0xFFF97316),
            onNavigateToKaomoji
        ),
        HomeExtraTool(
            "تاریخ و ساعت",
            "تاریخ شمسی و میلادی",
            Icons.Default.DateRange,
            Color(0xFF3B82F6),
            onNavigateToDateTime
        ),
        HomeExtraTool(
            "راهنما و معرفی",
            "آموزش کار با کیبورد مرسانا",
            Icons.Default.TipsAndUpdates,
            PrimaryCyan,
            onNavigateToIntro
        ),
        HomeExtraTool(
            "حریم خصوصی و قوانین",
            "جزئیات قوانین و سیاست‌های حفظ اطلاعات",
            Icons.Default.Policy,
            Color(0xFF64748B),
            onNavigateToPrivacy
        )
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "سایر ابزارها و امکانات کیبورد مرسانا",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ابزارهای کاربردی و پیشرفته برای تایپ هوشمندتر",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(extraTools) { tool ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.5.dp, tool.accentColor.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismissRequest()
                                    tool.onClick()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(tool.accentColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = tool.icon,
                                        contentDescription = null,
                                        tint = tool.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tool.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = tool.description,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}