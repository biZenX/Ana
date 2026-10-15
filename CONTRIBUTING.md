# Contributing to Wafeer

Thanks for taking the time to contribute! Whether you're reporting bugs, improving documentation, submitting translations, or writing code, your help is appreciated.

---

## Getting Started

1. **Prerequisites:**
   - Android Studio Ladybug (2024.2.1+) or newer
   - JDK 17 or JDK 21
   - Android SDK with API Level 35

2. **Clone & Setup:**
   ```bash
   git clone https://github.com/biZenX/Wafeer.git
   cd Wafeer
   ```

3. **Project Modules:**
   - `:app` — Main mobile application (Jetpack Compose, Room, Hilt, MVI).
     - Flavor `foss`: Direct APK distribution with self-updating capabilities.
     - Flavor `wear`: Google Play Store flavor without `INTERNET` permission.
   - `:wear` — Wear OS standalone companion app.
   - `:sync-contract` — Shared data models and communication contracts between Phone and Watch.

---

## Pre-PR Verification

Before submitting a pull request, run the local verification suite:

```bash
# 1. Compile all Kotlin sources across modules
./gradlew :app:compileFossDebugKotlin :wear:compileDebugKotlin :sync-contract:compileKotlin

# 2. Run Detekt static analysis
./gradlew detekt

# 3. Run unit tests
./gradlew :app:testFossDebugUnitTest :app:testWearDebugUnitTest
```

---

## Guidelines

1. **Commit Messages:** Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `ci:`).
2. **Branching:** Work on a branch off `main` (e.g., `fix/numpad-decimal` or `feat/goal-export`).
3. **Privacy First:** Never introduce external tracking, third-party network SDKs, or cloud dependencies into the offline architecture.
4. **UI Changes:** Attach Before / After screenshots to your pull request.
5. **Localization:** Keep user-facing strings in `res/values/strings.xml` and `res/values-en/strings.xml`.

---

## Community

- Please review our [Code of Conduct](.github/CODE_OF_CONDUCT.md).
- To report security issues, see our [Security Policy](.github/SECURITY.md).
