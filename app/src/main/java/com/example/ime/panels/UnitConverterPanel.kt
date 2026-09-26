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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.PersianNumberIntelligence
import com.example.domain.smart.UnitConverterEngine
import com.example.themes.KeyboardTheme

@Composable
fun UnitConverterPanel(
    theme: KeyboardTheme,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    val categories = UnitConverterEngine.UnitCategory.values()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val currentCategory = categories[selectedCategoryIndex]

    var inputValueText by remember { mutableStateOf("1") }

    val fromUnitId = when (currentCategory) {
        UnitConverterEngine.UnitCategory.LENGTH -> "m"
        UnitConverterEngine.UnitCategory.WEIGHT -> "kg"
        UnitConverterEngine.UnitCategory.TEMPERATURE -> "C"
        UnitConverterEngine.UnitCategory.VOLUME -> "l"
        UnitConverterEngine.UnitCategory.SPEED -> "kmh"
        UnitConverterEngine.UnitCategory.AREA -> "m2"
        UnitConverterEngine.UnitCategory.TIME -> "min"
    }

    val targetUnits = when (currentCategory) {
        UnitConverterEngine.UnitCategory.LENGTH -> UnitConverterEngine.LENGTH_UNITS.filter { it.id != fromUnitId }
        UnitConverterEngine.UnitCategory.WEIGHT -> UnitConverterEngine.WEIGHT_UNITS.filter { it.id != fromUnitId }
        UnitConverterEngine.UnitCategory.VOLUME -> UnitConverterEngine.VOLUME_UNITS.filter { it.id != fromUnitId }
        UnitConverterEngine.UnitCategory.SPEED -> UnitConverterEngine.SPEED_UNITS.filter { it.id != fromUnitId }
        UnitConverterEngine.UnitCategory.AREA -> UnitConverterEngine.AREA_UNITS.filter { it.id != fromUnitId }
        UnitConverterEngine.UnitCategory.TIME -> UnitConverterEngine.TIME_UNITS.filter { it.id != fromUnitId }
        UnitConverterEngine.UnitCategory.TEMPERATURE -> listOf(
            UnitConverterEngine.UnitItem("F", "فارنهایت", "F", 1.0),
            UnitConverterEngine.UnitItem("K", "کلوین", "K", 1.0)
        )
    }

    val parsedVal = PersianNumberIntelligence.toEnglishDigits(inputValueText).toDoubleOrNull() ?: 1.0

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "⚖️ تبدیل سریع واحدها",
                    color = theme.keyTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Categories tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(categories) { idx, cat ->
                val isSelected = idx == selectedCategoryIndex
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) theme.accentColor else theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, if (isSelected) theme.accentColor else theme.keyTopHighlightColor),
                    modifier = Modifier.clickable { selectedCategoryIndex = idx }
                ) {
                    Text(
                        text = cat.titlePersian,
                        color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Value Input Field
        OutlinedTextField(
            value = inputValueText,
            onValueChange = { inputValueText = it },
            placeholder = { Text("مقدار ورودی...", fontSize = 11.sp, color = theme.keySubTextColor) },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentColor,
                unfocusedBorderColor = theme.keyBackgroundColor,
                focusedContainerColor = theme.keyBackgroundColor,
                unfocusedContainerColor = theme.keyBackgroundColor,
                focusedTextColor = theme.keyTextColor,
                unfocusedTextColor = theme.keyTextColor
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Conversion results list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(targetUnits) { target ->
                val result = UnitConverterEngine.convert(currentCategory, parsedVal, fromUnitId, target.id)
                if (result != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInsertText(result.displayText) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = result.displayText,
                                color = theme.keyTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { onInsertText(result.displayText) },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("درج", color = theme.accentTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
