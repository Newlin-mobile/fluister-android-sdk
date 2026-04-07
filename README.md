# Fluister Android SDK

Native Android SDK for collecting user feedback via the Fluister platform. Provides a beautiful, customizable bottom sheet UI built with Jetpack Compose.

![Fluister Android SDK](https://img.shields.io/badge/platform-Android-green.svg)
![Minimum SDK](https://img.shields.io/badge/minSdk-24-blue.svg)

## Features

- ✨ **Native Compose UI** - Beautiful Material3 bottom sheet
- 😊 **Emoji Sentiment** - Quick emotional feedback with 4 emoji options
- ⚡ **Lightweight** - Minimal dependencies (Compose + OkHttp)
- 🔒 **Privacy-First** - GDPR-compliant, optional email collection
- 🎨 **Customizable** - Match your app's design
- 📱 **Android 7+** - Support for API 24+

## Installation

### Step 1: Add JitPack repository

Add JitPack to your root `build.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add dependency

Add Fluister SDK to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Newlin-mobile:fluister-android-sdk:1.0.0")
}
```

## Quick Start

### 1. Configure in Application class

```kotlin
import android.app.Application
import dev.fluister.sdk.Fluister

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Fluister.configure(
            context = this,
            apiKey = "your_fluister_project_api_key"
        )
    }
}
```

Don't forget to register your Application class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApp"
    ...>
```

### 2. Add the feedback sheet to your activity

```kotlin
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.fluister.sdk.FluisterFeedbackSheet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                // Your app content
                MyScreen()
                
                // Add feedback sheet
                FluisterFeedbackSheet()
            }
        }
    }
}
```

### 3. Trigger the feedback widget

**Option A: Use the built-in FAB**

```kotlin
import dev.fluister.sdk.FluisterFeedbackButton

@Composable
fun MyScreen() {
    Scaffold(
        floatingActionButton = {
            FluisterFeedbackButton()
        }
    ) {
        // Your content
    }
}
```

**Option B: Custom button**

```kotlin
import dev.fluister.sdk.Fluister

Button(onClick = { Fluister.show(context) }) {
    Text("Give Feedback")
}
```

## Advanced Usage

### Set screen/page context

```kotlin
Fluister.setScreen("HomeScreen")
Fluister.setScreen("ProductDetail-${productId}")
```

### Pre-fill user email

```kotlin
Fluister.setUserEmail("user@example.com")
```

### Full example

```kotlin
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set context
        Fluister.setScreen("ProfileScreen")
        Fluister.setUserEmail(userRepository.getCurrentUserEmail())
        
        setContent {
            // Your UI + feedback sheet
        }
    }
}
```

## API Reference

### Fluister

```kotlin
// Configure SDK (call once in Application.onCreate)
Fluister.configure(context: Context, apiKey: String)

// Show feedback sheet
Fluister.show(context: Context)

// Set current screen/page
Fluister.setScreen(screenName: String)

// Set user email
Fluister.setUserEmail(email: String?)
```

### Composables

```kotlin
// Include in your Compose hierarchy
FluisterFeedbackSheet()

// Pre-built floating action button
FluisterFeedbackButton(modifier: Modifier = Modifier)
```

## Permissions

The SDK requires internet permission (automatically included):

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Privacy & Data

The SDK collects:
- **Required:** Sentiment (emoji selection), platform ("android")
- **Optional:** Message text, user email (only if provided)
- **Automatic:** App version, Android version, device model

All data is sent to your Fluister project endpoint. Email collection requires explicit user opt-in via checkbox.

See [Fluister Privacy Policy](https://fluister.dev/privacy) for details.

## ProGuard

No special ProGuard rules needed. The SDK is minification-safe.

## Requirements

- **Min SDK:** 24 (Android 7.0)
- **Compile SDK:** 34+
- **Kotlin:** 1.9+
- **Compose:** BOM 2023.10.01+

## Get Your API Key

1. Visit [fluister.dev](https://fluister.dev)
2. Create a project
3. Copy your API key from the dashboard

## Sample App

See the `app/` module in this repository for a complete working example.

To run:
```bash
git clone https://github.com/Newlin-mobile/fluister-android-sdk.git
cd fluister-android-sdk
./gradlew :app:installDebug
```

## License

MIT License - see LICENSE file

## Support

- 🐛 **Issues:** [GitHub Issues](https://github.com/Newlin-mobile/fluister-android-sdk/issues)
- 📧 **Email:** support@fluister.dev
- 📖 **Docs:** [fluister.dev/docs](https://fluister.dev/docs)

## Contributing

Contributions welcome! Please open an issue first to discuss changes.

---

Made with ❤️ for Android developers
