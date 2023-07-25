package com.laconichy.dialog.extension

import android.app.Activity

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/24
 *   desc：
 * </pre>
 */

internal fun Activity?.isAlive(): Boolean = this?.let { !isFinishing && !isDestroyed } ?: false