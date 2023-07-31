package com.laconichy.auto

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import java.lang.reflect.Method

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/26
 *   desc：
 * </pre>
 */
open class ClearEditText(
    context: Context,
    attrs: AttributeSet?,
) : AppCompatEditText(context, attrs) {

    /**
     * 清空icon 根据情况，是否需要显示
     * 有内容并且有焦点时，才显示；否则就隐藏
     */
    private var clearIconVisible = false

    /**
     * 清空icon 是否显示（是否需要清空的这个功能）
     * 给用户进行控制
     */
    private var mClearVisible: Boolean

    /**
     * 清空icon
     */
    private var drawableEndClear: Drawable? = null

    /**
     * 是否值引起的改变
     * 用于区分「值改变」还是「输入改变」
     */
    var valueChange = false

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.autoClearEditText, 0, 0)
        val resourceId = attributes.getResourceId(R.styleable.autoClearEditText_autoClearIcon, R.drawable.auto_search_clear)
        mClearVisible = attributes.getBoolean(R.styleable.autoClearEditText_autoClearVisible, true)
        attributes.recycle()

        setClearIcon(resourceId)
        if (mClearVisible && compoundDrawablePadding == 0) {
            compoundDrawablePadding = AutoUtils.dp2px(4f)
        }

        setOnFocusChangeListener { view, hasFocus ->
            setCompoundDrawablesWithIntrinsicBounds(hasFocus && length() > 0)
            onSelfFocusChangeListener?.invoke(view, hasFocus)
        }

        doAfterTextChanged {
            setCompoundDrawablesWithIntrinsicBounds(it != null && isFocused && it.isNotEmpty())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val drawableEnd = getDrawablesByValue()[2]
            if (drawableEnd != null && event.x > width - paddingEnd - drawableEnd.intrinsicWidth) {
                if (clearIconVisible) {
                    valueChange = true
                    text?.clear()
                    valueChange = false
                    onClearListener?.invoke(ClearMode.Click)
                }
                invokeShowSoftInput(false)
                return super.onTouchEvent(event)
            }
            invokeShowSoftInput(true)
        }
        return super.onTouchEvent(event)
    }

    private fun setClearIcon(@DrawableRes resId: Int) {
        drawableEndClear = ContextCompat.getDrawable(context, resId)
    }

    private fun setCompoundDrawablesWithIntrinsicBounds(clearIconVisible: Boolean) {
        if (!mClearVisible) return
        val drawables: Array<Drawable?> = getDrawablesByValue()
        val visible = drawables[2] != null
        if (clearIconVisible != visible) {
            this.clearIconVisible = clearIconVisible
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawables[0],
                drawables[1],
                if (clearIconVisible) drawableEndClear else null,
                drawables[3]
            )
        }
    }

    /**
     * 对于android:drawableRight，应该使用getCompoundDrawables()，
     * 而对于android:drawableEnd，应该使用getCompoundDrawablesRelative()。
     */
    private fun getDrawablesByValue(): Array<Drawable?> {
        val drawables = compoundDrawablesRelative
        return if (drawables[0] != null || drawables[1] != null || drawables[2] != null || drawables[3] != null) {
            drawables
        } else {
            compoundDrawables
        }
    }

    private fun invokeShowSoftInput(isShow: Boolean) {
        val cls = EditText::class.java
        val setSoftInputShownOnFocus: Method
        try {
            setSoftInputShownOnFocus =
                cls.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            setSoftInputShownOnFocus.isAccessible = true
            setSoftInputShownOnFocus.invoke(this, isShow)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
//        super.setOnFocusChangeListener(l)
//    }

    private var onSelfFocusChangeListener: ((view: View, hasFocus: Boolean) -> Unit)? = null
    fun setOnSelfFocusChangeListener(listener: ((view: View, hasFocus: Boolean) -> Unit)?) {
        this.onSelfFocusChangeListener = listener
    }

    fun getOnSelfFocusChangeListener() = onSelfFocusChangeListener

    private var onClearListener: ((mode: ClearMode) -> Unit)? = null
    fun setOnClearListener(listener: ((mode: ClearMode) -> Unit)?) {
        this.onClearListener = listener
    }

    fun getOnClearListener() = onClearListener

}

