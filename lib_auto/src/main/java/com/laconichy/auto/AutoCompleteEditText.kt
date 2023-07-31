package com.laconichy.auto

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.PopupWindowCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.laconichy.auto.adapter.PopupEditTextAdapter
import java.lang.reflect.Method

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/26
 *   desc：
 * </pre>
 */
class AutoCompleteEditText<T>(
    context: Context,
    attrs: AttributeSet?,
) : ClearEditText(context, attrs) {

    private var pb_loading: ProgressBar? = null
    private var ll_state: LinearLayout? = null
    private var tv_state_msg: TextView? = null
    private var recycler_view: RecyclerView? = null
    private var textWatcher: TextWatcher? = null

    companion object {
        val sGetMaxAvailableHeightMethod: Method? =
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                try {
                    PopupWindow::class.java.getDeclaredMethod(
                        "getMaxAvailableHeight", View::class.java, Int::class.java, Boolean::class.java)
                } catch (ignore: NoSuchMethodException) {
                    null
                }
            } else null
    }

    init {

        textWatcher = doAfterTextChanged {
            it?.apply {
                if (valueChange || !hasFocus()) {
                    valueChange = false
                    if (hasFocus()) setSelection(length)
                    return@doAfterTextChanged
                }
                if (toString().isEmpty()) {
                    popupDismiss()
                    getOnClearListener()?.invoke(ClearMode.Input)
                } else {
                    onInputChangeListener?.invoke(this as CharSequence)
                }
            }
        }

    }

    private var mPopupAdapter: PopupEditTextAdapter<T>? = null
    private var mPopup: PopupWindow? = null
    private fun createPopupWindow() {
        if (mPopup == null) {
            mPopup = PopupWindow(this).apply {
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
                isOutsideTouchable = true
                isClippingEnabled = false
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            setOnDismissListener {  }

                val view = LayoutInflater.from(context)
                    .inflate(R.layout.auto_popup_edit_text, parent as ViewGroup, false)
                recycler_view = view.findViewById(R.id.recycler_view)
                ll_state = view.findViewById(R.id.ll_state)
                pb_loading = view.findViewById(R.id.pb_loading)
                tv_state_msg = view.findViewById(R.id.tv_state_msg)

                recycler_view!!.layoutManager = LinearLayoutManager(context)
                mPopupAdapter = PopupEditTextAdapter<T>().apply {
                    setOnItemClickListener { adapter, v, position ->
                        val textView = (v as LinearLayout).getChildAt(0) as TextView
                        setText(textView.text.toString())
                        onItemClickListener?.invoke(adapter, v, position)
                        popupDismiss()
                    }
                }
                recycler_view!!.adapter = mPopupAdapter

                contentView = view
            }
        }
        mPopupAdapter!!.setOnBindViewListener(onBindViewListener)
    }

    private fun getPopupAvailableHeight(): Int {
        val ignoreBottomDecorations = mPopup?.inputMethodMode == PopupWindow.INPUT_METHOD_NOT_NEEDED
        return getMaxAvailableHeight(this, 0, ignoreBottomDecorations)
    }

    private fun getMaxAvailableHeight(anchor: View, yOffset: Int, ignoreBottomDecorations: Boolean): Int {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            sGetMaxAvailableHeightMethod?.let {
                try {
                    return it.invoke(mPopup, anchor, yOffset, ignoreBottomDecorations) as Int
                } catch (ignore: Exception) {
                }
            }
            return mPopup?.getMaxAvailableHeight(anchor, yOffset) ?: 0
        } else {
            return mPopup?.getMaxAvailableHeight(anchor, yOffset, ignoreBottomDecorations) ?: 0
        }
    }

    private val mutableData: MutableList<T>
        get() {
            return when (_data) {
                is ArrayList -> _data as ArrayList
                is MutableList -> _data as MutableList
                else -> _data.toMutableList().apply { _data = this }
            }
        }

    private var _data: List<T>
        get() = mPopupAdapter?.data ?: emptyList()
        set(value) {
            createPopupWindow()
            mPopupAdapter?.data = value
            mPopupAdapter?.notifyDataSetChanged()
        }

    var data: List<T>?
        get() = _data
        set(value) {
            if (value.isNullOrEmpty()) {
                showEmptyView()
            } else {
                _data = value
                showContentView()
            }
        }

    private val popupWidth: Int
        get() = width.coerceAtLeast(AutoUtils.dp2px(120f))

    private val popupHeight: Int
        get() = getPopupAvailableHeight().coerceAtMost(measuredHeight())

    val isPopupShowing: Boolean
        get() = mPopup?.isShowing ?: false

    private fun measuredHeight(): Int {
        recycler_view!!.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return recycler_view!!.measuredHeight
    }

    private fun show(state: Int) {
        mPopup?.apply {
            val pWidth = popupWidth
            val pHeight = if (state == 1) popupHeight else AutoUtils.dp2px(44f)
            if (isPopupShowing) {
                if (!ViewCompat.isAttachedToWindow(this@AutoCompleteEditText)) {
                    return
                }
                update(this@AutoCompleteEditText, 0, 0, pWidth, pHeight)
            } else {
                inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
                width = pWidth
                height = pHeight
                PopupWindowCompat.showAsDropDown(
                    this, this@AutoCompleteEditText, 0, 0, Gravity.NO_GRAVITY)
            }
        }
    }

    private fun clearAdapterAndTop() {
        mutableData.clear()
        recycler_view?.scrollToPosition(0)
        mPopupAdapter?.notifyDataSetChanged()
    }

    fun popupDismiss() {
        mPopup?.dismiss()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        textWatcher?.let { // 还未初始化时，系统会先回调该方法
            valueChange = true
        }
        super.setText(text, type)
    }

    /**
     * 切换页面的状态
     *
     * @param state 0：加载中   1：成功并且有数据（展示内容） 2：空数据   3：报错
     * @param msg 文本信息（提示语）
     */
    private fun changePageState(state: Int, msg: String = "") {
        ll_state?.visibility = if (state != 1) View.VISIBLE else View.GONE
        pb_loading?.visibility = if (state == 0) View.VISIBLE else View.GONE
        tv_state_msg?.text = msg
        recycler_view?.visibility = if (state == 1) View.VISIBLE else View.GONE
    }

    fun showLoadingView() {
        createPopupWindow()
        changePageState(0, "正在加载中...")
        clearAdapterAndTop()
        show(0)
    }

    fun showContentView() {
        createPopupWindow()
        changePageState(1)
//        clearAdapterAndTop()
        show(1)
    }

    fun showEmptyView(empty: String = "暂时没有数据~") {
        createPopupWindow()
        changePageState(2, empty)
        clearAdapterAndTop()
        show(2)
    }

    fun showErrorView(error: String = "查找数据失败！") {
        createPopupWindow()
        changePageState(2, error)
        clearAdapterAndTop()
        show(2)
    }

    private var onInputChangeListener: ((s: CharSequence) -> Unit)? = null
    fun setOnInputChangedListener(listener: ((s: CharSequence) -> Unit)?): AutoCompleteEditText<T> {
        this.onInputChangeListener = listener
        return this
    }

    private var onBindViewListener: ((adapter: PopupEditTextAdapter<T>, tv: TextView, position: Int) -> Unit)? = null
    fun setOnBindViewListener(listener: ((adapter: PopupEditTextAdapter<T>, tv: TextView, position: Int) -> Unit)?): AutoCompleteEditText<T> {
        this.onBindViewListener = listener
        return this;
    }

    private var onItemClickListener: ((adapter: PopupEditTextAdapter<T>, tv: View, position: Int) -> Unit)? = null
    fun setOnItemClickListener(listener: ((adapter: PopupEditTextAdapter<T>, v: View, position: Int) -> Unit)?): AutoCompleteEditText<T> {
        this.onItemClickListener = listener
        return this
    }

    /**
     * 请尽量使用该方法进行文本内容的清空
     */
    fun clear() {
        valueChange = true
        text?.clear()
        getOnClearListener()?.invoke(ClearMode.Method)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = keyDispatcherState
                state.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val state = keyDispatcherState
                state?.handleUpEvent(event)
                if (event.isTracking && !event.isCanceled) {
                    popupDismiss()
                    return true
                }
            }
        }
        return super.onKeyPreIme(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                if (isPopupShowing) popupDismiss()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        popupDismiss()
    }
}

/**
 * 清空的方式
 */
sealed class ClearMode {
    /**
     * 点击清空按钮
     */
    object Click : ClearMode()

    /**
     * 输入
     */
    object Input : ClearMode()

    /**
     * 方法
     */
    object Method : ClearMode()
}