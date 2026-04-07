# Changelog

All notable changes to the Fluister Android SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-04-07

### Added

- **Core SDK functionality**
  - Native Jetpack Compose feedback widget with Material3 design
  - Bottom sheet UI with emoji sentiment picker (😍 love, 😊 happy, 😐 neutral, 😢 sad)
  - Multiline message input field
  - Conditional email collection with opt-in checkbox
  - HTTP client for Fluister backend API (OkHttp-based)
  - Automatic metadata collection (app version, Android version, device model)
  - Session ID tracking (UUID per app session)
  - Screen/page context tracking via `setScreen()`
  - User email pre-fill via `setUserEmail()`

- **Google Play In-App Review integration** 🆕
  - Automatically triggers native review prompt after positive feedback (love/happy)
  - Enabled by default, configurable via `reviewPromptEnabled` parameter
  - Graceful fallback if Play Store unavailable (emulator/sideload apps)
  - Respects Google's quota limits to prevent review request fatigue
  - Converts happy moments into 5-star ratings seamlessly

- **Components**
  - `FluisterFeedbackSheet()` - Main bottom sheet composable
  - `FluisterFeedbackButton()` - Pre-styled floating action button
  - `SentimentPicker()` - Emoji selector with scale animation
  - `Fluister` singleton - Public API for configuration and triggering

- **API**
  - `Fluister.configure(context, apiKey, reviewPromptEnabled)` - SDK setup
  - `Fluister.show(context)` - Trigger feedback sheet
  - `Fluister.setScreen(name)` - Set current screen context
  - `Fluister.setUserEmail(email)` - Pre-fill user email

- **Sample app**
  - Complete working example with multiple trigger patterns
  - Demonstrates FAB, custom button, and email pre-fill

- **Documentation**
  - Comprehensive README with installation and quick start
  - USAGE.md with advanced integration patterns
  - DEVELOPMENT.md for contributors
  - QUICK_START.md for rapid integration
  - PROJECT_SUMMARY.md with complete feature overview

### Dependencies

- Jetpack Compose BOM 2023.10.01
- Material3
- Kotlin Coroutines 1.7.3
- OkHttp 4.12.0
- Google Play In-App Review 2.0.1
- AndroidX Core KTX 1.12.0

### Compatibility

- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Kotlin: 1.9.20
- JDK: 17

### Distribution

- Published via JitPack: `com.github.Newlin-mobile:fluister-android-sdk:1.0.0`
- MIT License
- Public GitHub repository

[1.0.0]: https://github.com/Newlin-mobile/fluister-android-sdk/releases/tag/v1.0.0
