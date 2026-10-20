# Dynamic Island Android

A clean Android baseline for the Dynamic Island project.

## Build

This project targets Android SDK 35 and uses Java 17 and Gradle 8.9. It includes a lightweight gradlew launcher for environments where the standard Gradle Wrapper JAR is unavailable. The first invocation downloads Gradle 8.9, so the device or IDE must have network access.

In Code On The Go, open/import the project root. If COTG expects the standard Gradle Wrapper JAR rather than invoking gradlew, use its installed Gradle option or share the exact build error so the project can be adjusted for that version.

GitHub Actions builds a debug APK on a hosted Linux runner and uploads it as an artifact. No emulator is used.

## Current scope

This baseline intentionally uses a native Android Activity with no third-party runtime dependencies. First goal: verify the app builds, installs, and opens reliably. Overlay permissions and Dynamic Island behavior will be added after this baseline passes.
