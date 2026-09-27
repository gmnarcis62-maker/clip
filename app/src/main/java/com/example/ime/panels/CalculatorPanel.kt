package com.example.ime.panels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.smart.KeyboardCalculatorEngine
import com.example.themes.KeyboardTheme

enum class CalcMode {
    STANDARD,
    SCIENTIFIC,
    HISTORY
}

data class CalcHistoryItem(
    val expression: String,
    val result: String
)

@Composable
fun CalculatorPanel(
    theme: KeyboardTheme,
    modifier: Modifier = Modifier.fillMaxWidth().height(265.dp),
    onInsertResult: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var expression by remember { mutableStateOf("") }
    var currentMode by remember { mutableStateOf(CalcMode.STANDARD) }
    val historyItems = remember {
        mutableStateListOf(
            CalcHistoryItem("1250000 × 3", "3,750,000"),
            CalcHistoryItem("18000000 ÷ 12", "1,500,000")
        )
    }

    val calcResult = remember(expression) {
        KeyboardCalculatorEngine.evaluate(expression)
    }

    val standardButtons = listOf(
        listOf("C", "(", ")", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "⌫", "=")
    )

    val scientificButtons = listOf(
        listOf("sin", "cos", "tan", "√"),
        listOf("π", "^", "²", "³"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", "⌫", "=")
    )

    Column(
        modifier = modifier
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (currentMode == CalcMode.STANDARD) theme.accentColor else theme.keyBackgroundColor,
                    modifier = Modifier.clickable { currentMode = CalcMode.STANDARD }
                ) {
                    Text(
                        text = "استاندارد",
                        color = if (currentMode == CalcMode.STANDARD) theme.accentTextColor else theme.keySubTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (currentMode == CalcMode.SCIENTIFIC) theme.accentColor else theme.keyBackgroundColor,
                    modifier = Modifier.clickable { currentMode = CalcMode.SCIENTIFIC }
                ) {
                    Text(
                        text = "مهندسی",
                        color = if (currentMode == CalcMode.SCIENTIFIC) theme.accentTextColor else theme.keySubTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (currentMode == CalcMode.HISTORY) theme.accentColor else theme.keyBackgroundColor,
                    modifier = Modifier.clickable { currentMode = CalcMode.HISTORY }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = if (currentMode == CalcMode.HISTORY) theme.accentTextColor else theme.keySubTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "تاریخچه",
                            color = if (currentMode == CalcMode.HISTORY) theme.accentTextColor else theme.keySubTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = theme.keyBackgroundColor,
            border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        color = theme.keySubTextColor,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    Text(
                        text = if (calcResult.success) calcResult.formattedResultWithCommas else "",
                        color = theme.accentColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    if (calcResult.success && calcResult.persianWords.isNotBlank()) {
                        Text(
                            text = calcResult.persianWords,
                            color = theme.keySubTextColor,
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                    }
                }

                if (calcResult.success) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            cm?.setPrimaryClip(ClipData.newPlainText("calc_result", calcResult.formattedResult))
                            Toast.makeText(context, "نتیجه کپی شد", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "کپی", tint = theme.keyTextColor, modifier = Modifier.size(13.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = {
                            historyItems.add(0, CalcHistoryItem(expression, calcResult.formattedResultWithCommas))
                            onInsertResult(calcResult.formattedResult)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("درج در متن", color = theme.accentTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (currentMode) {
            CalcMode.HISTORY -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(theme.keyBackgroundColor, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(historyItems) { item ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = theme.surfaceColor,
                            border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expression = item.expression
                                    currentMode = CalcMode.STANDARD
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item.expression, color = theme.keySubTextColor, fontSize = 12.sp)
                                Text(text = item.result, color = theme.accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            CalcMode.STANDARD -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    standardButtons.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            row.forEach { btn ->
                                val isOperator = btn in listOf("÷", "×", "-", "+", "=")
                                val isSpecial = btn in listOf("C", "(", ")", "⌫")

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when {
                                        btn == "=" -> theme.accentColor
                                        isOperator -> theme.specialKeyBackgroundColor
                                        isSpecial -> theme.specialKeyBackgroundColor
                                        else -> theme.keyBackgroundColor
                                    },
                                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            when (btn) {
                                                "C" -> expression = ""
                                                "⌫" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                                "=" -> {
                                                    if (calcResult.success) {
                                                        historyItems.add(0, CalcHistoryItem(expression, calcResult.formattedResultWithCommas))
                                                        expression = calcResult.formattedResult
                                                    }
                                                }
                                                else -> expression += btn
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (btn == "⌫") {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = null,
                                                tint = theme.keyTextColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Text(
                                                text = btn,
                                                color = if (btn == "=") theme.accentTextColor else if (isOperator) theme.accentColor else theme.keyTextColor,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CalcMode.SCIENTIFIC -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    scientificButtons.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            row.forEach { btn ->
                                val isOperator = btn in listOf("÷", "×", "-", "+", "=")
                                val isFunc = btn in listOf("sin", "cos", "tan", "√", "π", "^", "²", "³")

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when {
                                        btn == "=" -> theme.accentColor
                                        isOperator -> theme.specialKeyBackgroundColor
                                        isFunc -> theme.specialKeyBackgroundColor
                                        else -> theme.keyBackgroundColor
                                    },
                                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            when (btn) {
                                                "C" -> expression = ""
                                                "⌫" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                                "=" -> {
                                                    if (calcResult.success) {
                                                        historyItems.add(0, CalcHistoryItem(expression, calcResult.formattedResultWithCommas))
                                                        expression = calcResult.formattedResult
                                                    }
                                                }
                                                "sin", "cos", "tan" -> expression += "$btn("
                                                "√" -> expression += "√("
                                                "²" -> expression += "²"
                                                "³" -> expression += "³"
                                                else -> expression += btn
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (btn == "⌫") {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = null,
                                                tint = theme.keyTextColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        } else {
                                            Text(
                                                text = btn,
                                                color = if (btn == "=") theme.accentTextColor else if (isOperator || isFunc) theme.accentColor else theme.keyTextColor,
                                                fontSize = if (btn.length > 2) 11.sp else 14.sp,
                                                fontWeight = FontWeight.Bold
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
    }
}