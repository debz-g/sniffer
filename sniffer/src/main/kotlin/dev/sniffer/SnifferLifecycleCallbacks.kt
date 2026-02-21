package dev.sniffer

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

/**
 * Injects a ComposeView overlay into the foreground Activity's Window.decorView.
 * No SYSTEM_ALERT_WINDOW permission: we attach to the content view of the current activity.
 */
internal class SnifferLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        injectOverlay(activity)
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        Sniffer.onActivityDestroyed(activity)
    }

    private fun injectOverlay(activity: Activity) {
        val content = activity.window?.decorView as? ViewGroup ?: return
        if (content.findViewWithTag<View>(OVERLAY_TAG) != null) return

        val container = FrameLayout(activity).apply {
            id = ViewCompat.generateViewId()
            tag = OVERLAY_TAG
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setClickable(false)
            setFocusable(false)
        }
        content.addView(container)
        Sniffer.attachOverlay(container)
    }

    companion object {
        private const val OVERLAY_TAG = "sniffer_overlay"
    }
}
