package me.weishu.kernelsu.ui.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Data class holding gyro tilt values for X and Y axes, normalized to [-1, 1].
 */
data class GyroTilt(val x: Float = 0f, val y: Float = 0f)

/**
 * Reads the accelerometer tilt of the device and returns animated tilt values.
 * Shared across the composition tree via CompositionLocal for efficiency.
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
 * CompositionLocal for sharing a single GyroTilt across the entire composition tree.
 * Provide this at the screen level to avoid creating multiple sensor listeners.
 */
val LocalGyroTilt = staticCompositionLocalOf { GyroTilt() }

/**
 * Creates a border glow brush that shifts direction based on tilt.
 * Uses Offset-based start/end for real gradient movement.
 */
@Composable
fun rememberGyroGlowBrush(
    primaryColor: Color = Color.White,
    accentColor: Color = Color(0xFF007AFF),
    tilt: GyroTilt = GyroTilt()
): Brush {
    val startX = (0.5f - tilt.x * 0.5f).coerceIn(0f, 1f) * 1000f
    val startY = (0.5f - tilt.y * 0.5f).coerceIn(0f, 1f) * 1000f
    val endX = (0.5f + tilt.x * 0.5f).coerceIn(0f, 1f) * 1000f
    val endY = (0.5f + tilt.y * 0.5f).coerceIn(0f, 1f) * 1000f

    val tiltMagnitude = kotlin.math.sqrt(tilt.x * tilt.x + tilt.y * tilt.y).coerceIn(0f, 1f)
    val highlightAlpha = 0.25f + tiltMagnitude * 0.35f

    return Brush.linearGradient(
        colors = listOf(
            primaryColor.copy(alpha = highlightAlpha),
            accentColor.copy(alpha = 0.10f + tiltMagnitude * 0.15f),
            primaryColor.copy(alpha = 0.05f),
        ),
        start = Offset(startX, startY),
        end = Offset(endX, endY)
    )
}

/**
 * Creates a radial light spot brush that follows device tilt.
 * This is the PRIMARY visual effect — a glowing "flashlight" spot on the card surface.
 */
@Composable
fun rememberGyroRadialGlow(
    color: Color = Color.White,
    tilt: GyroTilt = GyroTilt()
): Brush {
    // Map tilt to center position: when phone tilts left, light moves right
    val centerX = (0.5f - tilt.x * 0.45f).coerceIn(0.05f, 0.95f)
    val centerY = (0.5f - tilt.y * 0.35f).coerceIn(0.05f, 0.95f)
    val tiltMagnitude = kotlin.math.sqrt(tilt.x * tilt.x + tilt.y * tilt.y).coerceIn(0f, 1f)

    // Brighter when tilting more
    val intensity = 0.06f + tiltMagnitude * 0.14f

    return Brush.radialGradient(
        colors = listOf(
            color.copy(alpha = intensity),
            color.copy(alpha = intensity * 0.3f),
            Color.Transparent,
        ),
        center = Offset(centerX * 1000f, centerY * 600f),
        radius = 400f
    )
}
