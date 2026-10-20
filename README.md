# Dynamic Island Android

Kotlin Android app built with Jetpack Compose. This branch temporarily opens a native diagnostic screen first so a device-specific startup crash can be investigated without needing ADB.

## Clone on Code On The Go

1. In Code On The Go, open the project/Git import option.
2. Clone this repository:
   `https://github.com/urfavxbf/dynamic-island-android.git`
3. If you already have a local copy and Git reports checkout conflicts, do not overwrite your local edits blindly. Rename the existing project folder or clone into a new folder.
4. Open the cloned `dynamic-island-android` folder as an existing Gradle project.
5. Wait for Gradle sync to finish, then build and install the debug variant.

If the app is already cloned and you want the newest version, run this only after saving your local changes:
```bash
git status
git pull --ff-only origin main
```

## Device startup diagnostics

The current launcher is the native **Dynamic Island Diagnostics** screen. It deliberately uses Android Views rather than Compose so you can open a basic diagnostic screen even if the Compose UI is the source of the crash.

1. Launch the app and read the diagnostic screen.
2. Tap **OPEN MAIN DEMO** to try launching the main Compose screen.
3. If it closes, reopen the app and tap **REFRESH CRASH LOG**.
4. Tap **COPY CRASH LOG** and paste the report into the GitHub issue or chat where the bug is being tracked.

The crash report is stored in the app's private storage as `last_crash.txt`; the diagnostics screen displays it without requiring the `adb` command. Some native/process kills may not be caught by the uncaught-exception handler, in which case the report may remain empty.

## Build configuration

- Namespace/application ID: `com.example.dynamicisland`
- Minimum Android API: 26 (Android 8.0)
- Target SDK: 28
- Java/Kotlin JVM target: 17
- Android SDK compile platform: 36

GitHub Actions workflows build the debug APK and run an emulator launch check. Emulator success does not guarantee success on every physical phone, so please share the crash report from your device if the issue persists.

## Main source files

- `app/src/main/java/com/example/dynamicisland/MainActivity.kt` — main Compose UI
- `app/src/main/java/com/example/dynamicisland/DebugActivity.kt` — native diagnostic screen
- `app/src/main/java/com/example/dynamicisland/CrashReportingApplication.kt` — uncaught exception report writer
- `app/src/main/java/com/example/dynamicisland/IslandOverlayService.kt` — floating overlay service
- `app/src/main/AndroidManifest.xml` — app components and launcher activity
