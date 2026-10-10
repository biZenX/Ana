package com.wafeer.app.presentation.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import com.wafeer.app.R
import javax.inject.Inject
import javax.inject.Singleton

data class ProximityDiagnosticState(
    val hasSensor: Boolean = false,
    val sensorName: String = "",
    val vendor: String = "",
    val maxRange: Float = 0f,
    val isVirtual: Boolean = false,
    val currentDistance: Float = -1f,
    val isNear: Boolean = false,
    val triggerCount: Int = 0,
)

@Singleton
class CensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Support wake-up sensor, standard sensor, and all registered proximity sensors on modern devices
    private val proximitySensors: List<Sensor> = run {
        val list = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY)
        val wakeUp = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY, true)
        val defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        (listOfNotNull(wakeUp, defaultSensor) + list).distinct()
    }

    private val primarySensor = proximitySensors.firstOrNull()

    private val _isCensored = MutableStateFlow(false)
    val isCensored: StateFlow<Boolean> = _isCensored

    private val _diagnosticState = MutableStateFlow(
        ProximityDiagnosticState(
            hasSensor = primarySensor != null,
            sensorName = primarySensor?.name ?: "",
            vendor = primarySensor?.vendor ?: "",
            maxRange = primarySensor?.maximumRange ?: 0f,
            isVirtual = primarySensor?.name?.contains("virtual", ignoreCase = true) == true ||
                primarySensor?.name?.contains("elliptic", ignoreCase = true) == true ||
                primarySensor?.name?.contains("ultrasonic", ignoreCase = true) == true,
        )
    )
    val diagnosticState: StateFlow<ProximityDiagnosticState> = _diagnosticState.asStateFlow()

    private var wasNear = false
    private var censorToggleJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun start() {
        for (sensor in proximitySensors) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        censorToggleJob?.cancel()
        censorToggleJob = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values.firstOrNull() ?: return
            val maxRange = event.sensor.maximumRange.takeIf { it > 0f } ?: 5f
            // Universal proximity detection:
            // 1. If distance is 0, it's always near (standard across hardware & virtual sensors).
            // 2. If maxRange > 1 and distance < maxRange, it's near.
            // 3. If maxRange <= 1 and distance < 1, it's near.
            val isNear = distance == 0f || (distance < maxRange && maxRange > 1f) || (maxRange <= 1f && distance < 1f)

            _diagnosticState.update { current ->
                current.copy(
                    sensorName = event.sensor.name,
                    vendor = event.sensor.vendor,
                    maxRange = event.sensor.maximumRange,
                    currentDistance = distance,
                    isNear = isNear,
                    triggerCount = if (isNear && !current.isNear) current.triggerCount + 1 else current.triggerCount,
                )
            }

            if (isNear && !wasNear) {
                startCensorTimer()
            } else if (!isNear && wasNear) {
                cancelCensorTimer()
            }
            wasNear = isNear
        }
    }

    private fun startCensorTimer() {
        censorToggleJob?.cancel()
        censorToggleJob = scope.launch {
            delay(350)
            toggleCensor()
        }
    }

    private fun cancelCensorTimer() {
        censorToggleJob?.cancel()
        censorToggleJob = null
    }

    fun toggleCensor() {
        val newState = !_isCensored.value
        _isCensored.value = newState
        vibrate()

        val messageRes = if (newState) R.string.censor_mode_toast_enabled
        else R.string.censor_mode_toast_disabled
        Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()

        logcat { "Censor mode toggled: $newState (after sensor wave)" }
    }

    fun setCensored(enabled: Boolean) {
        if (_isCensored.value == enabled) return
        _isCensored.value = enabled
        vibrate()
        val messageRes = if (enabled) R.string.censor_mode_toast_enabled
        else R.string.censor_mode_toast_disabled
        Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        logcat { "Censor mode manually set: $enabled" }
    }

    private fun vibrate() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
