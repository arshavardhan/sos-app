ShakeToSOS is a Flutter-based emergency alert app that allows users to send an SOS message with their location by shaking their phone. The app runs a **background service** to detect shakes, even when the app is closed or the phone is rebooted.

---

## Features

- **Shake Detection**
  - Detects 3 shakes within 5 seconds to trigger SOS.
  
- **Emergency Alerts**
  - Sends pre-filled SMS messages to saved emergency contacts.
  - Opens WhatsApp with a pre-filled SOS message.
  - Includes Google Maps link with live location.

- **Persistent Background Service**
  - Foreground service runs continuously with a notification: *“Shake-to-SOS is active.”*
  - Auto-starts on device boot using `RECEIVE_BOOT_COMPLETED` permission.

- **Emergency Contacts Management**
  - Add, remove, and manage contacts.
  - Stored locally using `SharedPreferences`.

- **Test SOS**
  - Test button to simulate shake detection and SOS delivery.

- **Flutter & Android Setup**
  - Flutter 3.35+ / Dart 3 compatible
  - Android V2 embedding
  - compileSdk 34, minSdk 24, targetSdk 34
  - Gradle 8.3, Kotlin 1.9.10

---

## Screenshots

*(Add your app screenshots here)*

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-username/shaketosos.git
cd shaketosos
2. Install dependencies
bash
Copy code
flutter pub get
3. Run the app
bash
Copy code
flutter run
After reboot, the background service will start automatically.

Usage
Open the app and go to Emergency Contacts to add phone numbers.

Shake the phone 3 times quickly to trigger the SOS alert.

The app will open SMS/WhatsApp apps with a pre-filled SOS message including your location.

You can also use the Test SOS button to verify functionality.

Dependencies
Flutter

cupertino_icons

url_launcher

shared_preferences

Characters, Material Color Utilities, Meta (Dart packages)

Permissions
SEND_SMS — send SMS messages to contacts

ACCESS_FINE_LOCATION — get live location for SOS

RECEIVE_BOOT_COMPLETED — restart background service after reboot

FOREGROUND_SERVICE — run continuous shake detection service

Contributing
Fork the repository

Create a feature branch (git checkout -b feature/your-feature)

Commit your changes (git commit -am 'Add new feature')

Push to the branch (git push origin feature/your-feature)

Open a Pull Request

