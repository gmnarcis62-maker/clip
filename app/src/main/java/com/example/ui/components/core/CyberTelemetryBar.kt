package com.example.ui.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ime.KeyboardActivationState

@Composable
fun CyberTelemetryHeader(
    onVipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand & Cyber Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFFFFB800))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color(0xFF020617),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MERSANA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00E5FF),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "CORE v4.2",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "سیستم تایپ هوشمند و دستیار فارسی",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // High-Tech VIP Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFB800).copy(alpha = 0.12f),
                border = BorderStroke(0.8.dp, Color(0xFFFFB800).copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable { onVipClick() }
                    .testTag("btn_top_cyber_vip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VIP PRO",
                        color = Color(0xFFFFB800),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Real-time Telemetry Stats Ribbon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryMetricChip(
                label = "ENGINE",
                value = "AI PERSIA",
                color = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f)
            )
            TelemetryMetricChip(
                label = "SECURITY",
                value = "100% OFFLINE",
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
            TelemetryMetricChip(
                label = "LATENCY",
                value = "0.8ms",
                color = Color(0xFFFFB800),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TelemetryMetricChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.7f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 7.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF64748B)
            )
            Text(
                text = value,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun CyberTelemetryFooter(
    activationState: KeyboardActivationState,
    onFooterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stateColor = when (activationState) {
        KeyboardActivationState.DEFAULT -> Color(0xFF00E5FF)
        KeyboardActivationState.ENABLED_NOT_DEFAULT -> Color(0xFFFFB800)
        else -> Color(0xFFFF3366)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0B1220).copy(alpha = 0.92f),
        border = BorderStroke(1.dp, stateColor.copy(alpha = 0.45f)),
        shadowElevation = 10.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onFooterClick() }
            .testTag("cyber_telemetry_footer")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Pulsing Status Beacon
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(stateColor)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = when (activationState) {
                            KeyboardActivationState.DEFAULT -> "سیستم کیبورد فعال و متصل است"
                            KeyboardActivationState.ENABLED_NOT_DEFAULT -> "کیبورد فعال است؛ لطفاً انتخاب کنید"
                            else -> "کیبورد در تنظیمات اندروید غیرفعال است"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    Text(
                        text = when (activationState) {
                            KeyboardActivationState.DEFAULT -> "آماده تایپ روان و هوشمند در تمامی برنامه‌ها"
                            KeyboardActivationState.ENABLED_NOT_DEFAULT -> "لمس برای باز کردن پنجره انتخاب کیبورد"
                            else -> "لمس برای مشاهده راهنمای گام‌به‌گام راه‌اندازی"
                        },
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Icon(
                imageVector = when (activationState) {
                    KeyboardActivationState.DEFAULT -> Icons.Default.Keyboard
                    else -> Icons.Default.HelpOutline
                },
                contentDescription = null,
                tint = stateColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}