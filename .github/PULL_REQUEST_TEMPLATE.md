## Description / الوصف
<!-- Explain the goal of this PR and what problem it solves. -->
<!-- اشرح الهدف من هذا الـ PR والمشكلة التي يعالجها. -->

#### Related Issue(s) / الأعداد المرتبطة
<!-- Fixes #123, Closes #456 -->

---

## Type of Change / نوع التغيير
- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 🎨 UI / Design update (visual layout, typography, animations)
- [ ] ⚡ Performance improvement
- [ ] 🌐 Localization / Translation update
- [ ] 🛠️ Refactoring or build / CI maintenance

---

## Architecture & Code Quality Checklist / معايير الكود والجودة
- [ ] Follows Clean Architecture & MVI pattern (Logic in UseCases/ViewModels, UI in Composables).
- [ ] Preserves **100% Offline Privacy** (Zero internet calls or telemetry in `main` / `wear` modules).
- [ ] Tested RTL / Arabic layout alignment and typography.
- [ ] User-facing strings localized in `res/values/strings.xml` and `res/values-en/strings.xml`.
- [ ] Ran static analysis: `./gradlew detekt` (0 errors / 0 warnings).
- [ ] Ran unit tests: `./gradlew :app:testFossDebugUnitTest :app:testWearDebugUnitTest`.
- [ ] Verified full compilation: `./gradlew :app:compileFossDebugKotlin :wear:compileDebugKotlin :sync-contract:compileKotlin`.

---

## Screenshots / Video (For UI changes) / لقطات شاشة أو فيديو
| Before (قبل) | After (بعد) |
| :---: | :---: |
| *(Image)* | *(Image)* |

---

## Notes for Reviewers / ملاحظات للمراجعين
<!-- Any context, edge cases, or deployment considerations -->
