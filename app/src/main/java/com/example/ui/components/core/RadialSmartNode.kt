package com.example.ui.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SmartNodeItem(
    val id: String,
    val title: String,
    val codeName: String,
    val icon: ImageVector,
    val neonColor: Color,
    val isDualTone: Boolean = false,
    val testTag: String,
    val onClick: () -> Unit
)

@Composable
fun RadialSmartNode(
    node: SmartNodeItem,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.91f else 1.0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .scale(pressScale)
            .width(76.dp)
            .semantics {
                role = Role.Button
                contentDescription = "${node.title} - ${node.codeName}"
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                node.onClick()
            }
            .testTag(node.testTag)
    ) {
        // High-Tech Circular Glass Node Body
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient Neon Halo behind node
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                node.neonColor.copy(alpha = 0.28f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Outer Concentric Ring
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            width = 1.2f.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    node.neonColor.copy(alpha = 0.85f),
                                    Color(0xFF1E293B),
                                    node.neonColor.copy(alpha = 0.85f)
                                )
                            )
                        ),
                        CircleShape
                    )
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF1E293B).copy(alpha = 0.95f),
                                Color(0xFF0F172A),
                                Color(0xFF020617)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner Glass Well
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    node.neonColor.copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(0.8.dp, node.neonColor.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = node.icon,
                        contentDescription = null,
                        tint = node.neonColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Micro Active Status LED on top-right rim
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(node.neonColor)
                        .border(0.5.dp, Color.Black, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Node Persian Label with Glass Badge
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.85f),
            border = BorderStroke(0.5.dp, node.neonColor.copy(alpha = 0.35f))
        ) {
            Text(
                text = node.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF1F5F9),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
