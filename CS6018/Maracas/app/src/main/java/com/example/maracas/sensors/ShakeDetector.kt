package com.example.maracas.sensors


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt
import android.util.Log

// Detects shake gestures using the accelerometer
class ShakeDetector(
    private val sensorManager: SensorManager,
    private val onShake: (gForce: Float) -> Unit,
    private val thresholdG: Float = 1.0f,
    private val cooldownMs: Long = 350L
) : SensorEventListener {

    private var lastShakeAt = 0L

    fun start() {
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accel == null) {
            Log.e("Shake", "No accelerometer on this device/emulator")
            return
        }
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)
        Log.d("Shake", "Accelerometer listener registered")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Convert to g-units (divide by Earth's gravity)
        val gx = event.values[0] / SensorManager.GRAVITY_EARTH
        val gy = event.values[1] / SensorManager.GRAVITY_EARTH
        val gz = event.values[2] / SensorManager.GRAVITY_EARTH
        val gForce = sqrt(gx * gx + gy * gy + gz * gz)
        Log.d("Shake", "ax=$gx, ay=$gy, az=$gz, gForce=$gForce")

        val now = System.currentTimeMillis()
        if (gForce > thresholdG && (now - lastShakeAt) > cooldownMs) {
            lastShakeAt = now
            onShake(gForce)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}