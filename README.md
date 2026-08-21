# Universal Scanner

Native Android barcode and QR scanner built for fast, continuous scanning. The app uses Jetpack Compose for the UI, CameraX for the camera pipeline, and ML Kit for on-device barcode recognition.

## Current state

The scanner is at the `1.1.1` implementation baseline. Recent work focuses on stabilized detection, duplicate suppression, responsive system insets, a scan targeting guide, and keeping the screen awake during active scanning.

## Build

The `main` branch builds the Android APK through GitHub Actions. Pull requests are also validated with the APK build workflow.

### Local build

```bash
./gradlew assembleDebug
```

The debug APK is produced under `app/build/outputs/apk/debug/`.

## Updates

### Automatic update path

Android does not allow an app to silently replace itself. Versioned APKs should therefore be distributed through GitHub Releases or another trusted Android distribution channel, with the user explicitly confirming installation of the new APK. The build/release process must keep `versionCode` monotonic and use Semantic Versioning for `versionName`.

### Manual source update

```bash
git checkout main
git fetch --prune origin main
git pull --ff-only origin main
./gradlew assembleDebug
```

To roll back, check out a known-good tag or commit and rebuild the APK:

```bash
git checkout <tag-or-commit>
./gradlew assembleDebug
```

## Versioning and releases

- Patch releases fix bugs or improve reliability without changing the product contract.
- Minor releases add backward-compatible scanner features.
- Major releases are reserved for incompatible user-facing or platform changes.
- Every meaningful release should have a matching Git tag, release notes, and reproducible APK artifact.
- See [CHANGELOG.md](CHANGELOG.md) for release history.
