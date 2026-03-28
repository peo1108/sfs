package me.weishu.kernelsu.ui.util

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * iOS 26 Double Bezel Card modifier.
 *
 * Creates a "volumetric" card with THREE layers of visual depth:
 * 1. Deep shadow underneath → card "floats" above background
 * 2. Outer glow border → GyroGlow dynamic light reflection (1dp width for visibility)
 * 3. Radial glow spot → moving "flashlight" on card surface based on device tilt
 * 4. Top-edge highlight → subtle gradient at top edge for 3D bezel
 *
 * @param shape The card shape
 * @param glowBrush The outer glow border brush (from rememberGyroGlowBrush)
 * @param radialGlow Optional radial glow brush (from rememberGyroRadialGlow) for surface light spot
 * @param shadowElevation Shadow depth
 * @param ambientColor Shadow ambient color
 * @param spotColor Shadow spot color
 */
// Static top highlight brush — never changes, cached once
private val topHighlightBrush = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.22f),
        Color.White.copy(alpha = 0.03f),
        Color.Transparent,
    ),
    startY = 0f,
    endY = 80f
)

@Composable
fun Modifier.doubleBezelCard(
    shape: Shape,
    glowBrush: Brush,
    radialGlow: Brush? = null,
    shadowElevation: Dp = 8.dp,
    ambientColor: Color = MiuixTheme.colorScheme.primary.copy(alpha = 0.18f),
    spotColor: Color = MiuixTheme.colorScheme.primary.copy(alpha = 0.12f),
): Modifier {
    return this
        // Layer 1: Deep shadow
        .shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = ambientColor,
            spotColor = spotColor
        )
        .clip(shape)
        // Layer 2+3+4: Draw content with overlays
        .drawWithContent {
            // Draw the actual card content first
            drawContent()

            // Layer 3: Radial glow spot (moving flashlight based on gyro tilt)
            if (radialGlow != null) {
                drawRect(brush = radialGlow, size = size)
            }

            // Layer 4: Top-edge highlight (second bezel - subtle white highlight at top)
            drawRect(
                brush = topHighlightBrush,
                size = size.copy(height = 60.dp.toPx().coerceAtMost(size.height))
            )
        }
        // Layer 2: Outer glow border (1dp for visibility, GyroGlow powered)
        .border(
            width = 1.dp,
            brush = glowBrush,
            shape = shape
        )
}
