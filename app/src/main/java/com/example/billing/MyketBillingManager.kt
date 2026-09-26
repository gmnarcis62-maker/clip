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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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
        private const val CONNECTION_TIMEOUT_MS = 6000L
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _billingStatus = MutableStateFlow(BillingStatus.IDLE)
    val billingStatus: StateFlow<BillingStatus> = _billingStatus.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _vipPrice = MutableStateFlow<String?>(null)
    val vipPrice: StateFlow<String?> = _vipPrice.asStateFlow()

    private val _vipTitle = MutableStateFlow<String?>(null)
    val vipTitle: StateFlow<String?> = _vipTitle.asStateFlow()

    private var billingService: IInAppBillingService? = null
    private var isConnected = false
    private var hasBound = false
    private var pendingActivity: Activity? = null
    private var timeoutJob: Job? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timeoutJob?.cancel()
            billingService = IInAppBillingService.Stub.asInterface(service)
            isConnected = true
            _billingStatus.value = BillingStatus.CONNECTED
            Log.d(TAG, "Myket billing service connected")
            fetchVipProductDetails()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            billingService = null
            isConnected = false
            _billingStatus.value = BillingStatus.DISCONNECTED
            _statusMessage.value = "ارتباط با سرویس مایکت قطع شد."
            Log.w(TAG, "Myket billing service disconnected")
        }

        override fun onBindingDied(name: ComponentName?) {
            billingService = null
            isConnected = false
            hasBound = false
            _billingStatus.value = BillingStatus.DISCONNECTED
            _statusMessage.value = "اتصال به مایکت از دست رفت. لطفاً دوباره تلاش کنید."
            Log.e(TAG, "Myket billing binding died")
        }

        override fun onNullBinding(name: ComponentName?) {
            billingService = null
            isConnected = false
            hasBound = false
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "سرویس مایکت پاسخ نداد. مطمئن شوید آخرین نسخه مایکت نصب است."
            Log.e(TAG, "Myket billing returned null binding")
        }
    }

    init {
        bindToBillingService()
    }

    /**
     * Attempts to bind to the Myket billing service.
     * Sets a timeout so the UI never stays stuck in CONNECTING forever.
     */
    fun bindToBillingService() {
        if (isConnected) {
            Log.d(TAG, "Already connected, skipping bind")
            return
        }

        if (!isMyketInstalled()) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "برنامه مایکت روی دستگاه شما نصب نیست. ابتدا مایکت را نصب کنید."
            Log.w(TAG, "Myket app not installed")
            return
        }

        try {
            val intent = Intent(BILLING_SERVICE_ACTION).apply {
                setPackage(MYKET_PACKAGE)
            }

            _billingStatus.value = BillingStatus.CONNECTING
            _statusMessage.value = "در حال اتصال به سرویس مایکت..."

            val bound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            hasBound = bound

            if (!bound) {
                Log.e(TAG, "bindService returned false — service not found")
                _billingStatus.value = BillingStatus.FAILED
                _statusMessage.value = "سرویس پرداخت مایکت در دسترس نیست. لطفاً مایکت را به‌روزرسانی کنید."
                return
            }

            // Timeout guard: if onServiceConnected never fires, don't stay stuck.
            timeoutJob?.cancel()
            timeoutJob = scope.launch {
                delay(CONNECTION_TIMEOUT_MS)
                if (!isConnected) {
                    Log.e(TAG, "Timeout while waiting for Myket billing service")
                    _billingStatus.value = BillingStatus.DISCONNECTED
                    _statusMessage.value = "زمان اتصال به مایکت به پایان رسید. لطفاً دوباره تلاش کنید."
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind to Myket billing service", e)
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "خطا در اتصال به مایکت: ${e.localizedMessage}"
        }
    }

    fun fetchVipProductDetails() {
        val service = billingService
        if (service == null || !isConnected) return

        scope.launch {
            try {
                val querySkus = Bundle().apply {
                    putStringArrayList("ITEM_ID_LIST", arrayListOf(SKU_VIP_PRO))
                }

                val skuDetails: Bundle = service.getSkuDetails(
                    BILLING_API_VERSION,
                    context.packageName,
                    "inapp",
                    querySkus
                )

                val responseCode = skuDetails.getInt("RESPONSE_CODE", -1)
                if (responseCode != 0) {
                    Log.e(TAG, "getSkuDetails failed: $responseCode")
                    return@launch
                }

                val detailsList = skuDetails.getStringArrayList("DETAILS_LIST") ?: return@launch

                for (detailsJson in detailsList) {
                    val json = JSONObject(detailsJson)
                    if (json.optString("productId") == SKU_VIP_PRO) {
                        val price = json.optString("price", "")
                        val title = json.optString("title", "")
                        _vipPrice.value = price.ifBlank { null }
                        _vipTitle.value = title.ifBlank { null }
                        Log.d(TAG, "Fetched VIP price: $price")
                        break
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching SKU details", e)
            }
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

    private fun isBillingSupported(): Boolean {
        val service = billingService ?: return false
        return try {
            service.isBillingSupported(BILLING_API_VERSION, context.packageName, "inapp") == 0
        } catch (e: RemoteException) {
            Log.e(TAG, "Error checking billing support", e)
            false
        }
    }

    fun initiatePurchase(activity: Activity) {
        // 1. Myket installed?
        if (!isMyketInstalled()) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "برنامه مایکت نصب نیست. برای خرید VIP ابتدا مایکت را نصب کنید."
            return
        }

        // 2. Service bound?
        if (billingService == null || !isConnected) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "در حال اتصال مجدد به سرویس مایکت... لطفاً یک لحظه صبر کنید و دوباره تلاش کنید."
            // Trigger a fresh bind attempt
            bindToBillingService()
            return
        }

        // 3. Billing supported?
        if (!isBillingSupported()) {
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "خرید درون‌برنامه‌ای در این نسخه از مایکت پشتیبانی نمی‌شود."
            return
        }

        pendingActivity = activity
        _billingStatus.value = BillingStatus.PURCHASING
        _statusMessage.value = "در حال باز کردن صفحه پرداخت مایکت..."

        try {
            val service = billingService ?: return
            val developerPayload = "mersana_vip_${System.currentTimeMillis()}"

            val buyIntentBundle: Bundle = service.getBuyIntent(
                BILLING_API_VERSION,
                context.packageName,
                SKU_VIP_PRO,
                "inapp",
                developerPayload
            )

            val responseCode = buyIntentBundle.getInt("RESPONSE_CODE", -1)

            if (responseCode != 0) {
                _billingStatus.value = BillingStatus.FAILED
                _statusMessage.value = "خطای مایکت در شروع خرید (کد $responseCode)."
                Log.e(TAG, "getBuyIntent failed: $responseCode")
                return
            }

            val pendingIntent: PendingIntent? =
                buyIntentBundle.getParcelable("BUY_INTENT")

            if (pendingIntent != null) {
                activity.startIntentSenderForResult(
                    pendingIntent.intentSender,
                    REQUEST_CODE_PURCHASE,
                    Intent(),
                    0, 0, 0
                )
            } else {
                _billingStatus.value = BillingStatus.FAILED
                _statusMessage.value = "صفحه پرداخت مایکت باز نشد."
            }

        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException during purchase", e)
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "خطا در ارتباط با مایکت: ${e.localizedMessage}"
        } catch (e: Exception) {
            Log.e(TAG, "Exception during purchase", e)
            _billingStatus.value = BillingStatus.FAILED
            _statusMessage.value = "خطا در شروع خرید: ${e.localizedMessage}"
        }
    }

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
                _statusMessage.value = "خرید لغو شد یا خطایی رخ داد (کد $responseCode)."
            }
        } else {
            _billingStatus.value = BillingStatus.IDLE
            _statusMessage.value = "خرید لغو شد."
        }
        return false
    }

    fun restorePurchases(onComplete: (Boolean, String) -> Unit) {
        if (!isMyketInstalled()) {
            onComplete(false, "برنامه مایکت روی دستگاه نصب نیست.")
            return
        }

        // Force a fresh bind attempt if not connected yet.
        if (billingService == null || !isConnected) {
            bindToBillingService()
            onComplete(
                false,
                "در حال اتصال به سرویس مایکت... چند لحظه بعد دوباره «بازیابی خرید» را بزنید."
            )
            return
        }

        val service = billingService ?: run {
            onComplete(false, "سرویس مایکت آماده نیست.")
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
                    onComplete(false, "خطا در دریافت اطلاعات خرید (کد $responseCode).")
                    return@launch
                }

                val purchaseDataList =
                    purchasesBundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                val signatureList =
                    purchasesBundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")

                if (!purchaseDataList.isNullOrEmpty() && !signatureList.isNullOrEmpty()) {
                    for (i in purchaseDataList.indices) {
                        val purchaseData = purchaseDataList[i]
                        val signature = signatureList.getOrNull(i) ?: continue

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
                if (json.optString("productId") == SKU_VIP_PRO) {
                    scope.launch { preferences.setVipStatus(true) }
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
        timeoutJob?.cancel()
        if (hasBound) {
            try {
                context.unbindService(serviceConnection)
            } catch (_: Exception) {}
        }
        hasBound = false
        isConnected = false
        billingService = null
    }
}