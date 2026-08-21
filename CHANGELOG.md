# Changelog

All notable changes to Universal Scanner are documented here.

The project follows [Semantic Versioning](https://semver.org/). Android `versionCode` is monotonically increased for every installable release, while `versionName` uses the corresponding semantic version.

## [1.1.1] - 2026-08-21

### Fixed
- Stabilized barcode detection and duplicate suppression.
- Corrected responsive system inset handling.
- Added a scan targeting guide and keep-screen-awake behavior.

### Build
- Added GitHub Actions APK verification for pull requests and production builds.

## [1.0.0]

### Added
- Initial native Android barcode and QR scanning implementation using CameraX, Jetpack Compose, and ML Kit.
