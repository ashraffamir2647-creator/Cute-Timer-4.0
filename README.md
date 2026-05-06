# 🌸 Cute Timer - Android App

A kawaii-themed countdown timer app with a bouncy star mascot, pastel colors, and cute animations!

## ✨ Features
- Set custom minutes & seconds with ▲▼ buttons
- Quick preset buttons: 1, 3, 5, 10 minutes
- Animated star mascot that bounces while counting down
- Purple-to-pink gradient progress ring
- Vibration + sound alert when time's up
- Soft lavender background with pastel UI

---

## 🔨 How to Build the APK

### Prerequisites
- **Android Studio** (recommended) — [Download here](https://developer.android.com/studio)
- OR **JDK 11+** and **Android SDK** command-line tools

---

### Option A: Android Studio (Easiest)

1. Open Android Studio
2. Click **"Open"** → select the `CuteTimer` folder
3. Wait for Gradle to sync (first time may take a few minutes)
4. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
5. APK will be at:
   `app/build/outputs/apk/debug/app-debug.apk`
6. Transfer to your phone and install!

---

### Option B: Command Line

```bash
# 1. Navigate to project folder
cd CuteTimer

# 2. Make gradlew executable
chmod +x gradlew

# 3. Build debug APK
./gradlew assembleDebug

# APK output:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 Install on Android

1. Enable **"Install from Unknown Sources"** on your phone:
   - Settings → Security → Unknown Sources ✅
   - (Or Settings → Apps → Special App Access → Install Unknown Apps)

2. Transfer the APK to your phone via:
   - USB cable
   - Google Drive / WhatsApp / email
   - ADB: `adb install app-debug.apk`

3. Tap the APK file on your phone to install

---

## 🎨 App Design

| Element | Color |
|---------|-------|
| Background | Soft Lavender `#F0F5FF` |
| Primary | Purple `#7C4DFF` |
| Accent | Pink `#FF6B9D` |
| Preset buttons | Pink, Purple, Mint, Yellow |

## 📁 Project Structure

```
CuteTimer/
├── app/
│   ├── src/main/
│   │   ├── java/com/cute/timer/
│   │   │   └── MainActivity.kt       ← All timer logic & animations
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml  ← UI layout
│   │   │   ├── drawable/
│   │   │   │   ├── cute_mascot.xml       ← Kawaii star character
│   │   │   │   └── circular_progress.xml ← Progress ring
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

---

Made with 💜 and lots of sparkles ✨
