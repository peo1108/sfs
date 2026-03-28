package me.weishu.kernelsu.ui.util

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * iOS 26 Double Bezel Card modifier.
 *
 * Creates a "volumetric" card effect with two layers:
 * 1. Outer glow border: GyroGlow-powered dynamic light reflection
 * 2. Inner top-edge highlight: A thin bright line simulating light hitting the top edge
 * 3. Shadow: Deep shadow beneath creating 3D "floating" illusion
 *
 * @param shape The card shape
 * @param glowBrush The outer glow border brush (typically from rememberGyroGlowBrush)
 * @param shadowElevation Shadow depth
 * @param ambientColor Shadow ambient color
 * @param spotColor Shadow spot color
 */
@Composable
fun Modifier.doubleBezelCard(
    shape: Shape,
    glowBrush: Brush,
    shadowElevation: Dp = 8.dp,
    ambientColor: Color = MiuixTheme.colorScheme.primary.copy(alpha = 0.18f),
    spotColor: Color = MiuixTheme.colorScheme.primary.copy(alpha = 0.12f),
): Modifier {
    // Inner bezel: top-edge light highlight brush
    val topHighlightBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.30f),
            Color.White.copy(alpha = 0.05f),
            Color.Transparent,
        ),
        startY = 0f,
        endY = 80f
    )

    return this
        // Layer 1: Deep shadow for 3D floating effect
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = ambientColor,
            spotColor = spotColor
        )
        .clip(shape)
        // Layer 2: Outer glow border (GyroGlow dynamic)
        .border(
            width = 0.5.dp,
            brush = glowBrush,
            shape = shape
        )
        // Layer 3: Inner top-edge highlight (second bezel)
        .drawWithContent {
            drawContent()
            // Draw a subtle top highlight gradient overlay
            drawRect(
                brush = topHighlightBrush,
                size = size.copy(height = 60.dp.toPx().coerceAtMost(size.height))
            )
        }
}
