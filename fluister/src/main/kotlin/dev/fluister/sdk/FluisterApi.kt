package dev.fluister.sdk

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

internal class FluisterApi(private val apiKey: String) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    
    private val baseUrl = "https://fluister.dev/api"
    
    suspend fun fetchConfig(): Result<WidgetConfig> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/widget/config?project=$apiKey")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return@withContext Result.failure(
                    Exception("Empty response body")
                )
                val json = JSONObject(body)
                val config = WidgetConfig(
                    emailFollowupEnabled = json.optBoolean("email_followup_enabled", false)
                )
                Result.success(config)
            } else {
                Result.failure(Exception("Config fetch failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun submitFeedback(
        sentiment: Sentiment,
        message: String?,
        pageUrl: String?,
        sessionId: String?,
        userEmail: String?,
        emailFollowupOptedIn: Boolean,
        appVersion: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val metadata = JSONObject().apply {
                put("app_version", appVersion)
                put("android_version", Build.VERSION.RELEASE)
                put("device_model", "${Build.MANUFACTURER} ${Build.MODEL}")
            }
            
            val payload = JSONObject().apply {
                put("sentiment", sentiment.value)
                if (message != null) put("message", message)
                if (pageUrl != null) put("page_url", pageUrl)
                put("platform", "android")
                if (sessionId != null) put("session_id", sessionId)
                if (userEmail != null) put("user_email", userEmail)
                put("email_followup_opted_in", emailFollowupOptedIn)
                put("metadata", metadata)
            }
            
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = payload.toString().toRequestBody(mediaType)
            
            val request = Request.Builder()
                .url("$baseUrl/feedback")
                .addHeader("X-API-Key", apiKey)
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Feedback submission failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
