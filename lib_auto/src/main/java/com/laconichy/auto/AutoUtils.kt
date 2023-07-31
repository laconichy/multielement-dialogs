package com.laconichy.auto

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.WindowManager
import androidx.annotation.RestrictTo

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/26
 *   desc：
 * </pre>
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object AutoUtils {

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}

internal fun Context?.getScreenWidthAndHeight(): Pair<Int, Int> {
    this?.let {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return Point()
            .apply { windowManager.defaultDisplay.getRealSize(this) }
            .let { Pair(it.x, it.y) }
    }
    return Pair(-1, -1)
}