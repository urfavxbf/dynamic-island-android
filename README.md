# Dynamic Island Android

A Dynamic Island-inspired Android UI prototype built with **Kotlin** and **Jetpack Compose**, prepared for development on an Android phone using **Code on the Go**.

## Current features

- Animated pill that expands and collapses when tapped
- Simulated now-playing state with animated audio bars
- Play/pause demo control
- Live-notification preview toggle
- Dark UI with a Compose layout

> **Current scope:** this is an in-app UI prototype. It does not yet draw over other apps or read real media sessions and notifications.

## Build environment

This project is configured for the current Code on the Go toolchain:

- Android Gradle Plugin 9.3.1
- Gradle 9.6.1
- Kotlin 2.3.21 / Compose compiler plugin
- Java 17 language target
- Android SDK platform 36
- Minimum Android API 26

Code on the Go releases and bundled toolchains can change. If your installed version uses a different toolchain, follow its migration prompt or let me know the exact error shown.

## Open and run using only your phone

1. Install or update Code on the Go from the [official website](https://appdevforall.org/code-on-the-go/).
2. Open the app and finish its first-time setup. Allow it to install/download its build assets; several gigabytes of free storage may be needed.
3. Open the project/import screen and choose **Import/Open existing Gradle project** (wording may differ by app version).
4. Select the downloaded or cloned `dynamic-island-android` project folder. If starting from GitHub, download the repository as a ZIP, then extract it using your phone's file manager.
5. Wait for Gradle sync/dependency resolution to finish. Internet access may be needed the first time.
6. Select the `app` run configuration and build/run the debug app on your phone.

If the app does not offer a project import option, check the current [Code on the Go help pages](https://appdevforall.org/codeonthego/help/index.html).

## Project structure

    .
    ├── app/
    │   ├── build.gradle.kts
    │   └── src/main/
    │       ├── AndroidManifest.xml
    │       └── java/com/example/dynamicisland/MainActivity.kt
    ├── gradle/wrapper/gradle-wrapper.properties
    ├── build.gradle.kts
    ├── gradle.properties
    └── settings.gradle.kts

## Planned next steps

1. Improve accessibility and polish the UI.
2. Integrate real playback information through Android media-session APIs.
3. Add notification-listener integration with explicit user permission.
4. Explore system overlay mode with the required special permission and lifecycle handling.

## License

No license has been added yet. All rights remain with the repository owner unless a license is added.
