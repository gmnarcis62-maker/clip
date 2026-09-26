package com.example.ui.components.core

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ime.KeyboardActivationState

@Composable
fun CentralSmartCore(
    activationState: KeyboardActivationState,
    coreSize: Dp = 150.dp,
    onCoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition(label = "core_visual_fx")

    // Slow rotation for dynamic neon status arcs
    val neonArcRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neon_arc_rotation"
    )

    // Breathing pulse for ambient core lighting
    val coreGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_glow_pulse"
    )

    // Dynamic state palette
    val primaryCoreColor = when (activationState) {
        KeyboardActivationState.DEFAULT -> Color(0xFF00E5FF)       // Vibrant Cyan / Cyber Teal
        KeyboardActivationState.ENABLED_NOT_DEFAULT -> Color(0xFFFFB800) // Vibrant Amber Gold
        KeyboardActivationState.SYSTEM_PROBLEM -> Color(0xFFFF3366)      // Red Alert
        else -> Color(0xFF00E5FF)                                        // Cyan
    }

    val secondaryCoreColor = when (activationState) {
        KeyboardActivationState.DEFAULT -> Color(0xFFFFB800)       // High-tech Dual Tone Gold
        KeyboardActivationState.ENABLED_NOT_DEFAULT -> Color(0xFF00E5FF) // Cyber Cyan
        KeyboardActivationState.SYSTEM_PROBLEM -> Color(0xFFFF8800)
        else -> Color(0xFFFFB800)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.95f else 1.0f

    Box(
        modifier = modifier
            .size(coreSize * 1.55f)
            .semantics {
                role = Role.Button
                contentDescription = when (activationState) {
                    KeyboardActivationState.DEFAULT -> "هسته هوشمند کیبورد مرسانا فعال است. برای آزمایش لمس کنید."
                    KeyboardActivationState.ENABLED_NOT_DEFAULT -> "کیبورد فعال است اما پیش‌فرض نیست. برای انتخاب لمس کنید."
                    else -> "کیبورد غیرفعال است. برای فعال‌سازی لمس کنید."
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // LAYER 1: Deep Ambient Radial Glow Halo
        Box(
            modifier = Modifier
                .size(coreSize * 1.5f)
                .scale(coreGlowPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryCoreColor.copy(alpha = 0.25f),
                            secondaryCoreColor.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // LAYER 2 & 3: Canvas-rendered Technical Dial, Knurled Rings & Calibrated Ticks
        Canvas(
            modifier = Modifier
                .size(coreSize * 1.45f)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width / 2f * 0.94f
            val midRadius = outerRadius * 0.86f
            val innerRadius = outerRadius * 0.72f

            // Metallic Knurled Rim Background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E293B),
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    ),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )

            // Outer Metallic Bezel Ring
            drawCircle(
                color = primaryCoreColor.copy(alpha = 0.35f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // Dotted Knurled Texture Ring
            drawCircle(
                color = Color(0xFF64748B).copy(alpha = 0.25f),
                radius = outerRadius * 0.93f,
                center = center,
                style = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 4f), 0f)
                )
            )

            // Middle Calibrated Tick Circle
            val tickCount = 60
            for (i in 0 until tickCount) {
                val angleRad = Math.toRadians(i * (360.0 / tickCount))
                val isMajor = i % 5 == 0
                val tickLength = if (isMajor) 7f else 3.5f
                val startR = midRadius
                val endR = startR + tickLength

                val startX = center.x + (kotlin.math.cos(angleRad) * startR).toFloat()
                val startY = center.y + (kotlin.math.sin(angleRad) * startR).toFloat()
                val endX = center.x + (kotlin.math.cos(angleRad) * endR).toFloat()
                val endY = center.y + (kotlin.math.sin(angleRad) * endR).toFloat()

                drawLine(
                    color = if (isMajor) primaryCoreColor.copy(alpha = 0.65f) else Color(0xFF94A3B8).copy(alpha = 0.35f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (isMajor) 1.6f else 1f,
                    cap = StrokeCap.Round
                )
            }

            // LAYER 4: Dynamic Neon Dual Arcs (Rotating)
            rotate(neonArcRotation, center) {
                // Primary Neon Arc (Cyan / Teal)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryCoreColor.copy(alpha = 0.1f),
                            primaryCoreColor,
                            primaryCoreColor.copy(alpha = 0.8f)
                        ),
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // Secondary Neon Arc (Amber / Gold)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            secondaryCoreColor.copy(alpha = 0.1f),
                            secondaryCoreColor,
                            secondaryCoreColor.copy(alpha = 0.8f)
                        ),
                        center = center
                    ),
                    startAngle = 180f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                    size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )
            }
        }

        // LAYER 5, 6 & 7: Interactive 3D Glass Action Core
        Surface(
            shape = CircleShape,
            color = Color(0xFF070D18).copy(alpha = 0.92f),
            shadowElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryCoreColor.copy(alpha = 0.85f),
                        Color(0xFF1E293B),
                        secondaryCoreColor.copy(alpha = 0.85f)
                    )
                )
            ),
            modifier = Modifier
                .size(coreSize)
                .scale(pressScale)
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCoreClick()
                }
                .testTag("central_smart_core_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryCoreColor.copy(alpha = 0.22f),
                                Color(0xFF0F172A).copy(alpha = 0.85f),
                                Color(0xFF020617)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Diagonal Glass Specular Reflection Highlight
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        ),
                        startAngle = 210f,
                        sweepAngle = 90f,
                        useCenter = true,
                        topLeft = Offset(8f, 8f),
                        size = androidx.compose.ui.geometry.Size(size.width - 16f, size.height - 16f)
                    )
                }

                // Central Technical Information & Status
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp)
                ) {
                    // Tech Telemetry Top Tag
                    Text(
                        text = when (activationState) {
                            KeyboardActivationState.DEFAULT -> "CORE // 100% READY"
                            KeyboardActivationState.ENABLED_NOT_DEFAULT -> "SELECT // PENDING"
                            else -> "SYSTEM // STANDBY"
                        },
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = primaryCoreColor.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Glowing Action Icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        primaryCoreColor.copy(alpha = 0.28f),
                                        primaryCoreColor.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .border(1.dp, primaryCoreColor.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (activationState) {
                                KeyboardActivationState.DEFAULT -> Icons.Default.CheckCircle
                                KeyboardActivationState.ENABLED_NOT_DEFAULT -> Icons.Default.TouchApp
                                KeyboardActivationState.SYSTEM_PROBLEM -> Icons.Default.Warning
                                else -> Icons.Default.PowerSettingsNew
                            },
                            contentDescription = null,
                            tint = primaryCoreColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Main Persian Action Title
                    Text(
                        text = when (activationState) {
                            KeyboardActivationState.DEFAULT -> "مرسانا آماده است"
                            KeyboardActivationState.ENABLED_NOT_DEFAULT -> "انتخاب کیبورد اصلی"
                            KeyboardActivationState.SYSTEM_PROBLEM -> "بررسی خطا"
                            else -> "فعال‌سازی کیبورد"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // Subtitle / Prompt
                    Text(
                        text = when (activationState) {
                            KeyboardActivationState.DEFAULT -> "برای آزمایش لمس کنید"
                            KeyboardActivationState.ENABLED_NOT_DEFAULT -> "کلیک برای انتخاب IME"
                            KeyboardActivationState.SYSTEM_PROBLEM -> "راهنمای عیب‌یابی"
                            else -> "شروع راه‌اندازی سریع"
                        },
                        fontSize = 9.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
