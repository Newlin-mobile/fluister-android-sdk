package dev.fluister.sdk

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.UUID

/**
 * Fluister SDK - User feedback collection for Android apps
 * 
 * Usage:
 * 1. Configure in Application.onCreate():
 *    Fluister.configure(this, apiKey = "your_api_key")
 * 
 * 2. Show feedback widget:
 *    Fluister.show(context)
 * 
 * 3. Optional: Set context
 *    Fluister.setScreen("HomeScreen")
 *    Fluister.setUserEmail("user@example.com")
 */
object Fluister {
    
    private var config: FluisterConfig? = null
    private var api: FluisterApi? = null
    
    private val sessionId: String = UUID.randomUUID().toString()
    
    private var currentScreen: String? = null
    private var userEmail: String? = null
    
    internal val isShowing = mutableStateOf(false)
    
    /**
     * Configure the SDK. Call this once in Application.onCreate()
     * 
     * @param reviewPromptEnabled If true, shows Google Play in-app review prompt after positive feedback
     */
    fun configure(
        context: Context,
        apiKey: String,
        reviewPromptEnabled: Boolean = true
    ) {
        config = FluisterConfig(
            apiKey = apiKey,
            context = context.applicationContext,
            reviewPromptEnabled = reviewPromptEnabled
        )
        api = FluisterApi(apiKey)
    }
    
    /**
     * Show the feedback bottom sheet
     */
    fun show(context: Context) {
        requireConfigured()
        isShowing.value = true
    }
    
    /**
     * Set the current screen/page name for context
     */
    fun setScreen(screenName: String) {
        currentScreen = screenName
    }
    
    /**
     * Set user email for feedback attribution
     */
    fun setUserEmail(email: String?) {
        userEmail = email
    }
    
    /**
     * Dismiss the feedback sheet
     */
    internal fun dismiss() {
        isShowing.value = false
    }
    
    /**
     * Get the API client
     */
    internal fun getApi(): FluisterApi {
        return api ?: throw IllegalStateException("Fluister not configured")
    }
    
    /**
     * Get current context data
     */
    internal fun getContextData(): FeedbackContext {
        val ctx = config?.context ?: throw IllegalStateException("Fluister not configured")
        return FeedbackContext(
            pageUrl = currentScreen,
            sessionId = sessionId,
            userEmail = userEmail,
            appVersion = getAppVersion(ctx)
        )
    }
    
    /**
     * Request in-app review after positive feedback
     * Internal use only - called from FeedbackSheet after successful submit
     */
    internal fun requestInAppReview(context: Context, sentiment: Sentiment) {
        val cfg = config ?: return
        
        // Only trigger for positive sentiments
        if (!cfg.reviewPromptEnabled) return
        if (sentiment != Sentiment.LOVE && sentiment != Sentiment.HAPPY) return
        
        // Context must be an Activity
        val activity = context as? Activity ?: run {
            Log.w("Fluister", "In-app review requires Activity context, got ${context.javaClass.simpleName}")
            return
        }
        
        try {
            val reviewManager = ReviewManagerFactory.create(cfg.context)
            val requestReviewFlow = reviewManager.requestReviewFlow()
            
            requestReviewFlow.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    reviewManager.launchReviewFlow(activity, reviewInfo).addOnCompleteListener {
                        // Review flow completed (user may or may not have left review)
                        Log.d("Fluister", "In-app review flow completed")
                    }
                } else {
                    // Failed to get review flow (e.g., quota exceeded, not on Play Store)
                    Log.d("Fluister", "In-app review not available: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            // Graceful fallback - don't crash the app
            Log.w("Fluister", "In-app review failed: ${e.message}")
        }
    }
    
    private fun requireConfigured() {
        if (config == null) {
            throw IllegalStateException(
                "Fluister not configured. Call Fluister.configure() in Application.onCreate()"
            )
        }
    }
    
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }
}

internal data class FeedbackContext(
    val pageUrl: String?,
    val sessionId: String,
    val userEmail: String?,
    val appVersion: String
)
