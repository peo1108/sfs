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
 * Creates a dynamic linear gradient brush that ACTUALLY shifts direction based on device tilt.
 * The gradient start/end Offsets move with the accelerometer, simulating real light reflection.
 *
 * FIX: Previous version computed startX/startY but never used them in Brush.linearGradient.
 * Now uses Offset-based start/end to create a truly dynamic gradient direction.
 */
@Composable
fun rememberGyroGlowBrush(
    primaryColor: Color = Color.White,
    accentColor: Color = Color(0xFF007AFF),
    tilt: GyroTilt = GyroTilt()
): Brush {
    // Use tilt to shift gradient direction — this is the key fix!
    // When phone tilts left (x > 0), light moves right; tilt right (x < 0), light moves left
    // When phone tilts forward (y > 0), light moves down; tilt back (y < 0), light moves up
    val startX = (0.5f - tilt.x * 0.5f).coerceIn(0f, 1f) * 1000f
    val startY = (0.5f - tilt.y * 0.5f).coerceIn(0f, 1f) * 1000f
    val endX = (0.5f + tilt.x * 0.5f).coerceIn(0f, 1f) * 1000f
    val endY = (0.5f + tilt.y * 0.5f).coerceIn(0f, 1f) * 1000f

    // Dynamic alpha intensity based on tilt magnitude
    val tiltMagnitude = kotlin.math.sqrt(tilt.x * tilt.x + tilt.y * tilt.y).coerceIn(0f, 1f)
    val highlightAlpha = 0.20f + tiltMagnitude * 0.30f  // 0.20 at rest → 0.50 at max tilt

    return Brush.linearGradient(
        colors = listOf(
            primaryColor.copy(alpha = highlightAlpha),
            accentColor.copy(alpha = 0.08f + tiltMagnitude * 0.12f),
            primaryColor.copy(alpha = 0.05f),
        ),
        start = Offset(startX, startY),
        end = Offset(endX, endY)
    )
}
