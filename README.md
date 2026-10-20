# Dynamic Island Android

A Dynamic Island-inspired Android UI prototype built with **Kotlin** and **Jetpack Compose**.

## Current features

- Animated pill that expands and collapses when tapped
- Simulated now-playing state with animated audio bars
- Play/pause demo control
- Live-notification preview toggle
- Dark UI with responsive Compose layout

> **Current scope:** this is an in-app UI prototype. It does not yet draw over other apps or read real media sessions and notifications. Those require additional Android permissions and platform integrations.

## Tech stack

- Kotlin
- Jetpack Compose + Material 3
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- JDK 17
- Minimum Android API 26 (Android 8.0)

## Open and run

1. Install Android Studio and a JDK 17-compatible Android toolchain.
2. Clone this repository or use **Code → Download ZIP** on GitHub.
3. Open the project root in Android Studio.
4. Allow Gradle sync to finish and install the Android SDK platform required by the project (API 35).
5. Select an emulator or connected Android device, then press **Run**.

If Android Studio asks to install a compatible Gradle version or SDK components, allow it to do so.

## Project structure

```
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/dynamicisland/MainActivity.kt
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Planned next steps

1. Add a proper app theme and accessibility polish.
2. Integrate real playback information through Android media-session APIs.
3. Add notification-listener integration with explicit user permission.
4. Explore a system overlay mode with the required special permission and lifecycle handling.

## License

No license has been added yet. All rights remain with the repository owner unless a license is added.
