package com.example.ime.panels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.ActiveImePanel
import com.example.themes.KeyboardTheme

data class ToolHubItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val panel: ActiveImePanel,
    val isSmart: Boolean = false
)

@Composable
fun MoreToolsHubPanel(
    theme: KeyboardTheme,
    onOpenPanel: (ActiveImePanel) -> Unit,
    onClose: () -> Unit
) {
    val items = listOf(
        ToolHubItem("استودیو TextArt", "تبدیل عکس به هنر متنی", Icons.Default.Image, ActiveImePanel.TEXT_ART, isSmart = true),
        ToolHubItem("مرکز ایموجی و اکسپرشن", "۱۵ دسته ایموجی، کاوموجی و خطوط", Icons.Default.EmojiEmotions, ActiveImePanel.EMOJI_CENTER),
        ToolHubItem("زیباساز متن و فونت", "فونت انگلیسی و کادرهای فانتزی", Icons.Default.FormatPaint, ActiveImePanel.TEXT_DECORATOR, isSmart = true),
        ToolHubItem("کپشن و بیو ساز", "کپشن اینستاگرام و متن‌های آماده", Icons.Default.FormatQuote, ActiveImePanel.CAPTION_BIO, isSmart = true),
        ToolHubItem("دیکشنری هوشمند", "معنی، مترادف، ترجمه و مثال", Icons.Default.MenuBook, ActiveImePanel.SMART_DICTIONARY, isSmart = true),
        ToolHubItem("اصلاح هوشمند فارسی", "تصحیح املا و نیم‌فاصله‌ها", Icons.Default.Spellcheck, ActiveImePanel.SMART_WRITING_AUTO_FIX, isSmart = true),
        ToolHubItem("پاسخ‌های سریع", "پیام‌های آماده روزمره", Icons.Default.QuestionAnswer, ActiveImePanel.QUICK_REPLIES, isSmart = true),
        ToolHubItem("تغییر لحن با هوش مصنوعی", "رسمی، صمیمی، کوتاه...", Icons.Default.Tune, ActiveImePanel.SMART_TONE, isSmart = true),
        ToolHubItem("ماشین حساب کیبورد", "محاسبه و درج مستقیم", Icons.Default.Calculate, ActiveImePanel.CALCULATOR),
        ToolHubItem("تاریخ و ساعت شمسی", "درج تقویم و زمان", Icons.Default.CalendarMonth, ActiveImePanel.DATE_TIME),
        ToolHubItem("تبدیل واحدها", "طول، وزن، دما، حجم", Icons.Default.SwapHoriz, ActiveImePanel.UNIT_CONVERTER),
        ToolHubItem("جعبه ابزار متن", "شمارش، معکوس، مرتب‌سازی", Icons.Default.Build, ActiveImePanel.TEXT_TOOLS),
        ToolHubItem("شکلک و نمادها", "Kaomoji و نشانه‌های اسلامی", Icons.Default.Mood, ActiveImePanel.KAOMOJI_SYMBOLS),
        ToolHubItem("ابزارهای مکان‌نما", "جهت‌نما، کپی، پیست PC", Icons.Default.TouchApp, ActiveImePanel.CURSOR_TOOLS),
        ToolHubItem("پوسته و تم", "انتخاب از ۱۰ تم مخملی", Icons.Default.Palette, ActiveImePanel.THEME_PICKER)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ مرکز ابزارها و دستیار هوشمند Clipbord",
                color = theme.keyTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(items) { item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, if (item.isSmart) theme.accentColor.copy(alpha = 0.5f) else theme.keyTopHighlightColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable { onOpenPanel(item.panel) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (item.isSmart) theme.accentColor else theme.specialKeyTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = item.title,
                                color = theme.keyTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = item.description,
                                color = theme.keySubTextColor,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
