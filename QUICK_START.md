# Quick Start Checklist

Get the Fluister Android SDK up and running in 5 minutes.

## ✅ Installation (2 minutes)

### 1. Add JitPack Repository

Open your **root** `build.gradle.kts` or `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }  // ← Add this
    }
}
```

### 2. Add Dependency

Open your **app** `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Newlin-mobile:fluister-android-sdk:1.0.0")  // ← Add this
    // ... other dependencies
}
```

Click **Sync Now**.

## ✅ Configuration (2 minutes)

### 3. Create Application Class

Create `MyApp.kt` (or use existing Application class):

```kotlin
package com.yourapp  // ← Your package name

import android.app.Application
import dev.fluister.sdk.Fluister

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Fluister.configure(
            context = this,
            apiKey = "your_fluister_api_key"  // ← Get from fluister.dev
        )
    }
}
```

### 4. Register in Manifest

Open `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApp"  ← Add this line
    android:label="@string/app_name"
    ...>
```

## ✅ Integration (1 minute)

### 5. Add to Your Activity

**If using Jetpack Compose:**

```kotlin
import dev.fluister.sdk.FluisterFeedbackSheet
import dev.fluister.sdk.FluisterFeedbackButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Scaffold(
                        floatingActionButton = {
                            FluisterFeedbackButton()  // ← Add FAB
                        }
                    ) {
                        // Your content
                    }
                    
                    FluisterFeedbackSheet()  // ← Add sheet
                }
            }
        }
    }
}
```

**If using XML layouts:**

```kotlin
import dev.fluister.sdk.Fluister

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.feedback_button).setOnClickListener {
            Fluister.show(this)  // ← Trigger feedback
        }
    }
}
```

## ✅ Test It!

Run your app and tap the feedback button. You should see:

1. Bottom sheet slides up
2. 4 emoji sentiments (😍 😊 😐 😢)
3. Message text field
4. Submit button

Select an emoji, optionally add a message, and tap Submit!

## 🎉 Done!

That's it. Your app now collects user feedback.

## 📋 Next Steps

### Optional but Recommended

**Set screen context:**

```kotlin
Fluister.setScreen("HomeScreen")  // Call in each screen/activity
```

**Pre-fill user email:**

```kotlin
Fluister.setUserEmail(user.email)  // If user is logged in
```

**Check your dashboard:**

Go to [fluister.dev](https://fluister.dev) to see feedback submissions.

## 🆘 Troubleshooting

### Bottom sheet doesn't appear?

1. Did you add `FluisterFeedbackSheet()` to your Compose tree?
2. Did you call `Fluister.configure()` in Application.onCreate()?
3. Is the FAB/button actually calling `Fluister.show(context)`?

### Build errors?

1. Make sure JitPack repository is added
2. Sync Gradle (File → Sync Project with Gradle Files)
3. Clean build: `./gradlew clean build`

### Runtime crashes?

Check Logcat for error messages. Common issues:
- Missing `Fluister.configure()` → crashes on `show()`
- Wrong context passed to `show()` → sheet won't display

## 📚 Learn More

- **Full docs:** [README.md](README.md)
- **Usage guide:** [USAGE.md](USAGE.md)
- **Code examples:** See `app/` module in this repo

## 💬 Need Help?

- 🐛 Issues: https://github.com/Newlin-mobile/fluister-android-sdk/issues
- 📧 Email: support@fluister.dev
