package com.laconichy.dialog.dialog

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.laconichy.dialog.extension.isAlive
import com.laconichy.dialog.extension.toActivity

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/24
 *   desc：Use lifecycle to bind
 * </pre>
 */
open class LDBaseDialog(
    context: Context,
    themeResId: Int
) : Dialog(context, themeResId) {

    init {
        val activity = context.toActivity()
        if (activity is LifecycleOwner) {
            activity.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        dismiss()
                    }
                }
            })
        }
    }

    override fun show() {
        if (context.isAlive()) {
            super.show()
        }
    }

    override fun dismiss() {
        if (context.isAlive()) {
            super.dismiss()
        }
    }

}