<p align="center">
  <img src="assets/banner.png" alt="Wafeer Banner" width="100%">
</p>

# Wafeer

<p align="center">
  <strong>Mindful, offline-first personal budget and expense tracking for Android and Wear OS.</strong><br/>
  Built with Google Material 3 Expressive. Log expenses instantly, calculate on the fly, and achieve financial calm.
</p>

<p align="center">
  <a href="https://github.com/biZenX/Wafeer/releases/latest"><img src="https://img.shields.io/badge/Release-v2.2.0-4CAF50?style=flat-square&logo=github" alt="Latest Release"></a>
  <img src="https://img.shields.io/badge/Platform-Android_%7C_Wear_OS-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Platform">
  <img src="https://img.shields.io/badge/Architecture-Material_3_Expressive-7F52FF?style=flat-square" alt="Material 3 Expressive">
  <img src="https://img.shields.io/badge/Offline-100%25_Local-blue?style=flat-square" alt="Offline">
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square" alt="License">
</p>

<p align="center">
  <a href="https://github.com/biZenX/Wafeer/releases/latest">
    <img src="assets/badge_github.png" height="58" alt="Download on GitHub">
  </a>
</p>

<div align="center">
  <h4>
    <a href="#features">Features</a>
    <span> &bull; </span>
    <a href="#screenshots">Screenshots</a>
    <span> &bull; </span>
    <a href="#installation">Installation</a>
    <span> &bull; </span>
    <a href="PROJECT_OVERVIEW.md">Architecture</a>
    <span> &bull; </span>
    <a href="CONTRIBUTING.md">Contributing</a>
    <span> &bull; </span>
    <a href="https://github.com/biZenX/Wafeer/issues">Issues</a>
  </h4>
</div>

---

## Overview

**Wafeer** is an easy-to-use, privacy-first personal budgeting and daily expense tracking app. Instead of complicated spreadsheets, monthly subscriptions, or slow banking tools, Wafeer focuses on answering the single most important financial question every day:

> **How much can I comfortably spend today without running short at the end of the cycle?**

Everything runs **100% locally on your device** with zero accounts, no cloud dependencies, and zero advertisements.

---

## Features

- **Fluid Calculator Numpad**  
  Log expenses in seconds with a familiar calculator numpad. Perform math calculations directly on the fly before saving — no complex multi-step forms.

- **Dynamic Budget Periods & Surplus Rollover**  
  Flexible budget cycles (Daily, Weekly, Bi-weekly, or Monthly). If you spend less today, your daily limit dynamically expands for tomorrow without manual adjustments.

- **Smart Savings Suite**  
  Built-in behavioral tools to curb impulse spending:
  - **48-Hour Wishlist Delay**: Cool-off timer before making discretionary purchases.
  - **Affordability Simulator**: Preview how any purchase impacts your daily spending capacity.
  - **Micro-Goal Jars**: Visual savings buckets for specific targets.
  - **Fun Fund Allocation**: Dedicated guilt-free entertainment budget.

- **Wear OS Smartwatch Companion**  
  Standalone module for Wear OS watches with Wear Compose. View remaining allowance and log transactions directly from your wrist via custom Tiles and complications.

- **Recurring Payments & Subscriptions**  
  Track recurring bills, monthly subscriptions, and installment reminders with advance due-date notifications.

- **Home Screen Widgets**  
  Check your remaining daily limit and record quick expenses directly from your home screen via Jetpack Glance.

- **100% Offline & Private**  
  All data is stored locally in an encrypted SQLite database. Zero network requests, zero analytics, and zero external trackers.

---

## Screenshots

<table align="center">
  <tr>
    <td align="center"><img src="assets/screenshot_1.png" alt="Calculator Numpad" width="220" style="border-radius:24px;"/><br/><sub><b>Calculator Numpad</b></sub></td>
    <td align="center"><img src="assets/screenshot_2.png" alt="Budget Overview" width="220" style="border-radius:24px;"/><br/><sub><b>Clover Budget Card</b></sub></td>
    <td align="center"><img src="assets/screenshot_3.png" alt="Analytics and Trends" width="220" style="border-radius:24px;"/><br/><sub><b>Spending Trends</b></sub></td>
  </tr>
  <tr>
    <td align="center"><img src="assets/screenshot_4.png" alt="Subscriptions" width="220" style="border-radius:24px;"/><br/><sub><b>Subscriptions & Bills</b></sub></td>
    <td align="center"><img src="assets/screenshot_5.png" alt="Theme Settings" width="220" style="border-radius:24px;"/><br/><sub><b>Material 3 Expressive</b></sub></td>
    <td align="center"><img src="assets/screenshot_6.png" alt="OLED Dark Mode" width="220" style="border-radius:24px;"/><br/><sub><b>OLED Dark Mode</b></sub></td>
  </tr>
</table>

---

## Installation

Download the official signed release package directly from GitHub:

1. Go to the [**Latest Release**](https://github.com/biZenX/Wafeer/releases/latest).
2. Download `Wafeer-v2.2.0.apk`.
3. Open the APK on your Android device (Android 8.0+ / API 26+) and confirm installation.

> [!NOTE]
> **Upgrading from older builds (v1.x or earlier):**  
> Release v2.2.0 uses a hardened cryptographic release signing key. If Android displays `App not installed`, export your data via **Settings > Data > Export Backup**, uninstall the older build, install v2.2.0, and import your backup with one click.

---

## Contributing & Development

Interested in contributing or building the app from source?  
Please check out our developer guides:

- [**Contributing Guidelines**](CONTRIBUTING.md) — Prerequisites, build commands, and testing instructions.
- [**Project Architecture Overview**](PROJECT_OVERVIEW.md) — Deep dive into MVI architecture, Room, and the Wear OS sync layer.
- [**Code of Conduct**](.github/CODE_OF_CONDUCT.md) — Community standards.
- [**Security Policy**](.github/SECURITY.md) — Vulnerability reporting guidelines.

---

## License

Wafeer is free and open-source software maintained by **Arkim ([@biZenX](https://github.com/biZenX))**.  
Released under the terms of the **[Apache License 2.0](LICENSE)**.
