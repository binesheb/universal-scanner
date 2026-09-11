# Universal Scanner

A lightweight Android barcode and QR scanner intended as a reusable foundation for logistics, package tracking, retail, and operational workflows.

## Current build
- Version: `1.0.1` (versionCode `2`)
- Live CameraX preview
- ML Kit barcode and QR recognition
- Duplicate suppression
- Live scan count and last scan display
- GitHub Actions release APK build

## Build output
The workflow publishes `universal-scanner-release-apk`, containing `app-release.apk` and its SHA-256 checksum.

The current workflow output is an **unsigned APK artifact for controlled testing/deployment**, not a production GitHub Release. Do not treat it as an automatically distributed app update.

## Safe update and rollback
See [UPDATE.md](UPDATE.md) for the manual update, rollback, artifact verification, and release-provenance procedure.

## Next milestones
- Multi-frame scan validation
- Offline persistent queue
- Configurable API endpoint
- Scanner identity and settings
- Scan history export

## Build trigger
The Android release workflow runs automatically on every push to `main` and can also be started manually with GitHub Actions.
