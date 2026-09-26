package com.example.keyboard

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticAndSoundFeedback(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun keyPress(vibrationEnabled: Boolean, vibrationStrength: Int, soundEnabled: Boolean) {
        if (soundEnabled && audioManager != null) {
            try {
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.4f)
            } catch (_: Exception) {
            }
        }

        if (vibrationEnabled && vibrator != null && vibrator.hasVibrator()) {
            try {
                val duration = vibrationStrength.coerceIn(10, 80).toLong()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val amplitude = (vibrationStrength * 2.5).toInt().coerceIn(20, 255)
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration)
                }
            } catch (_: Exception) {
            }
        }
    }
}
