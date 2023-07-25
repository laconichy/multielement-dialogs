package com.laconichy.dialog.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.lang.ref.WeakReference

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/24
 *   desc：Context extension
 * </pre>
 */

internal fun Context?.toActivity(): Activity? {
    val activity = toActivityByInner()
    return if (activity.isAlive()) activity else null
}

private fun Context?.toActivityByInner(): Activity? {
    var context: Context? = this
    val list = arrayListOf<Context>()
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        val activity: Activity? = toActivityFromDecor()
        if (activity != null) return activity
        list.add(context)
        context = context.baseContext
        if (context == null) {
            return null
        }
        if (list.contains(context)) {
            return null
        }
    }
    return null
}

private fun Context?.toActivityFromDecor(): Activity? {
    if (this == null) return null
    if (javaClass.name == "com.android.internal.policy.DecorContext") {
        try {
            val mActivityContextField = javaClass.getDeclaredField("mActivityContext")
            mActivityContextField.isAccessible = true
            return (mActivityContextField[this] as WeakReference<Activity>).get()
        } catch (ignore: Exception) {
        }
    }
    return null
}

internal fun Context?.isAlive(): Boolean = toActivity().isAlive()