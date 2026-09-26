package com.example.ai.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<ChatMessage>,
    @Json(name = "temperature") val temperature: Float? = 0.7f,
    @Json(name = "max_tokens") val maxTokens: Int? = 1200
)

@JsonClass(generateAdapter = true)
data class ChatChoice(
    @Json(name = "index") val index: Int? = null,
    @Json(name = "message") val message: ChatMessage,
    @Json(name = "finish_reason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "id") val id: String? = null,
    @Json(name = "choices") val choices: List<ChatChoice>? = null,
    @Json(name = "created") val created: Long? = null,
    @Json(name = "model") val model: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiErrorDetail(
    @Json(name = "message") val message: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "code") val code: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiErrorResponse(
    @Json(name = "error") val error: ApiErrorDetail? = null
)
