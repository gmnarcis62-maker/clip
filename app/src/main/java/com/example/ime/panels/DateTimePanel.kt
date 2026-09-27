package com.example.ime.panels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.shamsi.PersianDateUtils
import com.example.themes.KeyboardTheme

data class DateTimeFormatItem(
    val title: String,
    val formattedText: String,
    val category: String
)

@Composable
fun DateTimePanel(
    theme: KeyboardTheme,
    modifier: Modifier = Modifier.fillMaxWidth().height(250.dp),
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    val now = remember { System.currentTimeMillis() }

    val shamsiItems = remember(now) {
        listOf(
            DateTimeFormatItem("تاریخ شمسی (عددی)", PersianDateUtils.formatPersianDate(now), "شمسی"),
            DateTimeFormatItem("تاریخ شمسی با نام ماه", PersianDateUtils.formatPersianDateWithMonthName(now), "شمسی"),
            DateTimeFormatItem("تاریخ و ساعت شمسی", PersianDateUtils.formatPersianDateWithTime(now), "شمسی"),
            DateTimeFormatItem("تاریخ شمسی با روز هفته", PersianDateUtils.formatPersianDateWithWeekday(now), "شمسی"),
            DateTimeFormatItem("فقط ساعت (شمسی)", PersianDateUtils.formatPersianTime(now), "شمسی")
        )
    }

    val miladiItems = remember(now) {
        listOf(
            DateTimeFormatItem("Gregorian (numeric)", PersianDateUtils.formatGregorianDate(now), "میلادی"),
            DateTimeFormatItem("Gregorian with month name", PersianDateUtils.formatGregorianDateWithMonthName(now), "میلادی"),
            DateTimeFormatItem("Gregorian date + time", PersianDateUtils.formatGregorianDateWithTime(now), "میلادی"),
            DateTimeFormatItem("Gregorian with weekday", PersianDateUtils.formatGregorianDateWithWeekday(now), "میلادی"),
            DateTimeFormatItem("Time only (Gregorian)", PersianDateUtils.formatGregorianTime(now), "میلادی")
        )
    }

    Column(
        modifier = modifier
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "📅 درج سریع تاریخ و ساعت",
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                SectionHeader(title = "🇮🇷 هجری شمسی", theme = theme)
            }
            items(shamsiItems) { item ->
                FormatCard(item = item, theme = theme, onInsertText = onInsertText)
            }

            item { Spacer(modifier = Modifier.height(6.dp)) }

            item {
                SectionHeader(title = "🌍 میلادی (Gregorian)", theme = theme)
            }
            items(miladiItems) { item ->
                FormatCard(item = item, theme = theme, onInsertText = onInsertText)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, theme: KeyboardTheme) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = theme.surfaceColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = theme.accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun FormatCard(
    item: DateTimeFormatItem,
    theme: KeyboardTheme,
    onInsertText: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = theme.keyBackgroundColor,
        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInsertText(item.formattedText) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = theme.keySubTextColor,
                    fontSize = 10.sp
                )
                Text(
                    text = item.formattedText,
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = theme.accentColor.copy(alpha = 0.15f),
                border = BorderStroke(0.5.dp, theme.accentColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = item.category,
                    color = theme.accentColor,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}