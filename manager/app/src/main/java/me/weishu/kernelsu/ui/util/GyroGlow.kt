package me.weishu.kernelsu.ui.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Data class holding gyro tilt values for X and Y axes, normalized to [-1, 1].
 */
data class GyroTilt(val x: Float = 0f, val y: Float = 0f)

/**
 * Reads the accelerometer tilt of the device and returns normalized tilt values.
 * Used for the "Gyro Shine" light reflection effect on Cards.
 */
@Composable
fun rememberGyroTilt(): State<GyroTilt> {
    val context = LocalContext.current
    val tiltState = remember { mutableStateOf(GyroTilt()) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Normalize accelerometer values: x, y range typically -9.8..9.8
                val rawX = (event.values[0] / 9.8f).coerceIn(-1f, 1f)
                val rawY = (event.values[1] / 9.8f).coerceIn(-1f, 1f)
                tiltState.value = GyroTilt(x = rawX, y = rawY)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    return tiltState
}

/**
 * Creates a dynamic linear gradient brush that shifts based on device tilt.
 * The gradient simulates light reflecting off a glass/metal surface.
 */
@Composable
fun rememberGyroGlowBrush(
    primaryColor: Color = Color.White,
    accentColor: Color = Color(0xFF007AFF), // iOS Blue
    tilt: GyroTilt = GyroTilt()
): Brush {
    // Map tilt to gradient start/end positions
    val startX = 0.5f + tilt.x * 0.4f
    val startY = 0.5f + tilt.y * 0.4f

    return Brush.linearGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.35f + tilt.x.coerceIn(0f, 1f) * 0.15f),
            accentColor.copy(alpha = 0.12f),
            primaryColor.copy(alpha = 0.08f + tilt.y.coerceIn(0f, 1f) * 0.10f),
        )
    )
}
