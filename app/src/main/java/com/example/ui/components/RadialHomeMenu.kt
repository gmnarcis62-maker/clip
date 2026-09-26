package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.domain.ime.KeyboardActivationState
import com.example.ui.components.core.CentralSmartCore
import com.example.ui.components.core.CyberCircuitCanvas
import com.example.ui.components.core.RadialSmartNode
import com.example.ui.components.core.SmartNodeItem
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadialHomeMenu(
    activationState: KeyboardActivationState,
    items: List<SmartNodeItem>,
    onCenterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemsProgress = remember { List(items.size) { Animatable(0f) } }
    val centerScale = remember { Animatable(0.75f) }

    LaunchedEffect(Unit) {
        launch {
            centerScale.animateTo(1f, tween(260, easing = FastOutSlowInEasing))
        }
        items.indices.forEach { index ->
            launch {
                itemsProgress[index].animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 220,
                        delayMillis = index * 18,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    val primaryNeonColor = when (activationState) {
        KeyboardActivationState.DEFAULT -> Color(0xFF00E5FF)
        KeyboardActivationState.ENABLED_NOT_DEFAULT -> Color(0xFFFFB800)
        else -> Color(0xFF00E5FF)
    }

    val secondaryNeonColor = when (activationState) {
        KeyboardActivationState.DEFAULT -> Color(0xFFFFB800)
        KeyboardActivationState.ENABLED_NOT_DEFAULT -> Color(0xFF00E5FF)
        else -> Color(0xFFFFB800)
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val minDim = minOf(widthPx, heightPx)

        // Responsive radius calculation for 8 radial nodes
        val radiusPx = (minDim * 0.38f).coerceIn(115f * density.density, 160f * density.density)
        val coreSize = if (maxWidth < 360.dp || maxHeight < 620.dp) 130.dp else 145.dp

        // 1. Procedural High-Tech Cyber Circuit Background Canvas
        CyberCircuitCanvas(
            accentColor = primaryNeonColor,
            secondaryColor = secondaryNeonColor,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Surrounding 8 Radial Smart Nodes (Polar Coordinates)
        val itemCount = items.size
        val angleOffset = -Math.PI / 2.0 // Top centered (-90 degrees)

        items.forEachIndexed { index, node ->
            val angle = angleOffset + (2 * Math.PI * index / itemCount)
            val progress = itemsProgress.getOrElse(index) { remember { Animatable(1f) } }.value

            val xOffsetPx = (cos(angle) * radiusPx * progress).toFloat()
            val yOffsetPx = (sin(angle) * radiusPx * progress).toFloat()

            RadialSmartNode(
                node = node,
                modifier = Modifier
                    .offset { IntOffset(xOffsetPx.toInt(), yOffsetPx.toInt()) }
                    .graphicsLayer {
                        alpha = progress
                        scaleX = 0.65f + (0.35f * progress)
                        scaleY = 0.65f + (0.35f * progress)
                    }
            )
        }

        // 3. Central Multi-Layered Smart Core
        CentralSmartCore(
            activationState = activationState,
            coreSize = coreSize,
            onCoreClick = onCenterClick,
            modifier = Modifier.graphicsLayer {
                scaleX = centerScale.value
                scaleY = centerScale.value
            }
        )
    }
}
