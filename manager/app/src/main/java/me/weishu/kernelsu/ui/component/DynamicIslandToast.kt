package me.weishu.kernelsu.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import top.yukonga.miuix.kmp.basic.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.weishu.kernelsu.ui.theme.isInDarkTheme

object DynamicIslandManager {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    fun show(message: String) {
        _messages.tryEmit(message)
    }
}

@Composable
fun DynamicIslandOverlay() {
    var currentMessage by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        DynamicIslandManager.messages.collect { msg ->
            currentMessage = msg
            isExpanded = true
            delay(2800)
            isExpanded = false
            delay(400)
            if (currentMessage == msg) {
                currentMessage = null
            }
        }
    }

    // Animated width for the expanding pill effect
    val expandProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 350f),
        label = "islandExpand"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = currentMessage != null,
            enter = slideInVertically(
                animationSpec = spring(dampingRatio = 0.55f, stiffness = 350f),
                initialOffsetY = { -it * 2 }
            ) + fadeIn(
                animationSpec = tween(200)
            ) + scaleIn(
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                initialScale = 0.3f
            ),
            exit = slideOutVertically(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f),
                targetOffsetY = { -it }
            ) + fadeOut(
                animationSpec = tween(250)
            ) + scaleOut(
                animationSpec = tween(200),
                targetScale = 0.6f
            )
        ) {
            currentMessage?.let { msg ->
                val isDark = isInDarkTheme()
                val bgColor = if (isDark) Color(0xFF1C1C1E) else Color(0xFF0A0A0A)
                val txtColor = Color.White
                val glowBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color(0xFF007AFF).copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.06f),
                    )
                )
                val pillShape = RoundedCornerShape(28.dp)

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .graphicsLayer {
                            // Subtle breathing scale
                            val breathe = 1f + expandProgress * 0.02f
                            scaleX = breathe
                            scaleY = breathe
                        }
                        .shadow(
                            elevation = 24.dp,
                            shape = pillShape,
                            ambientColor = Color(0xFF007AFF).copy(alpha = 0.25f),
                            spotColor = Color.Black.copy(alpha = 0.4f)
                        )
                        .clip(pillShape)
                        .border(
                            width = 0.5.dp,
                            brush = glowBrush,
                            shape = pillShape
                        )
                        .background(bgColor)
                        .padding(horizontal = 28.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated dot indicator
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34C759)) // iOS Green
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = msg,
                        color = txtColor,
                        fontWeight = FontWeight(550),
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
