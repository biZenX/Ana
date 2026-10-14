# Security Policy / سياسة الأمان

## Supported Versions / الإصدارات المدعومة

We actively maintain and provide security updates for the latest production release of Wafeer:

| Version | Supported          |
| ------- | ------------------ |
| 1.3.x   | :white_check_mark: |
| < 1.3.0 | :x:                |

---

## Reporting a Vulnerability / الإبلاغ عن ثغرة أمنية

If you discover a security vulnerability or sensitive data leak in Wafeer, please **do not open a public issue**. 

Instead, report it responsibly via:
1. **GitHub Private Security Advisory:** [Report a security advisory](https://github.com/biZenX/Wafeer/security/advisories/new)
2. **Direct Contact:** Open a confidential discussion or email the maintainer via GitHub profile ([@biZenX](https://github.com/biZenX)).

Please provide:
- A clear description of the vulnerability and its potential impact.
- Step-by-step instructions or proof-of-concept (PoC) to reproduce the issue.
- Your recommended solution or mitigation (if available).

We will acknowledge receipt within 48 hours and work with you to remediate and publish a patch.

---

## Offline-First Privacy Guarantee / ضمان الخصوصية الكاملة

Wafeer is built from the ground up as an **offline-first** budget manager:
- Financial records, transactions, categories, and settings never leave your device.
- The Google Play build requires zero network permissions (`android.permission.INTERNET` is omitted).
- The FOSS release only accesses the official GitHub API to query whether an update is available when initiated by the user.
