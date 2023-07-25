package com.laconichy.dialog.extension

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/24
 *   desc：
 * </pre>
 */
@RestrictTo(LIBRARY_GROUP)
object LDUtils {

    @RestrictTo(LIBRARY_GROUP)
    fun getDefaultFormat(): SimpleDateFormat
        = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @RestrictTo(LIBRARY_GROUP)
    fun getSimpleDateFormat(pattern: String): SimpleDateFormat
        = SimpleDateFormat(pattern, Locale.getDefault())

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

internal fun View.onClick(block: (view: View) -> Unit) {
    setOnClickListener(block)
}

internal fun Int.toWeekStr(): String {
    var weekStr = ""
    when (this) {
        1 -> weekStr = "周日"
        2 -> weekStr = "周一"
        3 -> weekStr = "周二"
        4 -> weekStr = "周三"
        5 -> weekStr = "周四"
        6 -> weekStr = "周五"
        7 -> weekStr = "周六"
    }
    return weekStr
}

internal fun String?.toCalendar(pattern: String): Calendar? =
    this?.let {
        val sdf = LDUtils.getSimpleDateFormat(pattern)
        try {
            sdf.parse(this)
        } catch (e: ParseException) {
            null
        }
    }?.let {
        val calendar = Calendar.getInstance()
        calendar.time = it
        calendar
    }

internal fun Calendar?.toString(pattern: String): String? =
    this?.let {
        val sdf = LDUtils.getSimpleDateFormat(pattern)
        sdf.format(it.time)
    }