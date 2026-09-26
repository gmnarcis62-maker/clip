package com.example.ai.data

object AiConfig {
    /**
     * Provider: Atria ASI
     * Official Docs: https://api.atria-asi.ai/docs
     */
    const val PROVIDER_NAME = "Atria ASI"

    /**
     * API Key: Provided for Atria ASI API gateway.
     * Bearer token format: Bearer atr_...
     */
    const val DEFAULT_API_KEY = "atr_ZtEtzGtqJmQkrO-AiemLVy2jHGzPtBoD"

    /**
     * Default Base URL for Atria ASI:
     * Endpoint: https://api.atria-asi.ai/v1/
     */
    const val DEFAULT_BASE_URL = "https://api.atria-asi.ai/v1/"

    /**
     * Official Atria Model:
     * Exact name: Atria-Dawn-Preview
     */
    const val DEFAULT_MODEL = "Atria-Dawn-Preview"

    /**
     * Network Timeouts in Seconds
     */
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 60L
    const val WRITE_TIMEOUT_SECONDS = 30L

    /**
     * Free Tier Daily Limit for non-VIP users
     */
    const val FREE_DAILY_REQUEST_LIMIT = 5
}
