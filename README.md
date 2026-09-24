# PosterFlow

Android poster and video maker built with Kotlin, Jetpack Compose, Room, and Firebase Google sign-in.

## Run locally on Windows

1. Open this folder in Android Studio and sync Gradle.
2. Start a Google Play emulator in Device Manager, or connect an Android phone with USB debugging enabled.
3. Select the `app` configuration and click Run.

Alternatively, after starting your emulator, run from this folder:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-local.ps1
```

The script builds a fresh debug APK, installs it on `emulator-5554`, and launches PosterFlow. Pass `-DeviceId <serial>` to use another device.

## Configuration on this PC

- `local.properties` points to `C:/Users/saideep/AppData/Local/Android/Sdk`.
- Android SDK platform 36.1 is required; it is installed on this PC.
- The wrapper uses Gradle 9.3.1. The run script uses Android Studio's bundled Java.
- Keep `debug.keystore` and the debug signing configuration.
- Build outputs stay on the same drive as the source to support Android Studio sync on Windows. After a successful build, the APK is at `app\build\outputs\apk\debug\app-debug.apk` inside this project.
- If the emulator fails due to memory, launch it with 2 GB RAM. This PC's existing AVD is `Society_API_37`.

## Google sign-in

The app uses `app/google-services.json`. Firebase must have Google sign-in enabled and the debug signing certificate registered. Use a Google Play emulator with a Google account, or an Android phone with Google Play services.

The active Firebase login flow does not need a `.env` file. The included Supabase repository and Gemini placeholders are not used by the current app entry point.

Release publishing requires a separate upload keystore and signing passwords. Local runs use debug builds.
