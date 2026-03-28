package me.weishu.kernelsu.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun IOSSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    // iOS switch metrics: width 51, height 31, padding 2, thumb 27
    val trackWidth = 51.dp
    val trackHeight = 31.dp
    val thumbSize = 27.dp
    val padding = 2.dp
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f, // iOS bounciness
            stiffness = Spring.StiffnessMedium
        ),
        label = "SwitchProgress"
    )

    val activeColor = MiuixTheme.colorScheme.primary
    val inactiveColor = MiuixTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
    
    val backgroundColor = androidx.compose.ui.graphics.lerp(
        start = inactiveColor,
        stop = activeColor,
        fraction = animatedProgress
    )

    // Calculate max offset: (51 - 27 - 2*2) = 20.dp
    val maxOffset = trackWidth - thumbSize - (padding * 2)

    Box(
        modifier = modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = onCheckedChange != null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange?.invoke(!checked)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        Box(
            modifier = Modifier
                .padding(padding)
                .graphicsLayer {
                    // Use graphicsLayer instead of offset() to avoid relayout each frame
                    translationX = with(density) { maxOffset.toPx() } * animatedProgress
                }
                .size(thumbSize)
                .shadow(
                    elevation = 4.dp, // Soft shadow for depth
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.2f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
