package dev.sniffer

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dev.sniffer.data.db.SnifferDatabase
import dev.sniffer.data.repository.SnifferRepository
import dev.sniffer.ui.SnifferOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Public API for the Sniffer debugging library.
 *
 * Usage:
 * 1. In Application.onCreate(): Sniffer.init(this)
 * 2. Add SnifferInterceptor to your OkHttpClient (see [interceptor]).
 * 3. Call Sniffer.log("message") for custom logs.
 */
object Sniffer {

    private var repository: SnifferRepository? = null
    private var scope: CoroutineScope? = null
    private var overlayView: ComposeView? = null
    private var overlayContainer: FrameLayout? = null
    private var overlayActivity: android.app.Activity? = null

    @Volatile
    private var initialized = false

    /**
     * Initialize Sniffer. Call from [Application.onCreate].
     * Injects overlay via [android.app.Application.ActivityLifecycleCallbacks] into the
     * foreground Activity's window (no SYSTEM_ALERT_WINDOW permission).
     * No-op in release builds: only runs when the app is debuggable
     * ([ApplicationInfo.FLAG_DEBUGGABLE]), e.g. debug build or run from IDE.
     */
    fun init(application: Application) {
        if ((application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) == 0) return
        if (initialized) return
        val db = SnifferDatabase.get(application)
        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        repository = SnifferRepository(
            networkCallDao = db.networkCallDao(),
            logDao = db.logDao(),
            mockDao = db.mockDao(),
            scope = appScope
        )
        scope = appScope
        application.registerActivityLifecycleCallbacks(SnifferLifecycleCallbacks())
        initialized = true
    }

    /**
     * Returns the OkHttp Interceptor that logs traffic and applies mocks.
     * Add to your client: OkHttpClient.Builder().addInterceptor(Sniffer.interceptor()).build()
     */
    fun interceptor(): dev.sniffer.network.SnifferInterceptor =
        dev.sniffer.network.SnifferInterceptor(repositoryProvider = { repository })

    /**
     * Add a mock response for requests whose URL contains [urlPattern].
     * When matched, the interceptor returns [statusCode] with [responseBody] (e.g. JSON) without calling the network.
     */
    fun addMock(urlPattern: String, responseBody: String, statusCode: Int = 200) {
        scope?.let { sc ->
            kotlinx.coroutines.CoroutineScope(sc.coroutineContext + kotlinx.coroutines.Dispatchers.IO).launch {
                repository?.addMock(urlPattern, responseBody, statusCode)
            }
        }
    }

    /**
     * Log a message to the Sniffer Inspector (Logs tab).
     */
    fun log(message: String, tag: String? = null, level: String = "INFO") {
        val repo = repository ?: return
        scope?.let { sc ->
            kotlinx.coroutines.CoroutineScope(sc.coroutineContext + kotlinx.coroutines.Dispatchers.IO).launch {
                repo.insertLog(message, tag, level)
            }
        }
    }

    fun getRepository(): SnifferRepository? = repository
    fun networkCallsState(): StateFlow<List<dev.sniffer.data.model.NetworkCall>>? = repository?.networkCalls
    fun logsState(): StateFlow<List<dev.sniffer.data.model.LogEntry>>? = repository?.logs
    fun newNetworkCallFlow(): SharedFlow<dev.sniffer.data.model.NetworkCall>? = repository?.newNetworkCall
    fun newLogFlow(): SharedFlow<dev.sniffer.data.model.LogEntry>? = repository?.newLog

    internal fun attachOverlay(container: FrameLayout) {
        if (overlayContainer == container) return
        detachOverlay()
        overlayContainer = container
        overlayActivity = container.context as? Activity
        val composeView = ComposeView(container.context).apply {
            setContent {
                SnifferOverlay(
                    repository = repository!!,
                    onDismiss = { /* optional: collapse only */ }
                )
            }
        }
        container.addView(
            composeView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        (container.context as? androidx.activity.ComponentActivity)?.let { activity ->
            composeView.setViewTreeLifecycleOwner(activity)
            composeView.setViewTreeSavedStateRegistryOwner(activity)
        }
        overlayView = composeView
    }

    internal fun detachOverlay() {
        overlayView?.parent?.let { (it as? ViewGroup)?.removeView(overlayView) }
        overlayView = null
        overlayContainer = null
        overlayActivity = null
    }

    internal fun onActivityDestroyed(activity: Activity) {
        if (overlayActivity === activity) detachOverlay()
    }
}
