<p align="center">
  <img src="assets/banner.png" alt="Wafeer Banner" width="100%">
</p>

# Wafeer - Simple, private, offline budget manager

<p align="center">
  <strong>Wafeer</strong> is an easy to use, offline-first personal budget and expense tracking app for Android and Wear OS.<br/>
  Register expenses instantly using a calculator numpad, organize flexible budget cycles, track recurring bills, and save towards your goals.
</p>

<div align="center">
  <h4>
    <a href="#features">Features</a>
    <span> &bull; </span>
    <a href="PROJECT_OVERVIEW.md">Project Overview</a>
    <span> &bull; </span>
    <a href="https://github.com/biZenX/Wafeer/issues/new?template=1_bug_report.yml">Report Bug</a>
    <span> &bull; </span>
    <a href="https://github.com/biZenX/Wafeer/issues/new?template=2_feature_request.yml">Request a Feature</a>
  </h4>
</div>

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/biZenX/Wafeer/releases/latest">
        <img src="assets/badge_github.png" height="70" alt="Get it on GitHub">
      </a>
    </td>
    <td align="center">
      <a href="https://play.google.com/store/apps/details?id=com.wafeer.app">
        <img src="assets/badge_playstore.png" height="70" alt="Get it on Google Play">
      </a>
    </td>
  </tr>
</table>

<p align="center">
  <a href="https://github.com/biZenX/Wafeer/releases/latest"><img src="https://img.shields.io/badge/Release-v2.0.0-4CAF50?style=flat-square&logo=github" alt="Latest Release"></a>
  <img src="https://img.shields.io/badge/Platform-Android_%7C_Wear_OS-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Platform">
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square" alt="License">
</p>

<table align="center">
  <tr>
    <td><img src="assets/screenshot_1.png" alt="Calculator Numpad" width="200" style="border-radius:26px;"/></td>
    <td><img src="assets/screenshot_2.png" alt="Budget Overview" width="200" style="border-radius:26px;"/></td>
    <td><img src="assets/screenshot_3.png" alt="Analytics and History" width="200" style="border-radius:26px;"/></td>
  </tr>
  <tr>
    <td><img src="assets/screenshot_4.png" alt="Subscriptions and Settings" width="200" style="border-radius:26px;"/></td>
    <td><img src="assets/screenshot_5.png" alt="Savings Goals" width="200" style="border-radius:26px;"/></td>
    <td><img src="assets/screenshot_6.png" alt="Customization" width="200" style="border-radius:26px;"/></td>
  </tr>
</table>

---

## Features

| Feature | Description |
|:---|:---|
| **Save, Edit & Calculate on the Fly** <br/> Log expenses in seconds using a familiar calculator-style numpad. No complex multi-step forms — tap the amount and save. Swipe up to reveal math operators and make calculations directly before logging. | <img src="assets/screenshot_1.png" alt="Numpad" width="480" style="border-radius:22px;"/> |
| **Smart Budget Periods** <br/> Configure your budget cycles to match your real cashflow. Choose between Daily, Weekly, Bi-weekly, or Monthly periods with automatic calculation of daily spending limits and rollover surplus. | <img src="assets/split_budget.png" alt="Budget Periods" width="480" style="border-radius:22px;"/> |
| **Recurring Expenses & Subscriptions** <br/> Track recurring subscriptions, bills, and credit card payments. Set advance reminders and let the app automatically deduct scheduled payments on due dates. | <img src="assets/recurrent_payment.png" alt="Recurring Payments" width="480" style="border-radius:22px;"/> |
| **Smart Savings Tools** <br/> Built-in behavioral tools to curb impulse spending: a 48-Hour Wishlist Filter with total money saved counter, an Affordability Simulator to preview purchase impacts, a Micro-Goal Jar, and a Guilt-Free Fun Fund. | <img src="assets/screenshot_5.png" alt="Savings Tools" width="480" style="border-radius:22px;"/> |
| **Spending Analytics & Insights** <br/> Group expenses by date, category, and payment method. Visual charts and breakdown metrics help you identify spending habits and spot where you can save. | <img src="assets/screenshot_3.png" alt="Analytics" width="480" style="border-radius:22px;"/> |
| **100% Offline & Private** <br/> All financial data stays strictly on your device in a local encrypted database. No user accounts, no cloud sync, and zero third-party analytics. The Google Play edition contains zero internet permissions. | <img src="assets/screenshot_4.png" alt="Settings" width="480" style="border-radius:22px;"/> |
| **Wear OS Companion App** <br/> Log expenses and check remaining balance directly from your smartwatch. Full numpad interface optimized for circular and square displays, with local sync to the phone app. | <img src="assets/wear_1.png" alt="Wear OS" width="480" style="border-radius:22px;"/> |

---

## Building from Source

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 or JDK 21
- Android SDK API Level 35

### Commands
```bash
# Clone the repository
git clone https://github.com/biZenX/Wafeer.git
cd Wafeer

# Build FOSS Debug APK
./gradlew :app:assembleFossDebug

# Build Wear OS APK
./gradlew :wear:assembleDebug

# Run static analysis and unit tests
./gradlew detekt :app:testFossDebugUnitTest :app:testWearDebugUnitTest
```

The compiled APK will be at:
`app/build/outputs/apk/foss/debug/app-foss-debug.apk`

---

## Contributing

Contributions are welcome! Please check our [Contributing Guidelines](CONTRIBUTING.md) and [Code of Conduct](.github/CODE_OF_CONDUCT.md).

To report bugs or request features, use our [Issue Templates](https://github.com/biZenX/Wafeer/issues/new/choose).

---

## Credits & License

- **Wafeer**: Maintained by **Abdelrahman Karim ([@biZenX](https://github.com/biZenX))**.
- **Original Architecture**: Based on **[Minus](https://github.com/isaacsa51/Minus)** by **[Isaac Serrano (@isaacsa51)](https://github.com/isaacsa51)**.
- **Typography**: Native support for **Thmanyah** and **IBM Plex Sans Arabic** fonts.

Licensed under the **[Apache License 2.0](LICENSE)**. See [LICENSE.md](LICENSE.md) for full terms.

<div align="center">
  <p>Made with &hearts; by <a href="https://github.com/biZenX">Abdelrahman Karim</a></p>
</div>
