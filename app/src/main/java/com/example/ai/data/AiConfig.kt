package com.example.ai.data

import com.example.BuildConfig

object AiConfig {
    /**
     * Provider: Atria ASI
     * Official Docs: https://api.atria-asi.ai/docs
     */
    const val PROVIDER_NAME = "Atria ASI"

    /**
     * ✅ API Key now comes from BuildConfig which is populated from `.env`
     *    via the Secrets Gradle Plugin. Never hardcode keys in source.
     *
     * Add this line to `.env`:
     *   ATRIA_API_KEY=atr_xxxxxxxxxxxxxxxxxxxx
     */
    val DEFAULT_API_KEY: String
        get() = BuildConfig.ATRIA_API_KEY

    /**
     * Default Base URL for Atria ASI
     */
    const val DEFAULT_BASE_URL = "https://api.atria-asi.ai/v1/"

    /**
     * Official Atria Model
     */
    const val DEFAULT_MODEL = "Atria-Dawn-Preview"

    /**
     * Network Timeouts (seconds)
     */
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 60L
    const val WRITE_TIMEOUT_SECONDS = 30L

    /**
     * Free Tier Daily Limit for non-VIP users
     */
    const val FREE_DAILY_REQUEST_LIMIT = 5
}