# Fluister Android SDK - Usage Guide

Complete guide for integrating and customizing the Fluister feedback widget in your Android app.

## Table of Contents

1. [Installation](#installation)
2. [Basic Setup](#basic-setup)
3. [Triggering Feedback](#triggering-feedback)
4. [Context & Metadata](#context--metadata)
5. [Compose Integration](#compose-integration)
6. [Advanced Configuration](#advanced-configuration)
7. [Troubleshooting](#troubleshooting)

## Installation

### Via JitPack

Add to your root `build.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Newlin-mobile:fluister-android-sdk:1.0.0")
}
```

Sync your project.

## Basic Setup

### 1. Create Application Class

```kotlin
package com.example.myapp

import android.app.Application
import dev.fluister.sdk.Fluister

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configure Fluister
        Fluister.configure(
            context = this,
            apiKey = "your_fluister_api_key_here"
        )
    }
}
```

### 2. Register in AndroidManifest.xml

```xml
<manifest>
    <application
        android:name=".MyApp"
        ...>
        <!-- activities -->
    </application>
</manifest>
```

### 3. Add Feedback Sheet to Activity

```kotlin
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.fluister.sdk.FluisterFeedbackSheet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface {
                    MyAppContent()
                    
                    // Add feedback sheet
                    FluisterFeedbackSheet()
                }
            }
        }
    }
}
```

## Triggering Feedback

### Option 1: Built-in FAB

```kotlin
@Composable
fun MyScreen() {
    Scaffold(
        floatingActionButton = {
            FluisterFeedbackButton()
        }
    ) { padding ->
        // Your content
    }
}
```

### Option 2: Custom Button

```kotlin
@Composable
fun MyScreen() {
    val context = LocalContext.current
    
    Button(onClick = { Fluister.show(context) }) {
        Icon(Icons.Default.Feedback, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Send Feedback")
    }
}
```

### Option 3: From Non-Compose Code

```kotlin
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        findViewById<View>(R.id.feedback_button).setOnClickListener {
            Fluister.show(this)
        }
    }
}
```

## Context & Metadata

### Set Current Screen

Track which screen the user is on when giving feedback:

```kotlin
class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val productId = intent.getStringExtra("product_id")
        Fluister.setScreen("ProductDetail-$productId")
        
        // ... rest of setup
    }
}
```

### Pre-fill User Email

If you know the user's email, pre-fill it:

```kotlin
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userEmail = authRepository.getCurrentUserEmail()
        Fluister.setUserEmail(userEmail)
        
        // ... rest of setup
    }
}
```

### Clear User Email

When user logs out:

```kotlin
fun onLogout() {
    Fluister.setUserEmail(null)
    // ... other logout logic
}
```

## Compose Integration

### Full Example

```kotlin
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.fluister.sdk.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Fluister.setScreen("Home")
        
        setContent {
            MaterialTheme {
                HomeScreen()
                FluisterFeedbackSheet()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My App") })
        },
        floatingActionButton = {
            FluisterFeedbackButton()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Your content
        }
    }
}
```

### Multiple Screens with Navigation

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = "home") {
        composable("home") {
            LaunchedEffect(Unit) {
                Fluister.setScreen("Home")
            }
            HomeScreen()
        }
        
        composable("settings") {
            LaunchedEffect(Unit) {
                Fluister.setScreen("Settings")
            }
            SettingsScreen()
        }
    }
    
    // Add feedback sheet once at root level
    FluisterFeedbackSheet()
}
```

## Advanced Configuration

### Automatic Metadata

The SDK automatically collects:

- **App version** - from `versionName` in build.gradle
- **Android version** - device OS version
- **Device model** - manufacturer + model (e.g., "Google Pixel 8")
- **Session ID** - unique per app session (UUID)

### Email Collection

Email field only appears if enabled in your Fluister project settings:

1. Go to fluister.dev dashboard
2. Navigate to your project settings
3. Enable "Email follow-up"

Users see a checkbox to opt-in to follow-up emails.

### Custom Triggers

Show feedback after specific events:

```kotlin
class CheckoutActivity : ComponentActivity() {
    private fun onOrderCompleted() {
        // Order completed successfully
        
        lifecycleScope.launch {
            delay(2000) // Wait 2 seconds
            Fluister.setScreen("OrderComplete")
            Fluister.show(this@CheckoutActivity)
        }
    }
}
```

### Conditional Display

```kotlin
@Composable
fun MyScreen() {
    val userLevel by remember { userRepository.userLevel }
    
    Scaffold(
        floatingActionButton = {
            // Only show feedback button to premium users
            if (userLevel == "premium") {
                FluisterFeedbackButton()
            }
        }
    ) { /* content */ }
}
```

## Troubleshooting

### Sheet doesn't appear

1. **Check configuration** - Did you call `Fluister.configure()` in Application.onCreate()?
2. **Check FluisterFeedbackSheet** - Is it included in your Compose hierarchy?
3. **Check context** - Are you passing a valid context to `Fluister.show()`?

### Network errors

- Verify your API key is correct
- Check internet permission in manifest
- Test on a real device (emulator may have network issues)
- Check Logcat for error messages

### ProGuard issues

The SDK is ProGuard-safe. If you encounter issues, add:

```proguard
-keep class dev.fluister.sdk.** { *; }
```

### Compose version conflicts

The SDK uses Compose BOM 2023.10.01. If you have version conflicts:

```kotlin
dependencies {
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation(project(":fluister"))
}
```

## Best Practices

1. **Set screen names** - Call `setScreen()` in each Activity/Screen for context
2. **Pre-fill email** - If authenticated, pre-fill user email
3. **Strategic placement** - Don't overwhelm users; place feedback prompts thoughtfully
4. **Test thoroughly** - Verify feedback submissions reach your Fluister dashboard

## API Reference

### Fluister Object

```kotlin
object Fluister {
    // Configure SDK (call once in Application.onCreate)
    fun configure(context: Context, apiKey: String)
    
    // Show feedback bottom sheet
    fun show(context: Context)
    
    // Set current screen/page for context
    fun setScreen(screenName: String)
    
    // Set user email (null to clear)
    fun setUserEmail(email: String?)
}
```

### Composables

```kotlin
// Feedback bottom sheet (required)
@Composable
fun FluisterFeedbackSheet()

// Pre-built FAB (optional)
@Composable
fun FluisterFeedbackButton(modifier: Modifier = Modifier)
```

## Support

Need help? 
- 📧 Email: support@fluister.dev
- 🐛 Issues: [GitHub Issues](https://github.com/Newlin-mobile/fluister-android-sdk/issues)
- 📖 Docs: [fluister.dev/docs](https://fluister.dev/docs)
