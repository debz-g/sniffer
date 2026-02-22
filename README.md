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
       implementation("dev.sniffer:sniffer:1.0.0")
   }
   ```

   Use the same version as in this repo’s `gradle.properties` (`VERSION_NAME`).

### Option B: Maven Central (recommended)

If Sniffer is published to [Maven Central](https://central.sonatype.com/), add nothing extra — use `mavenCentral()` and the dependency:

1. **App’s `settings.gradle.kts`** — ensure `mavenCentral()` is in `repositories` (default in most projects):

   ```kotlin
   dependencyResolutionManagement {
       repositories {
           google()
           mavenCentral()
       }
   }
   ```

2. **App’s module `build.gradle.kts`** (e.g. `app/build.gradle.kts`):

   ```kotlin
   dependencies {
       implementation("dev.sniffer:sniffer:1.0.0")
   }
   ```

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

### Using with Ktor

Sniffer works with [Ktor](https://ktor.io/) when you use the **OkHttp** engine and add the interceptor in the engine config:

1. **Dependency** — use the OkHttp engine (Android’s default engine uses OkHttp):

   ```kotlin
   implementation("io.github.debz-g:sniffer:1.0.0")
   implementation("io.ktor:ktor-client-okhttp:2.x.x")  // or ktor-client-android on Android
   ```

2. **Init** — same as above: `Sniffer.init(this)` in `Application.onCreate()`.

3. **Add interceptor** to your Ktor client:

   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.okhttp.*
   import dev.sniffer.Sniffer

   val client = HttpClient(OkHttp) {
       engine {
           addInterceptor(Sniffer.interceptor())
       }
   }
   ```

   Requests made with this client will appear in the Sniffer overlay.

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

- **Maven Local:** `./gradlew publishToMavenLocal` — artifact under `dev.sniffer:sniffer:VERSION_NAME` (see `gradle.properties`).
- **Maven Central:** Uses [gradle-nexus/publish-plugin](https://github.com/gradle-nexus/publish-plugin) (Nexus Staging API).  
  1. At [central.sonatype.com](https://central.sonatype.com/) add and **verify** a namespace. **GROUP in gradle.properties must match this exactly** (e.g. `dev.sniffer` or `io.github.debz-g`).  
  2. [Generate a user token](https://central.sonatype.com/usertoken); set `SONATYPE_USERNAME` and `SONATYPE_PASSWORD` in `gradle.properties`.  
  3. Configure GPG signing: `signing.keyId`, `signing.password`, `signing.secretKeyRingFile` in `gradle.properties`.  
  4. Run:
     ```bash
     ./gradlew clean publishToMavenCentral
     ```
  5. Open [central.sonatype.com/publishing](https://central.sonatype.com/publishing), find the deployment, and click **Release**.  
  **400 Bad Request:** Set `GROUP` in `gradle.properties` to your **verified** namespace (e.g. `io.github.debz-g` if you verified that). See [View Namespaces](https://central.sonatype.com/namespaces).
