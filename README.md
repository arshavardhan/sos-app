# ShakeToSOS - Flutter scaffold (DELIVERABLE ZIP)

This archive contains a Flutter project scaffold + Android native files to implement:

- Background foreground Service that monitors accelerometer for shakes (3 shakes within 5s).
- BootReceiver to auto-start the service after device reboot.
- MethodChannel bridge to toggle the service from Flutter.
- Prefilled SMS & WhatsApp intents when SOS triggers (will open respective apps).

IMPORTANT:
- This is a **scaffold** intended to be imported into Android Studio / opened with your Flutter environment.
- You must run `flutter pub get`, grant runtime permissions (LOCATION, SEND_SMS, etc.), and build the APK from your machine.
- The Android native code assumes V2 embedding (MainActivity extends FlutterActivity).
- Gradle plugin versions are set to 8.3.0 and Kotlin 1.9.10; Android compileSdk/targetSdk = 34; minSdk = 24.

How to import:
1. Unzip `ShakeToSOS.zip`.
2. Open the folder in Android Studio (it will prompt to update Gradle & download necessary SDKs).
3. From Flutter: run `flutter pub get` (or use the IDE's prompt).
4. Build & Run. You may need to grant runtime permissions on first run.

Notes & Limitations:
- The service uses a **simple** accelerometer threshold; you might want to refine the algorithm.
- Opening SMS/WhatsApp from a background service starts the respective activity; on some OEMs this may be restricted.
- For production, implement runtime permission requests in Flutter and handle OS-specific battery optimizations (auto-start policies vary by vendor).
- If you want a ready-signed APK, build a debug APK locally or request that I include one (I can provide a debug APK in this package on request).

