# Agent HQ

A native Android app for managing GitHub Copilot Coding Agent sessions.

## Features
- Browse and track GitHub Copilot agent PRs across your repositories
- Infer agent session status (Active/Paused/Completed/Failed) from PR metadata
- Review and approve PRs directly from the app
- Steer the agent by posting instructions as issue comments
- Offline-first with Room caching
- Background sync with WorkManager

## Setup
1. Register a GitHub OAuth App at https://github.com/settings/developers
   - Set callback URL to: `agenthq://oauth`
2. Replace `GITHUB_CLIENT_ID` in `BuildConfigHelper.kt` with your app's client_id
3. Build with Android Studio or: `./gradlew assembleDebug`

## Architecture
- **UI**: Jetpack Compose + Material 3 + Navigation Compose
- **DI**: Hilt
- **Data**: Room (offline cache) + Retrofit (REST) + Apollo (GraphQL)
- **Background**: WorkManager (15-minute periodic sync)
- **Auth**: GitHub Device Flow + EncryptedSharedPreferences

## Requirements
- Android 8.0+ (API 26)
- GitHub account with Copilot access

## Building
```bash
./gradlew assembleDebug   # debug APK
./gradlew assembleRelease # release APK (requires signing config)
./gradlew test            # unit tests
```
