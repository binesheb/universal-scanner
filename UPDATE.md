# Update and rollback

## Manual update

For a checked-out development or deployment copy, update only with a clean working tree:

```bash
git status --short
git fetch origin
git pull --ff-only origin main
```

Build the release APK before distributing it:

```bash
gradle --no-daemon :app:assembleRelease
```

The GitHub Actions workflow also builds the release APK on every push to `main` and uploads the APK as a workflow artifact.

## Rollback

Record the current revision before updating:

```bash
git rev-parse HEAD
```

If the updated revision fails validation, return to the last known-good commit explicitly:

```bash
git reset --hard <known-good-commit>
```

Do not use this reset in a working tree containing uncommitted changes.

## Automatic updates

The app does not currently implement runtime self-updating. APK distribution should remain a controlled release/deployment step until a signed release channel and an in-app update design are established. The GitHub Actions workflow provides automated builds, not automatic installation on user devices.
