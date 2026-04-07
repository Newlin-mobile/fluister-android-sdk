# Development Guide

Guide for contributors and maintainers of the Fluister Android SDK.

## Project Structure

```
fluister-android-sdk/
├── fluister/                      # Main library module
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/dev/fluister/sdk/
│           ├── Fluister.kt        # Public API singleton
│           ├── FluisterConfig.kt  # Data models
│           ├── FluisterApi.kt     # HTTP client (OkHttp)
│           ├── FeedbackSheet.kt   # Main bottom sheet UI
│           ├── SentimentPicker.kt # Emoji selector component
│           └── FluisterButton.kt  # FAB component
├── app/                           # Sample app
│   └── src/main/kotlin/dev/fluister/sample/
│       ├── SampleApp.kt
│       └── MainActivity.kt
├── build.gradle.kts               # Root build config
├── settings.gradle.kts
└── README.md
```

## Building

### Prerequisites

- JDK 17+
- Android SDK with API 34
- Gradle 8.2+

### Build Commands

```bash
# Build library AAR
./gradlew :fluister:assembleRelease

# Build sample app
./gradlew :app:assembleDebug

# Install sample app
./gradlew :app:installDebug

# Run tests
./gradlew test

# Lint check
./gradlew lint
```

### Output

Library AAR: `fluister/build/outputs/aar/fluister-release.aar`

## Architecture

### Core Components

1. **Fluister.kt** - Singleton entry point
   - Configuration management
   - Global state (screen, email)
   - Session ID generation

2. **FluisterApi.kt** - Network layer
   - OkHttp-based HTTP client
   - Coroutine-based async operations
   - Config fetching + feedback submission

3. **FeedbackSheet.kt** - UI layer
   - Material3 ModalBottomSheet
   - State management (sentiment, message, email)
   - Loading/success/error states
   - Email opt-in checkbox (conditional)

4. **SentimentPicker.kt** - Emoji selector
   - 4 emoji buttons (love/happy/neutral/sad)
   - Scale animation on selection
   - Selection indicator

5. **FluisterButton.kt** - FAB component
   - Pre-styled Material3 FAB
   - Branded color (#667EEA)

### Data Flow

```
User Tap → Fluister.show() → isShowing.value = true
         → FluisterFeedbackSheet observes → ModalBottomSheet appears
         → User selects sentiment + message
         → Submit → FluisterApi.submitFeedback()
         → Success → Auto-dismiss after 1s
```

### API Endpoints

**Config Fetch:**
```
GET https://fluister.dev/api/widget/config?project={apiKey}
Response: { "email_followup_enabled": boolean }
```

**Feedback Submit:**
```
POST https://fluister.dev/api/feedback
Headers: X-API-Key: {apiKey}
Body: {
  "sentiment": "love|happy|neutral|sad",
  "message": "...",
  "page_url": "...",
  "platform": "android",
  "session_id": "...",
  "user_email": "...",
  "email_followup_opted_in": boolean,
  "metadata": {
    "app_version": "1.0.0",
    "android_version": "14",
    "device_model": "Google Pixel 8"
  }
}
```

## Dependencies

### Library Module

- **Jetpack Compose BOM** - UI framework
- **Material3** - Modern Material Design
- **Kotlin Coroutines** - Async operations
- **OkHttp 4.12** - HTTP client
- **Google Play In-App Review** - Store review prompts
- **AndroidX Core KTX** - Kotlin extensions

### Why OkHttp?

- Production-ready HTTP client
- Built-in connection pooling
- Timeout management
- Well-tested and maintained
- Alternative: Could use HttpURLConnection for zero-dependency (but less robust)

## Testing Locally

### 1. Sample App

```bash
./gradlew :app:installDebug
adb shell am start -n dev.fluister.sample/.MainActivity
```

### 2. Local Maven

Publish to local Maven for testing in other apps:

```bash
./gradlew :fluister:publishToMavenLocal
```

Then in your test app:

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("dev.fluister:fluister-android-sdk:1.0.0")
}
```

## JitPack Publishing

### Automatic

JitPack builds from Git tags:

```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

Then users can use:

```kotlin
implementation("com.github.Newlin-mobile:fluister-android-sdk:1.0.0")
```

### Build Requirements

- `jitpack.yml` specifies JDK 17
- Maven publishing configured in `fluister/build.gradle.kts`
- Public GitHub repository

## Version Management

Follow semantic versioning (semver):

- **MAJOR**: Breaking API changes
- **MINOR**: New features (backwards-compatible)
- **PATCH**: Bug fixes

Update version in:
1. Git tag
2. README examples
3. Library module `build.gradle.kts` (publishing block)

## Code Style

- **Kotlin official style** (configured in gradle.properties)
- 4-space indentation
- Max line length: 120 characters
- Use `internal` for non-public APIs
- Document public APIs with KDoc

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Pull Request Guidelines

- Update README if adding features
- Add tests for new functionality
- Ensure `./gradlew lint` passes
- Keep backwards compatibility when possible

## Troubleshooting Build Issues

### Gradle sync fails

```bash
./gradlew clean
./gradlew --stop
rm -rf .gradle build
./gradlew build
```

### Compose version conflicts

Ensure all modules use the same Compose BOM version.

### JDK version issues

Verify JDK 17 is active:

```bash
java -version  # Should show 17
```

## Release Checklist

- [ ] Update version number
- [ ] Update CHANGELOG.md
- [ ] Test sample app
- [ ] Run `./gradlew lint`
- [ ] Update README with any API changes
- [ ] Create Git tag
- [ ] Push tag to GitHub
- [ ] Verify JitPack build succeeds
- [ ] Test installation from JitPack

## Roadmap Ideas

- Dark mode support (theme awareness)
- Screenshot attachment
- Offline queueing (submit when online)
- Customizable colors/theming
- Localization (i18n)
- Crash reporting integration
- Analytics events

## License

MIT License - see LICENSE file for details.
