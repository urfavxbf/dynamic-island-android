# Dynamic Island Android

A clean Android baseline for the Dynamic Island project.

## Build

The GitHub Actions workflow builds a debug APK on GitHub-hosted Linux runners and uploads it as an artifact.

For local Android development, open this project in Android Studio or import it into a compatible Android IDE with Android SDK Platform 35 installed.

## Current scope

This baseline intentionally uses a native Android Activity with no third-party runtime dependencies. First goal: verify the app builds, installs, and opens reliably. Overlay permissions and Dynamic Island behavior will be added after this baseline passes.
