package me.weishu.kernelsu.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme

fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerColors = listOf(
        MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 3000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer translation"
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnimation - 500f, translateAnimation - 500f),
            end = Offset(translateAnimation + 500f, translateAnimation + 500f)
        )
    )
}

@Composable
fun AppListSkeleton() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MiuixTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon skeleton
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            // Title skeleton
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .height(18.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Subtitle skeleton
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(14.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleListSkeleton() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MiuixTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Module Title skeleton
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(20.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Module Version skeleton
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .height(14.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Module Description skeleton
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(14.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        // Switch skeleton
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(28.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}
