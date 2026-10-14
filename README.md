<p align="center">
  <img src="assets/banner.png" alt="Wafeer Banner" width="100%">
</p>

<p align="center">
  <a href="https://github.com/biZenX/Wafeer/releases/latest"><img src="https://img.shields.io/badge/Release-v1.3.24-4CAF50?style=for-the-badge&logo=github" alt="Latest Release"></a>
  <img src="https://img.shields.io/badge/Platform-Android_%7C_Wear_OS-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform">
  <img src="https://img.shields.io/badge/Privacy-100%25_Offline-009688?style=for-the-badge&logo=shield" alt="Privacy">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=for-the-badge" alt="License"></a>
</p>

<h1 align="center">Wafeer (وفير)</h1>

<p align="center">
  <b>A fast, elegant, offline-first personal expense and budget manager for Android and Wear OS.</b><br>
  <b>تطبيق ذكي، سريع، ويعمل أوفلاين بالكامل لإدارة المصاريف والميزانية اليومية والشهرية بخصوصية تامة.</b>
</p>

<p align="center">
  <a href="https://github.com/biZenX/Wafeer/releases/latest/download/wafeer-foss-release.apk">
    <img src="assets/badge_github.png" alt="Download APK" height="50">
  </a>
  <a href="https://play.google.com/store/apps/details?id=com.wafeer.app">
    <img src="assets/badge_playstore.png" alt="Get on Google Play" height="50">
  </a>
</p>

---

## 🌟 Key Features / أبرز المميزات

<div align="center">

| Feature (الميزة) | Description (الوصف) |
| :--- | :--- |
| 🧮 **Instant Numpad Calculator** | Fast single-tap expense logging with an interactive numpad calculator — zero multi-step forms. |
| 🎯 **Micro-Goal Jar (برطمان الأهداف)** | Automated daily savings pacer that helps you achieve financial micro-goals effortlessly. |
| ⚖️ **Affordability Check (أقدر أشتريها ولا لأ؟)** | Instant simulator showing exactly how any purchase impacts your remaining daily budget before you pay. |
| ⏳ **48-Hour Wishlist Filter** | Cooling-off period for impulse purchases with an active tracker of money saved by waiting. |
| ☕ **Guilt-Free Fun Fund (فلوس الروقان)** | Safe discretionary allowance for personal enjoyment without impacting essential obligations. |
| 🔄 **Flexible Budget Cycles** | Customizable cycles (Daily, Weekly, Bi-Weekly, Monthly) tailored to your income schedule. |
| 📅 **Subscriptions & Recurrent Bills** | Due-date reminders, recurring expense deduction, and budget impact forecasting. |
| 📊 **Visual Spending Insights** | Clean charts and category breakdowns showing exactly where your money goes. |
| ⌚ **Wear OS Companion App** | Log spending and check remaining balances directly from your smartwatch. |
| 🔒 **100% Offline & Private** | No user accounts, no cloud servers, and zero data tracking. Your money stays your business. |

</div>

---

## 📱 Screenshots / لقطات من التطبيق

<p align="center">
  <img src="assets/screenshot_1.png" width="30%" alt="Main Calculator Screen">
  &nbsp;&nbsp;
  <img src="assets/screenshot_2.png" width="30%" alt="Budget Breakdown">
  &nbsp;&nbsp;
  <img src="assets/screenshot_3.png" width="30%" alt="History & Analytics">
</p>
<p align="center">
  <img src="assets/screenshot_4.png" width="30%" alt="Recurrent Bills">
  &nbsp;&nbsp;
  <img src="assets/screenshot_5.png" width="30%" alt="Micro-Goal Jar">
  &nbsp;&nbsp;
  <img src="assets/screenshot_6.png" width="30%" alt="Settings & Customization">
</p>

<p align="center">
  <b>Wear OS Smartwatch Companion</b><br>
  <img src="assets/wear_1.png" width="22%" alt="Wear OS Screen 1">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="assets/wear_2.png" width="22%" alt="Wear OS Screen 2">
</p>

---

## 🛡️ Privacy & Permissions / الخصوصية والأذونات

Wafeer is built with an uncompromising commitment to privacy:

* **100% Offline by Design:** All records, transactions, budgets, and categories are saved locally on your device via an encrypted SQLite Room database.
* **Google Play Edition:** Completely stripped of `android.permission.INTERNET` and `REQUEST_INSTALL_PACKAGES`. It has zero network capabilities.
* **GitHub FOSS Edition:** Contains `INTERNET` solely to check for updates on GitHub Releases, and `REQUEST_INSTALL_PACKAGES` to allow seamless in-app APK upgrades.

---

## 🏗️ Architecture & Tech Stack

Wafeer is designed following Modern Android Development (MAD) best practices:

* **UI:** 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Expressive guidelines.
* **Architecture:** Clean Architecture + MVI (Model-View-Intent) with Unidirectional Data Flow.
* **Concurrency:** Kotlin Coroutines & Reactive Flows (`StateFlow`, `SharedFlow`).
* **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) / Dagger.
* **Persistence:** [Room](https://developer.android.com/training/data-storage/room) & [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore).
* **Scheduling:** `AlarmManager` for precise recurring resets + `WorkManager` for background tasks.
* **Smartwatch:** Independent `:wear` module with direct companion sync via `:sync-contract`.

*For an in-depth architectural breakdown, check out [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md).*

---

## 🛠️ Building from Source / البناء من المصدر

### Requirements:
* Android Studio Ladybug (2024.2.1+) or newer
* JDK 17 or JDK 21
* Android SDK API Level 35

### Steps:
```bash
# 1. Clone the repository
git clone https://github.com/biZenX/Wafeer.git
cd Wafeer

# 2. Build the FOSS Debug APK
./gradlew :app:assembleFossDebug

# 3. Build the Wear OS Companion APK
./gradlew :wear:assembleDebug

# 4. Run tests and static analysis
./gradlew detekt :app:testFossDebugUnitTest :app:testWearDebugUnitTest
```

The resulting APK will be located at:
`app/build/outputs/apk/foss/debug/app-foss-debug.apk`

---

## 🤝 Contributing / المساهمة

We welcome contributions of all kinds! Please read our [Contributing Guidelines](CONTRIBUTING.md) and [Code of Conduct](.github/CODE_OF_CONDUCT.md) before submitting pull requests.

To report bugs or suggest enhancements, please use our structured [GitHub Issue Templates](https://github.com/biZenX/Wafeer/issues/new/choose).

---

## 📜 Credits & License / الإسناد والترخيص

* **Wafeer (وفير):** Maintained and enhanced by **Abdelrahman Karim ([@biZenX](https://github.com/biZenX))**.
* **Base Architecture:** Based on the open-source architecture of **[Minus](https://github.com/isaacsa51/Minus)** by **[Isaac Serrano (@isaacsa51)](https://github.com/isaacsa51)**.
* **Typography:** Built-in support for **Thmanyah** and **IBM Plex Sans Arabic** fonts for optimal Arabic reading comfort.

This project is open-source and licensed under the **[Apache License 2.0](LICENSE)**.
See [LICENSE.md](LICENSE.md) for full attribution details.
