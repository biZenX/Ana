## Description
<!-- Briefly explain what changes were made and why. -->

#### Related Issue
<!-- e.g. Fixes #123 -->

---

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] UI / Design update
- [ ] Performance improvement
- [ ] Localization / Translation
- [ ] Build / CI maintenance

---

## Checklist
- [ ] Code follows existing project style and architecture (MVI / Clean Architecture).
- [ ] Respects offline-first privacy (no telemetry or unapproved network requests).
- [ ] Tested RTL / layout orientation if modifying UI.
- [ ] Static analysis passes: `./gradlew detekt`
- [ ] Unit tests pass: `./gradlew :app:testFossDebugUnitTest :app:testWearDebugUnitTest`
- [ ] Verified full compilation: `./gradlew :app:compileFossDebugKotlin :wear:compileDebugKotlin :sync-contract:compileKotlin`

---

## Screenshots (if applicable)
| Before | After |
| :---: | :---: |
| | |
