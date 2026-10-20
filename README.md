# Dynamic Island Android

A clean native Android baseline for the Dynamic Island project, configured with Groovy Gradle files for compatibility with Android IDEs that scan `settings.gradle` and `app/build.gradle`.

## Project setup

- Root project: `settings.gradle` and `build.gradle`
- Android application module: `app/build.gradle`
- Android SDK: 35
- Minimum SDK: 26
- Java: 17
- Android Gradle Plugin: 8.7.3
- Gradle: 8.9

Open/import the repository root (the directory containing `settings.gradle`) in Code On The Go. The application module is `:app`.

## Build validation

GitHub Actions builds the debug APK on a hosted Linux runner and uploads it as an artifact. No emulator is used.

## Current scope

This baseline intentionally uses a native Android Activity with no third-party runtime dependencies. First goal: verify the app builds, installs, and opens reliably. Overlay permissions and Dynamic Island behavior will be added after startup is verified.
