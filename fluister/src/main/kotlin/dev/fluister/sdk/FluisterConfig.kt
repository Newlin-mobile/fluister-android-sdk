package dev.fluister.sdk

import android.content.Context

internal data class FluisterConfig(
    val apiKey: String,
    val context: Context,
    val reviewPromptEnabled: Boolean = true
)

internal data class WidgetConfig(
    val emailFollowupEnabled: Boolean = false
)

internal data class FeedbackPayload(
    val sentiment: String,
    val message: String? = null,
    val page_url: String? = null,
    val platform: String = "android",
    val session_id: String? = null,
    val user_email: String? = null,
    val email_followup_opted_in: Boolean = false,
    val metadata: FeedbackMetadata
)

internal data class FeedbackMetadata(
    val app_version: String,
    val android_version: String,
    val device_model: String
)

enum class Sentiment(val value: String, val emoji: String) {
    LOVE("love", "😍"),
    HAPPY("happy", "😊"),
    NEUTRAL("neutral", "😐"),
    SAD("sad", "😢")
}
