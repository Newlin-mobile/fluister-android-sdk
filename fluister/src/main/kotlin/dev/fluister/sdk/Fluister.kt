package dev.fluister.sdk

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
     */
    fun configure(context: Context, apiKey: String) {
        config = FluisterConfig(
            apiKey = apiKey,
            context = context.applicationContext
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
