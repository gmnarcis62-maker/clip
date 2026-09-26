package com.example.ai.domain

import android.util.Log
import com.example.ai.data.AiConfig
import com.example.ai.data.model.ChatCompletionRequest
import com.example.ai.data.remote.AiApiService
import com.example.data.datastore.KeyboardPreferences
import com.example.domain.shamsi.PersianDateUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

sealed class AiResult {
    data class Success(val outputText: String, val remainingUsage: Int, val isVip: Boolean) : AiResult()
    data class Error(val messagePersian: String, val canRetry: Boolean = true) : AiResult()
    object LimitReached : AiResult()
}

interface AiRepository {
    suspend fun executeOperation(
        operation: AiOperation,
        inputText: String,
        customPrompt: String? = null
    ): AiResult

    suspend fun testDirectConnection(samplePrompt: String = "به فارسی بگو سلام"): AiResult

    suspend fun getRemainingDailyUsage(): Int
}

class AiRepositoryImpl(
    private val preferences: KeyboardPreferences
) : AiRepository {

    companion object {
        private const val TAG = "ClipbordAI"
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(AiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(AiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(AiConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor { message ->
                // Filter out any authorization lines for security
                if (!message.contains("Authorization", ignoreCase = true) && !message.contains("atr_", ignoreCase = true)) {
                    Log.d(TAG, message)
                }
            }.apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    private fun getApiService(baseUrl: String): AiApiService {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(safeBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AiApiService::class.java)
    }

    override suspend fun getRemainingDailyUsage(): Int {
        val isVip = preferences.isVip.first()
        if (isVip) return Int.MAX_VALUE

        val todayDate = PersianDateUtils.getCurrentPersianDate()
        val lastUsageDate = preferences.aiLastUsageDate.first()
        val currentCount = if (lastUsageDate == todayDate) preferences.aiDailyUsageCount.first() else 0

        return (AiConfig.FREE_DAILY_REQUEST_LIMIT - currentCount).coerceAtLeast(0)
    }

    override suspend fun testDirectConnection(samplePrompt: String): AiResult = withContext(Dispatchers.IO) {
        val customEndpoint = preferences.customAiEndpoint.first()
        val customModel = preferences.customAiModel.first()
        val baseUrl = if (customEndpoint.isNotBlank()) customEndpoint else AiConfig.DEFAULT_BASE_URL
        val model = if (customModel.isNotBlank()) customModel else AiConfig.DEFAULT_MODEL
        val apiKey = AiConfig.DEFAULT_API_KEY

        Log.i(TAG, "Testing Atria ASI connection to baseUrl: $baseUrl, model: $model")

        try {
            val messages = listOf(
                com.example.ai.data.model.ChatMessage(role = "user", content = samplePrompt)
            )
            val request = ChatCompletionRequest(
                model = model,
                messages = messages,
                temperature = 0.7f,
                maxTokens = 300
            )

            val apiService = getApiService(baseUrl)
            val response = apiService.createChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val statusCode = response.code()
            Log.i(TAG, "Atria ASI Test HTTP Status: $statusCode")

            if (response.isSuccessful) {
                val responseBody = response.body()
                val choice = responseBody?.choices?.firstOrNull()
                val outputText = choice?.message?.content?.trim()
                Log.i(TAG, "Atria ASI Test Success output length: ${outputText?.length ?: 0}")

                if (!outputText.isNullOrBlank()) {
                    return@withContext AiResult.Success(
                        outputText = outputText,
                        remainingUsage = Int.MAX_VALUE,
                        isVip = true
                    )
                } else {
                    return@withContext AiResult.Error("پاسخ دریافتی از هوش مصنوعی خالی است.")
                }
            } else {
                val errorBodyStr = response.errorBody()?.string() ?: ""
                Log.e(TAG, "Atria ASI Error HTTP $statusCode, Body: $errorBodyStr")

                val errorMsg = when (statusCode) {
                    400 -> "درخواست ارسالی به مدل هوش مصنوعی نامعتبر است (HTTP 400)."
                    401 -> "خطای احراز هویت یا کلید API نامعتبر است (HTTP 401)."
                    404 -> "سرویس هوش مصنوعی یا مدل $model در دسترس نیست (HTTP 404)."
                    429 -> "سقف تعداد درخواست یا سهمیه سرویس هوش مصنوعی به پایان رسیده است (HTTP 429)."
                    in 500..599 -> "خطای موقت سرور هوش مصنوعی (HTTP $statusCode). لطفاً دقایقی بعد امتحان کنید."
                    else -> "خطا در ارتباط با سرور هوش مصنوعی (کد وضعیت: $statusCode)"
                }
                return@withContext AiResult.Error(errorMsg)
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network UnknownHostException: ${e.message}")
            return@withContext AiResult.Error("عدم دسترسی به اینترنت. لطفاً اتصال اینترنت خود را بررسی کنید.")
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network SocketTimeoutException: ${e.message}")
            return@withContext AiResult.Error("مهلت پاسخگویی سرویس به پایان رسید (Timeout). لطفاً مجدداً امتحان کنید.")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception: ${e.message}")
            return@withContext AiResult.Error("خطای غیرمنتظره در ارتباط با هوش مصنوعی: ${e.localizedMessage ?: "ناشناخته"}")
        }
    }

    override suspend fun executeOperation(
        operation: AiOperation,
        inputText: String,
        customPrompt: String?
    ): AiResult = withContext(Dispatchers.IO) {
        if (inputText.isBlank() && operation != AiOperation.CUSTOM_PROMPT) {
            return@withContext AiResult.Error("لطفاً ابتدا متنی را بنویسید یا انتخاب کنید.")
        }

        // 1. Check VIP & Daily Limit
        val isVip = preferences.isVip.first()
        val todayDate = PersianDateUtils.getCurrentPersianDate()
        val lastUsageDate = preferences.aiLastUsageDate.first()
        var currentCount = if (lastUsageDate == todayDate) preferences.aiDailyUsageCount.first() else 0

        if (!isVip && currentCount >= AiConfig.FREE_DAILY_REQUEST_LIMIT) {
            return@withContext AiResult.LimitReached
        }

        // 2. Fetch configured Endpoint, Model and Key
        val customEndpoint = preferences.customAiEndpoint.first()
        val customModel = preferences.customAiModel.first()
        val baseUrl = if (customEndpoint.isNotBlank()) customEndpoint else AiConfig.DEFAULT_BASE_URL
        val model = if (customModel.isNotBlank()) customModel else AiConfig.DEFAULT_MODEL
        val apiKey = AiConfig.DEFAULT_API_KEY

        Log.i(TAG, "Executing AI operation: ${operation.name} on baseUrl: $baseUrl with model: $model")

        try {
            val messages = operation.buildMessages(inputText, customPrompt)
            val request = ChatCompletionRequest(
                model = model,
                messages = messages,
                temperature = if (operation == AiOperation.GRAMMAR_CORRECTION) 0.2f else 0.7f,
                maxTokens = 1200
            )

            val apiService = getApiService(baseUrl)
            val response = apiService.createChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            val statusCode = response.code()
            Log.i(TAG, "Atria ASI Response HTTP Status: $statusCode")

            if (response.isSuccessful) {
                val responseBody = response.body()
                val choice = responseBody?.choices?.firstOrNull()
                val outputText = choice?.message?.content?.trim()

                if (!outputText.isNullOrBlank()) {
                    // Update usage stats for non-VIP
                    if (!isVip) {
                        currentCount++
                        preferences.setAiUsage(currentCount, todayDate)
                    }

                    val remaining = if (isVip) Int.MAX_VALUE else (AiConfig.FREE_DAILY_REQUEST_LIMIT - currentCount).coerceAtLeast(0)
                    Log.i(TAG, "AI Operation Success. Length: ${outputText.length}, Remaining: $remaining")
                    return@withContext AiResult.Success(
                        outputText = outputText,
                        remainingUsage = remaining,
                        isVip = isVip
                    )
                } else {
                    Log.w(TAG, "AI returned empty response body choice")
                    return@withContext AiResult.Error("پاسخی از هوش مصنوعی دریافت نشد. لطفاً دوباره تلاش کنید.")
                }
            } else {
                val errorBodyStr = response.errorBody()?.string() ?: ""
                Log.e(TAG, "Atria ASI Error HTTP $statusCode, Body: $errorBodyStr")

                val errorMsg = when (statusCode) {
                    400 -> "درخواست ارسالی به مدل هوش مصنوعی نامعتبر است (HTTP 400)."
                    401 -> "خطای احراز هویت یا کلید API نامعتبر است (HTTP 401)."
                    404 -> "سرویس هوش مصنوعی یا مدل $model در دسترس نیست (HTTP 404)."
                    429 -> "سقف تعداد درخواست یا سهمیه سرویس هوش مصنوعی به پایان رسیده است (HTTP 429)."
                    in 500..599 -> "خطای موقت سرور هوش مصنوعی (HTTP $statusCode). لطفاً دقایقی بعد امتحان کنید."
                    else -> "خطا در ارتباط با سرور هوش مصنوعی (کد وضعیت: $statusCode)"
                }
                return@withContext AiResult.Error(errorMsg)
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network UnknownHostException: ${e.message}")
            return@withContext AiResult.Error("عدم دسترسی به اینترنت. لطفاً اتصال اینترنت خود را بررسی کنید.")
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network SocketTimeoutException: ${e.message}")
            return@withContext AiResult.Error("مهلت پاسخگویی سرویس به پایان رسید (Timeout). لطفاً مجدداً امتحان کنید.")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception: ${e.message}")
            return@withContext AiResult.Error("خطای غیرمنتظره در پردازش هوش مصنوعی: ${e.localizedMessage ?: "ناشناخته"}")
        }
    }
}
