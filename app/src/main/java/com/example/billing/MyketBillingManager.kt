package com.example.billing

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.android.vending.billing.IInAppBillingService
import com.example.data.datastore.KeyboardPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

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

class MyketBillingManager(
    private val context: Context,
    private val preferences: KeyboardPreferences
) {
    companion object {
        const val SKU_VIP_PRO = "red.line.clipbord_pro"
        const val MYKET_PACKAGE = "ir.mservices.market"
        const val BILLING_SERVICE_ACTION = "ir.mservices.market.InAppBillingService.BIND"
        const val BILLING_API_VERSION = 3
        const val REQUEST_CODE_PURCHASE = 1001
        private const val TAG = "MyketBillingManager"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _billingStatus = MutableStateFlow(BillingStatus.IDLE)
    val billingStatus: StateFlow<BillingStatus> = _billingStatus.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var billingService: IInAppBillingService? = null
    private var isConnected = false
    private var pendingActivity: Activity? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            billingService = IInAppBillingService.Stub.asInterface(service)
            isConnected = true
            _billingStatus.value = BillingStatus.CONNECTED
            Log.d(TAG, "Myket billing service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            billingService = null
            isConnected = false
            _billingStatus.value = BillingStatus.DISCONNECTED
            Log.d(TAG, "Myket billing service disconnected")
        }
    }

    init {
        bindToBillingService()
    }

    private fun bindToBillingService() {
        try {
            val intent = Intent(BILLING_SERVICE_ACTION).apply {
                setPackage(MYKET_PACKAGE)
            }
            _billingStatus.value = BillingStatus.CONNECTING
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind to Myket billing service", e)
            _billingStatus.value = BillingStatus.FAILED
        }
    }

    fun isMyketInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(MYKET_PACKAGE, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Check if the Myket billing API version is supported.
     */
    private fun isBillingSupported(): Boolean {
        val service = billingService ?: return false
        return try {
            val response = service.isBillingSupported(
                BILLING_API_VERSION,
                context.packageName,
                "inapp"
            )
            response == 0 // BILLING_RESPONSE_RESULT_OK
        } catch (e: RemoteException) {
            Log.e(TAG, "Error checking billing support", e)
            false
        }
    }

    /**
     * Start the purchase flow for the VIP product.
     * This sends a real IAP request to Myket via the bound billing service.
     */
    fun initiatePurchase(activity: Activity) {
        if (!isMyketInstalled()) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "برنامه مایکت روی دستگاه شما نصب نیست. برای خرید VIP ابتدا مایکت را نصب کنید."
            return
        }

        if (billingService == null || !isConnected) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "اتصال به سرویس پرداخت مایکت برقرار نشد. لطفاً دوباره تلاش کنید."
            // Try to reconnect
            bindToBillingService()
            return
        }

        if (!isBillingSupported()) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "نسخه خرید درون‌برنامه‌ای مایکت پشتیبانی نمی‌شود."
            return
        }

        pendingActivity = activity
        _billingStatus.value = BillingStatus.PURCHASING
        _statusMessage.value = "در حال اتصال به درگاه پرداخت مایکت..."

        try {
            val service = billingService ?: return

            // Generate a unique developer payload for security
            val developerPayload = "mersana_vip_${System.currentTimeMillis()}"

            val buyIntentBundle: Bundle = service.getBuyIntent(
                BILLING_API_VERSION,
                context.packageName,
                SKU_VIP_PRO,
                "inapp",
                developerPayload
            )

            val responseCode = buyIntentBundle.getInt("RESPONSE_CODE", -1)

            if (responseCode != 0) { // 0 = BILLING_RESPONSE_RESULT_OK
                _billingStatus.value = BillingStatus.FAILED
                _statusMessage.value = "خطا در دریافت اطلاعات خرید. کد خطا: $responseCode"
                Log.e(TAG, "getBuyIntent failed with response code: $responseCode")
                return
            }

            val pendingIntent: PendingIntent? = buyIntentBundle.getParcelable("BUY_INTENT")

            if (pendingIntent != null) {
                activity.startIntentSenderForResult(
                    pendingIntent.intentSender,
                    REQUEST_CODE_PURCHASE,
                    Intent(),
                    0, 0, 0
                )
            } else {
                _billingStatus.value = BillingStatus.FAILED
                _statusMessage.value = "خطا در باز کردن صفحه پرداخت مایکت."
            }

        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException during purchase", e)
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "خطا در ارتباط با سرویس مایکت: ${e.localizedMessage}"
        } catch (e: Exception) {
            Log.e(TAG, "Exception during purchase", e)
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "خطا در شروع خرید: ${e.localizedMessage}"
        }
    }

    /**
     * Handle the result returned from the Myket purchase activity.
     * Call this from your Activity's onActivityResult.
     */
    fun handlePurchaseResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        if (requestCode != REQUEST_CODE_PURCHASE) return false

        if (resultCode == Activity.RESULT_OK) {
            val responseCode = data?.getIntExtra("RESPONSE_CODE", -1) ?: -1
            val purchaseData = data?.getStringExtra("INAPP_PURCHASE_DATA")
            val dataSignature = data?.getStringExtra("INAPP_DATA_SIGNATURE")

            if (responseCode == 0 && purchaseData != null && dataSignature != null) {
                return verifyAndActivatePurchase(purchaseData, dataSignature)
            } else {
                _billingStatus.value = BillingStatus.FAILED
                _statusMessage.value = "خرید لغو شد یا خطایی رخ داد. کد: $responseCode"
            }
        } else {
            _billingStatus.value = BillingStatus.IDLE
            _statusMessage.value = "خرید لغو شد."
        }
        return false
    }

    fun restorePurchases(onComplete: (Boolean, String) -> Unit) {
        if (!isMyketInstalled()) {
            onComplete(false, "برنامه مایکت روی دستگاه شما نصب نیست.")
            return
        }

        val service = billingService
        if (service == null || !isConnected) {
            onComplete(false, "اتصال به سرویس مایکت برقرار نشد.")
            return
        }

        scope.launch {
            _billingStatus.value = BillingStatus.CONNECTING
            try {
                val purchasesBundle = service.getPurchases(
                    BILLING_API_VERSION,
                    context.packageName,
                    "inapp",
                    null
                )
                val responseCode = purchasesBundle.getInt("RESPONSE_CODE", -1)

                if (responseCode != 0) {
                    _billingStatus.value = BillingStatus.FAILED
                    onComplete(false, "خطا در دریافت اطلاعات خرید. کد: $responseCode")
                    return@launch
                }

                val purchaseDataList = purchasesBundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                val signatureList = purchasesBundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")

                if (purchaseDataList != null && signatureList != null) {
                    for (i in purchaseDataList.indices) {
                        val purchaseData = purchaseDataList[i]
                        val signature = signatureList[i]

                        if (verifyAndActivatePurchase(purchaseData, signature)) {
                            _billingStatus.value = BillingStatus.RESTORED
                            onComplete(true, "خرید VIP شما با موفقیت بازیابی شد.")
                            return@launch
                        }
                    }
                }

                _billingStatus.value = BillingStatus.IDLE
                onComplete(false, "هیچ خریدی برای بازیابی پیدا نشد.")

            } catch (e: RemoteException) {
                _billingStatus.value = BillingStatus.FAILED
                onComplete(false, "خطا در ارتباط با مایکت: ${e.localizedMessage}")
            } catch (e: Exception) {
                _billingStatus.value = BillingStatus.FAILED
                onComplete(false, "خطای غیرمنتظره: ${e.localizedMessage}")
            }
        }
    }

    fun verifyAndActivatePurchase(purchaseData: String, signature: String): Boolean {
        val isValid = Security.verifyPurchase(
            Security.MYKET_PUBLIC_KEY,
            purchaseData,
            signature
        )

        if (isValid) {
            try {
                val json = JSONObject(purchaseData)
                val productId = json.optString("productId")
                if (productId == SKU_VIP_PRO) {
                    scope.launch {
                        preferences.setVipStatus(true)
                    }
                    _billingStatus.value = BillingStatus.SUCCESS
                    _statusMessage.value = "تبریک! اشتراک VIP شما با موفقیت فعال شد."
                    return true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing purchase data", e)
            }
        }

        _billingStatus.value = BillingStatus.FAILED
        _statusMessage.value = "تأیید اعتبار خرید ناموفق بود."
        return false
    }

    fun destroy() {
        if (isConnected) {
            try {
                context.unbindService(serviceConnection)
            } catch (_: Exception) {}
            isConnected = false
            billingService = null
        }
    }
}