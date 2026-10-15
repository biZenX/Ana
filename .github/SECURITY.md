# Security Policy

## Supported Versions

Security updates are provided for the latest release series:

| Version | Supported          |
| ------- | ------------------ |
| 1.3.x   | :white_check_mark: |
| < 1.3.0 | :x:                |

---

## Reporting a Vulnerability

If you discover a security vulnerability or sensitive data issue, please **do not open a public issue**.

Report it responsibly via:
- **GitHub Private Advisory:** [Report a security advisory](https://github.com/biZenX/Wafeer/security/advisories/new)
- **Maintainer Contact:** Reach out directly through the maintainer GitHub profile ([@biZenX](https://github.com/biZenX)).

Please include:
- A description of the issue and its potential impact.
- Steps or proof-of-concept to reproduce the problem.
- Any suggested fix or workaround.

Reports are acknowledged within 48 hours.

---

## Offline Privacy Guarantee

Wafeer is designed as an offline-first budget manager:
- All transaction records and categories are stored locally in SQLite.
- The Google Play build includes no network permissions (`android.permission.INTERNET` is omitted).
- The FOSS build only contacts the GitHub API when checking for newer APK releases.
