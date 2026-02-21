# Sniffer

**Just sniff it.** Open-source Android debugging library: on-screen Compose overlay to inspect HTTP traffic, view custom logs, and mock API responses without leaving the host app.

- **Repo:** [github.com/debz-g/sniffer](https://github.com/debz-g/sniffer)
- **License:** GPL-3.0

## Tech stack

- **Kotlin** + **Jetpack Compose** (no XML)
- **OkHttp3** interceptors for network capture
- **Kotlin Coroutines** + **StateFlow** / **SharedFlow**
- **Room** for persisting logs and network calls across sessions

## Install

### Option A: Maven Local (for local use)

1. **Publish to Maven Local** from this repo:

   ```bash
   ./gradlew publishToMavenLocal
   ```

   (Or `./gradlew :sniffer:publishReleasePublicationToMavenLocal`.)

2. In your **app’s** `settings.gradle.kts` (or `settings.gradle`), ensure Maven Local is a repository:

   ```kotlin
   dependencyResolutionManagement {
       repositories {
           mavenLocal()
           google()
           mavenCentral()
       }
   }
   ```

3. In your app’s `build.gradle.kts`:

   ```kotlin
   dependencies {
       implementation("dev.sniffer:sniffer:0.1.0")
   }
   ```

   Use the same version as in this repo’s `gradle.properties` (`VERSION_NAME`).

### Option B: Maven Central (planned)

Once published to Maven Central, you’ll only need:

```kotlin
implementation("dev.sniffer:sniffer:0.1.0")
```

(with `mavenCentral()` in your repositories).

## Setup (in your app)

1. In your `Application.onCreate()`:

   ```kotlin
   Sniffer.init(this)
   ```

2. Add the interceptor to your OkHttp client:

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(Sniffer.interceptor())
    .build()
```

3. (Optional) Log custom messages:

```kotlin
Sniffer.log("User tapped submit", tag = "Auth")
```

## Overlay

- **No `SYSTEM_ALERT_WINDOW`** – the overlay is injected into the foreground Activity’s `Window.decorView` via `ActivityLifecycleCallbacks`.
- A **draggable floating bubble** appears; **tap** to open the Inspector, **drag** to move.
- The Inspector has **Network** and **Logs** tabs and shows recent requests and logs from the repository (StateFlow).

## Response mocking

Enable mocks via the repository (e.g. from a future “Mocks” tab or API):

- Add a mock with a URL pattern, response body JSON, and status code.
- When a request matches an enabled mock, `SnifferInterceptor` does **not** call `chain.proceed()`; it returns a fake `Response` built with `Response.Builder()`.

Mocks are stored in Room and observed via `repository.getEnabledMocks()` / `observeEnabledMocks()`.

## Module structure

- **Data**: `entity` (Room), `dao`, `db`, `model`, `repository`
- **Network**: `SnifferInterceptor`
- **UI**: Compose overlay (bubble + Inspector with Network / Logs tabs)
- **Init**: `Sniffer` singleton + `SnifferLifecycleCallbacks` for Window injection

## Publishing (maintainers)

- **Maven Local:** `./gradlew publishToMavenLocal` — artifact will be under `dev.sniffer:sniffer:VERSION_NAME` (see `gradle.properties`).
- **Maven Central (later):** Uncomment the `maven { ... }` block in `sniffer/build.gradle.kts` under `repositories`, add Sonatype credentials to `~/.gradle/gradle.properties`, then use the [Central publishing workflow](https://central.sonatype.com/publish/publish-gradle/).
