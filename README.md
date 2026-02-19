# Agent HQ Android — Copilot Coding Agent Session Manager

An Android app for monitoring and managing GitHub Copilot coding-agent sessions — review PRs, steer in-progress work, and stay on top of your AI-assisted development workflow.

## Features

- **Dashboard** — real-time overview of all active Copilot agent sessions
- **PR Review** — review and approve Copilot-generated pull requests on the go
- **Session Steering** — send follow-up instructions or stop a running session
- **Notifications** — push alerts for session completion, failures, and review requests
- **Offline Cache** — Room-backed local storage so you can browse sessions without connectivity

## Architecture

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| Local DB | Room |
| Background | WorkManager |
| Auth | Encrypted SharedPreferences (AndroidX Security) |

## Build

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

Requires JDK 17 and Android SDK 35.

## Project Structure

```
app/src/main/java/com/agenthq/app/
├── data/
│   ├── api/          # Retrofit services & API models
│   ├── auth/         # OAuth / token management
│   ├── db/           # Room database, DAOs, entities
│   └── session/      # Copilot session inference engine
├── di/               # Hilt modules
├── ui/               # Compose screens & view models
└── worker/           # WorkManager background jobs
```

## Screenshots

> _Screenshots coming soon._

## License

See [LICENSE](LICENSE) for details.

