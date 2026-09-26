package com.example.ui.components.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws high-precision procedural circuit lines, technical ticks, concentric calibration rings,
 * and micro data points inspired by futuristic aerospace/cybernetic control centers.
 */
@Composable
fun CyberCircuitCanvas(
    accentColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "circuit_motion")

    // Very slow, energy-efficient rotation for technical outer rings (60s loop)
    val slowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "slow_rotation"
    )

    // Reverse slow rotation for inner ticks (45s loop)
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    // Subtle breathing pulse for bus lines
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val minDim = minOf(size.width, size.height)

        // 1. Concentric Guide & Calibration Rings
        val r1 = minDim * 0.20f
        val r2 = minDim * 0.28f
        val r3 = minDim * 0.38f
        val r4 = minDim * 0.46f

        // Base background ambient glow ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.12f * pulseAlpha),
                    secondaryColor.copy(alpha = 0.04f * pulseAlpha),
                    Color.Transparent
                ),
                center = center,
                radius = r4 * 1.15f
            ),
            radius = r4 * 1.15f,
            center = center
        )

        // Ultra-thin reference circles
        drawCircle(
            color = accentColor.copy(alpha = 0.15f),
            radius = r1,
            center = center,
            style = Stroke(width = 1f)
        )

        drawCircle(
            color = secondaryColor.copy(alpha = 0.18f),
            radius = r2,
            center = center,
            style = Stroke(
                width = 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f)
            )
        )

        drawCircle(
            color = accentColor.copy(alpha = 0.12f),
            radius = r3,
            center = center,
            style = Stroke(
                width = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 16f, 4f, 16f), 0f)
            )
        )

        drawCircle(
            color = accentColor.copy(alpha = 0.20f),
            radius = r4,
            center = center,
            style = Stroke(
                width = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 12f, 10f, 12f), 0f)
            )
        )

        // 2. Technical Dial Tick Marks (Rotated slowly)
        rotate(slowRotation, center) {
            val totalTicks = 72
            for (i in 0 until totalTicks) {
                val angleRad = Math.toRadians((i * (360.0 / totalTicks)))
                val isMajor = i % 6 == 0
                val tickLen = if (isMajor) 10f else 4.5f
                val innerR = r3 - (if (isMajor) 5f else 2f)
                val outerR = innerR + tickLen

                val startX = center.x + (cos(angleRad) * innerR).toFloat()
                val startY = center.y + (sin(angleRad) * innerR).toFloat()
                val endX = center.x + (cos(angleRad) * outerR).toFloat()
                val endY = center.y + (sin(angleRad) * outerR).toFloat()

                drawLine(
                    color = if (isMajor) secondaryColor.copy(alpha = 0.45f) else accentColor.copy(alpha = 0.20f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (isMajor) 1.8f else 1f,
                    cap = StrokeCap.Round
                )
            }
        }

        // 3. Counter-rotating Segmented Technical Arcs
        rotate(counterRotation, center) {
            drawArc(
                color = secondaryColor.copy(alpha = 0.35f),
                startAngle = 15f,
                sweepAngle = 40f,
                useCenter = false,
                topLeft = Offset(center.x - r3, center.y - r3),
                size = androidx.compose.ui.geometry.Size(r3 * 2, r3 * 2),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            drawArc(
                color = accentColor.copy(alpha = 0.40f),
                startAngle = 135f,
                sweepAngle = 60f,
                useCenter = false,
                topLeft = Offset(center.x - r3, center.y - r3),
                size = androidx.compose.ui.geometry.Size(r3 * 2, r3 * 2),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            drawArc(
                color = secondaryColor.copy(alpha = 0.35f),
                startAngle = 240f,
                sweepAngle = 45f,
                useCenter = false,
                topLeft = Offset(center.x - r3, center.y - r3),
                size = androidx.compose.ui.geometry.Size(r3 * 2, r3 * 2),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }

        // 4. Procedural PCB Bus Lines with 45-degree angle elbows & micro solder pads
        drawCircuitBuses(
            center = center,
            innerRadius = r1 * 1.05f,
            outerRadius = r4 * 0.95f,
            accentColor = accentColor.copy(alpha = 0.28f * pulseAlpha),
            secondaryColor = secondaryColor.copy(alpha = 0.32f * pulseAlpha)
        )
    }
}

/**
 * Draws PCB trace lines with 45-degree angled routing and solder pad dots.
 */
private fun DrawScope.drawCircuitBuses(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    accentColor: Color,
    secondaryColor: Color
) {
    val angles = listOf(25.0, 65.0, 115.0, 155.0, 205.0, 245.0, 295.0, 335.0)

    angles.forEachIndexed { index, baseAngle ->
        val rad1 = Math.toRadians(baseAngle)
        val rad2 = Math.toRadians(baseAngle + if (index % 2 == 0) 12.0 else -12.0)

        val p1X = center.x + (cos(rad1) * innerRadius).toFloat()
        val p1Y = center.y + (sin(rad1) * innerRadius).toFloat()

        val midR = innerRadius + (outerRadius - innerRadius) * 0.45f
        val p2X = center.x + (cos(rad1) * midR).toFloat()
        val p2Y = center.y + (sin(rad1) * midR).toFloat()

        val p3X = center.x + (cos(rad2) * (midR + 18f)).toFloat()
        val p3Y = center.y + (sin(rad2) * (midR + 18f)).toFloat()

        val p4X = center.x + (cos(rad2) * outerRadius).toFloat()
        val p4Y = center.y + (sin(rad2) * outerRadius).toFloat()

        val path = Path().apply {
            moveTo(p1X, p1Y)
            lineTo(p2X, p2Y)
            lineTo(p3X, p3Y)
            lineTo(p4X, p4Y)
        }

        val traceColor = if (index % 2 == 0) accentColor else secondaryColor

        drawPath(
            path = path,
            color = traceColor,
            style = Stroke(width = 1.2f, cap = StrokeCap.Round)
        )

        // Micro Solder Pad Node at the terminus
        drawCircle(
            color = traceColor,
            radius = 2.4f,
            center = Offset(p4X, p4Y)
        )

        // Micro internal chip via node
        drawCircle(
            color = traceColor.copy(alpha = 0.7f),
            radius = 1.5f,
            center = Offset(p2X, p2Y)
        )
    }
}
