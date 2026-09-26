package com.example.domain.ime

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager

enum class KeyboardActivationState {
    NOT_ENABLED,          // Service found, but toggle is OFF in Android Language & Input settings
    ENABLED_NOT_DEFAULT,  // Toggle is ON, but not selected as current default keyboard
    DEFAULT,              // Toggle is ON and selected as active default keyboard
    SYSTEM_PROBLEM,       // Service not registered in PackageManager or Manifest issue
    UNKNOWN
}

object ImeActivationManager {

    private const val TAG = "ImeActivationManager"
    const val IME_SERVICE_CLASS = "com.example.ime.ClipbordIME"

    /**
     * Checks the real-time IME state from Android system APIs.
     * Uses official InputMethodManager APIs without triggering Android 14+ SecurityExceptions.
     */
    fun checkActivationState(context: Context): KeyboardActivationState {
        return try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: return KeyboardActivationState.UNKNOWN

            val packageName = context.packageName
            val targetComponent = ComponentName(packageName, IME_SERVICE_CLASS)
            val targetImeId = targetComponent.flattenToShortString()

            // 1. Check if the IME is registered in PackageManager / system
            val allImes = imm.inputMethodList
            val isInstalled = allImes.any { imeInfo ->
                imeInfo.packageName == packageName || imeInfo.serviceName.contains("ClipbordIME")
            }

            if (!isInstalled && allImes.isNotEmpty()) {
                Log.w(TAG, "ClipbordIME service not found in system input method list")
                return KeyboardActivationState.SYSTEM_PROBLEM
            }

            // 2. Check if the IME is enabled in Settings using official public API
            val enabledImes = imm.enabledInputMethodList
            val isEnabled = enabledImes.any { imeInfo ->
                imeInfo.packageName == packageName || imeInfo.serviceName.contains("ClipbordIME")
            }

            if (!isEnabled) {
                return KeyboardActivationState.NOT_ENABLED
            }

            // 3. Check if the IME is currently selected as DEFAULT
            var isDefault = false

            // Try reading default IME from Settings.Secure safely
            try {
                val currentDefault = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD
                ) ?: ""

                if (currentDefault.isNotBlank()) {
                    isDefault = currentDefault.contains(packageName) ||
                            currentDefault.contains("ClipbordIME") ||
                            currentDefault == targetImeId
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not read DEFAULT_INPUT_METHOD from Settings.Secure", e)
            }

            // On Android 14+ (API 34+), verify with currentInputMethodInfo if available
            if (!isDefault && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                try {
                    val currentIme = imm.currentInputMethodInfo
                    if (currentIme != null && (currentIme.packageName == packageName || currentIme.serviceName.contains("ClipbordIME"))) {
                        isDefault = true
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not check currentInputMethodInfo", e)
                }
            }

            if (isDefault) {
                KeyboardActivationState.DEFAULT
            } else {
                KeyboardActivationState.ENABLED_NOT_DEFAULT
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking IME activation state", e)
            KeyboardActivationState.UNKNOWN
        }
    }

    /**
     * Launches the official Android Input Method Settings screen.
     */
    fun openInputMethodSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch ACTION_INPUT_METHOD_SETTINGS, trying fallback", e)
            try {
                // Fallback to general settings if OEM modified the action
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                true
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to launch Settings fallback", ex)
                false
            }
        }
    }

    /**
     * Shows the official Android Input Method Picker dialog.
     */
    fun showInputMethodPicker(context: Context): Boolean {
        return try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showInputMethodPicker()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show Input Method Picker", e)
            false
        }
    }
}
