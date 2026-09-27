package com.example.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.datastore.KeyboardPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BillingStatus {
    IDLE,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    PURCHASING,
    SUCCESS,
    FAILED,
    RESTORED
}

/**
 * ✅ نسخه‌ی رایگان (Free/Premium Unlocked)
 *
 * این کلاس دیگه به سرویس مایکت وصل نمی‌شه و تمام بخش‌های برنامه
 * به صورت پیش‌فرض VIP هستن. همه‌ی متدها فقط برای سازگاری با کدهای
 * موجود نگه داشته شدن و بی‌اثر هستن.
 */
class MyketBillingManager(
    private val context: Context,
    private val preferences: KeyboardPreferences
) {
    companion object {
        private const val TAG = "MyketBillingManager"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _billingStatus = MutableStateFlow(BillingStatus.SUCCESS)
    val billingStatus: StateFlow<BillingStatus> = _billingStatus.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>("اشتراک ویژه به‌صورت پیش‌فرض فعال است.")
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _vipPrice = MutableStateFlow<String?>(null)
    val vipPrice: StateFlow<String?> = _vipPrice.asStateFlow()

    private val _vipTitle = MutableStateFlow<String?>("اشتراک ویژه (فعال)")
    val vipTitle: StateFlow<String?> = _vipTitle.asStateFlow()

    init {
        // ✅ از اولین لحظه، VIP رو فعال کن
        scope.launch {
            try {
                preferences.setVipStatus(true)
            } catch (e: Exception) {
                Log.w(TAG, "Could not set VIP status on init", e)
            }
        }
        Log.d(TAG, "Billing disabled — all features unlocked for free.")
    }

    // ─────────── متدهای بی‌اثر (برای سازگاری با کدهای موجود) ───────────

    fun bindToBillingService() {
        // no-op
        _billingStatus.value = BillingStatus.SUCCESS
    }

    fun fetchVipProductDetails() {
        // no-op
    }

    fun isMyketInstalled(): Boolean = false

    fun initiatePurchase(activity: Activity) {
        // ✅ به جای باز کردن صفحه‌ی پرداخت، فقط VIP رو فعال می‌کنیم
        scope.launch {
            try {
                preferences.setVipStatus(true)
                _billingStatus.value = BillingStatus.SUCCESS
                _statusMessage.value = "تمام امکانات برنامه به‌صورت رایگان فعال است."
            } catch (e: Exception) {
                Log.e(TAG, "Failed to activate VIP", e)
            }
        }
    }

    fun handlePurchaseResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        // هیچ کاری نمی‌کنیم — همه چیز از قبل فعاله
        return false
    }

    fun restorePurchases(onComplete: (Boolean, String) -> Unit) {
        scope.launch {
            try {
                preferences.setVipStatus(true)
                onComplete(true, "تمام امکانات برنامه به‌صورت رایگان فعال است.")
            } catch (e: Exception) {
                onComplete(false, "خطا در فعال‌سازی امکانات: ${e.localizedMessage}")
            }
        }
    }

    fun verifyAndActivatePurchase(purchaseData: String, signature: String): Boolean {
        scope.launch {
            try {
                preferences.setVipStatus(true)
            } catch (_: Exception) {}
        }
        return true
    }

    fun destroy() {
        // no-op
    }
}