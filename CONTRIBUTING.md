# Contributing to Wafeer (دليل المساهمة في وفير)

Thanks for your interest in contributing to **Wafeer (وفير)**! We welcome contributions from bug fixes and localization to new features and UI improvements.

---

## Getting Started / البدء

1. **Prerequisites:**
   - Android Studio Ladybug or newer.
   - JDK 17 (or JDK 21).
   - Android SDK with API Level 35 and Build-Tools 35.0.0+.

2. **Clone & Setup:**
   ```bash
   git clone https://github.com/biZenX/Wafeer.git
   cd Wafeer
   ```

3. **Modules Structure:**
   - `:app` — Main mobile Android application (Jetpack Compose, Room, Hilt, MVI).
     - Flavor `foss`: Direct APK distribution with self-updating capabilities.
     - Flavor `wear`: Google Play Store flavor without `INTERNET` permission.
   - `:wear` — Wear OS standalone companion app.
   - `:sync-contract` — Shared data contracts and serialization models between Phone and Watch.

---

## Verification & Pre-PR Checks / الفحص قبل إرسال المساهمة

Before opening a pull request, run the CI validation suite locally:

```bash
# 1. Compile all Kotlin code across mobile, wear, and sync modules
./gradlew :app:compileFossDebugKotlin :wear:compileDebugKotlin :sync-contract:compileKotlin

# 2. Run static analysis with Detekt
./gradlew detekt

# 3. Run unit tests
./gradlew :app:testFossDebugUnitTest :app:testWearDebugUnitTest
```

---

## Pull Request Guidelines / إرشادات المساهمة

1. **Commit Messages:** Follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `ci:`).
2. **Branch Strategy:** Create a descriptive branch from `main` (e.g. `feature/smart-saving` or `fix/numpad-decimal`).
3. **Screenshots:** For UI changes, attach Before / After screenshots or screen recordings.
4. **Privacy First:** Never introduce external tracking, third-party network SDKs, or cloud dependencies into the offline architecture.
5. **Localization:** Keep user-facing strings in `res/values/strings.xml` (Arabic default) and `res/values-en/strings.xml` (English).

---

## Code of Conduct & Security

- Please review our [Code of Conduct](.github/CODE_OF_CONDUCT.md).
- To report security vulnerabilities, see our [Security Policy](.github/SECURITY.md).
