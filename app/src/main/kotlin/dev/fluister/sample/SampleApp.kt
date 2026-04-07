package dev.fluister.sample

import android.app.Application
import dev.fluister.sdk.Fluister

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configure Fluister SDK
        Fluister.configure(
            context = this,
            apiKey = "demo_api_key_replace_with_yours"
        )
    }
}
