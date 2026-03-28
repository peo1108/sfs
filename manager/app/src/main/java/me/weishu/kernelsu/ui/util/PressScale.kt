package me.weishu.kernelsu.ui.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

/**
 * iOS 26 "Squish" press effect: When pressed, the composable shrinks to [pressedScale]
 * with a bouncy spring animation. On release, it bounces back to 1.0f via [overshootScale].
 */
@Composable
fun Modifier.pressScale(
    pressedScale: Float = 0.94f,
    overshootScale: Float = 1.02f,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    return this
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                scope.launch {
                    scale.animateTo(
                        pressedScale,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
                    )
                }
                val up = waitForUpOrCancellation()
                scope.launch {
                    // Bounce overshoot then settle
                    scale.animateTo(
                        overshootScale,
                        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f)
                    )
                    scale.animateTo(
                        1f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
                    )
                }
            }
        }
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
}
