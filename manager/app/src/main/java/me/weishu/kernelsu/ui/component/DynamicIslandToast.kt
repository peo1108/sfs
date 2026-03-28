package me.weishu.kernelsu.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    
    LaunchedEffect(Unit) {
        DynamicIslandManager.messages.collect { msg ->
            currentMessage = msg
            delay(2800) // Show for 2.8 seconds
            if (currentMessage == msg) {
                currentMessage = null
            }
        }
    }

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
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                initialOffsetY = { -it }
            ) + fadeIn(),
            exit = slideOutVertically(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                targetOffsetY = { -it }
            ) + fadeOut()
        ) {
            currentMessage?.let { msg ->
                val isDark = isInDarkTheme()
                val bgColor = if (isDark) Color(0xFF1C1C1E) else Color.Black
                val txtColor = Color.White
                
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .shadow(elevation = 16.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(bgColor)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg,
                        color = txtColor,
                        fontWeight = FontWeight(500),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
