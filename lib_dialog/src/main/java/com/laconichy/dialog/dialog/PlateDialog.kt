package com.laconichy.dialog.dialog

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.laconichy.dialog.R
import com.laconichy.dialog.extension.getScreenWidthAndHeight

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/20
 *   desc：PlateDialog
 * </pre>
 */
class PlateDialog(
    context: Context,
    themeResId: Int = R.style.ld_DialogNormalStyle
) : LDBaseDialog(context, themeResId) {

    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var tv3: TextView
    private lateinit var tv4: TextView
    private lateinit var tv5: TextView
    private lateinit var tv6: TextView
    private lateinit var tv7: TextView
    private lateinit var tv8: TextView
    private lateinit var tv_cancel: TextView
    private lateinit var tv_confirm: TextView
    private lateinit var ll_provinces_keyboard: LinearLayout
    private lateinit var ll_letter_keyboard: LinearLayout

    /**
     * provinces
     */
    private val provinces = arrayOf(
        arrayOf("京", "津", "渝", "沪", "冀", "晋", "辽", "吉", "黑", "苏"),
        arrayOf("浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "琼"),
        arrayOf("川", "贵", "云", "陕", "甘", "青", "蒙", "桂", "宁", "新"),
        arrayOf("ABC", "藏", "使", "领", "警", "学", "港", "澳", "delete")
    )

    /**
     * letters
     */
    private val letters = arrayOf(
        arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        arrayOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        arrayOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        arrayOf("省份", "Z", "X", "C", "V", "B", "N", "M", "delete")
    )

    init {
        setContentView(R.layout.ld_dialog_plate)
        initViews()
        initListeners()
        setWindowManager()

        provinceKeyboard()
        letterKeyboard()
        setInputIndex(0)
        setKeyboardStatus(0)
    }

    /**
     * initialize views
     */
    private fun initViews() {
        tv1 = findViewById(R.id.tv1)
        tv2 = findViewById(R.id.tv2)
        tv3 = findViewById(R.id.tv3)
        tv4 = findViewById(R.id.tv4)
        tv5 = findViewById(R.id.tv5)
        tv6 = findViewById(R.id.tv6)
        tv7 = findViewById(R.id.tv7)
        tv8 = findViewById(R.id.tv8)
        tv_cancel = findViewById(R.id.tv_cancel)
        tv_confirm = findViewById(R.id.tv_confirm)
        ll_provinces_keyboard = findViewById(R.id.ll_provinces_keyboard)
        ll_letter_keyboard = findViewById(R.id.ll_letter_keyboard)
    }

    /**
     * initialize listeners
     */
    private fun initListeners() {
        tv1.setOnClickListener {
            setInputIndex(0)
            setKeyboardStatus(0)
        }
        tv2.setOnClickListener {
            setInputIndex(1)
            setKeyboardStatus(1)
        }
        tv3.setOnClickListener {
            setInputIndex(2)
            setKeyboardStatus(1)
        }
        tv4.setOnClickListener {
            setInputIndex(3)
            setKeyboardStatus(1)
        }
        tv5.setOnClickListener {
            setInputIndex(4)
            setKeyboardStatus(1)
        }
        tv6.setOnClickListener {
            setInputIndex(5)
            setKeyboardStatus(1)
        }
        tv7.setOnClickListener {
            setInputIndex(6)
            setKeyboardStatus(1)
        }
        tv8.setOnClickListener {
            setInputIndex(7)
            setKeyboardStatus(1)
        }
        tv_cancel.setOnClickListener {
            dismiss()
        }
        tv_cancel.setOnClickListener {
            dismiss()
        }

        tv_confirm.setOnClickListener {
            if (isPassValid()) {
                onConfirmClickListener?.invoke(getContentText())
                dismiss()
            } else {
                Toast.makeText(context, "请输入正确的车牌号", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Set the properties of the window
     */
    private fun setWindowManager() {
        window?.let {
            val (width, _) = it.context.getScreenWidthAndHeight()
            val lp = it.attributes
            lp.width = width
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.CENTER
            it.attributes = lp
        }
    }

    private var mInputIndex = 0

    /**
     * Sets the index of the specified input field
     */
    private fun setInputIndex(s: Int) {
        var status = s
        if (status < 0) {
            status = 0
        } else if (status > 7) {
            status = 7
        }
        tv1.isSelected = status == 0
        tv2.isSelected = status == 1
        tv3.isSelected = status == 2
        tv4.isSelected = status == 3
        tv5.isSelected = status == 4
        tv6.isSelected = status == 5
        tv7.isSelected = status == 6
        tv8.isSelected = status == 7
        mInputIndex = status
    }

    private var mKeyboardStatus = 0

    /**
     * The province keyboard or alphabetic keyboard should now be displayed
     *
     * @param status 0：province VISIBLE
     *               !0：letter VISIBLE
     */
    private fun setKeyboardStatus(status: Int) {
        ll_provinces_keyboard.visibility = if (status == 0) View.VISIBLE else View.GONE
        ll_letter_keyboard.visibility = if (status != 0) View.VISIBLE else View.GONE
        mKeyboardStatus = status
    }

    /**
     * Keyboard click event
     */
    private fun keyboardViewClick(text: String) {
        when (text) {
            "ABC" -> setKeyboardStatus(1)
            "省份" -> setKeyboardStatus(0)
            "delete" -> {
                setChildContentText("", mInputIndex)
                setInputIndex(--mInputIndex)
                if (mInputIndex == 0) {
                    setKeyboardStatus(0)
                } else {
                    setKeyboardStatus(1)
                }
            }

            else -> {
                setChildContentText(text, mInputIndex)
                setInputIndex(++mInputIndex)
                if (mInputIndex == 0) {
                    setKeyboardStatus(0)
                } else {
                    setKeyboardStatus(1)
                }
            }
        }
    }

    /**
     * Click the Confirm button to confirm the effect
     */
    private fun isPassValid(): Boolean {
        // The first 7 digits are required
        val isExistEmpty =
            (TextUtils.isEmpty(tv1.text.toString()) || TextUtils.isEmpty(tv2.text.toString())
                    || TextUtils.isEmpty(tv3.text.toString()) || TextUtils.isEmpty(tv4.text.toString())
                    || TextUtils.isEmpty(tv5.text.toString()) || TextUtils.isEmpty(tv6.text.toString())
                    || TextUtils.isEmpty(tv7.text.toString()))
        // If true, clear data
        val isAllEmpty = TextUtils.isEmpty(getContentText())
        return !isExistEmpty || isAllEmpty
    }

    /**
     * Gets the data in the textView
     */
    private fun getContentText(): String {
        return tv1.text.toString() + tv2.text.toString() + tv3.text.toString() + tv4.text.toString() +
                tv5.text.toString() + tv6.text.toString() + tv7.text.toString() + tv8.text.toString()
    }

    /**
     * Set the contents of the textView by position
     */
    private fun setChildContentText(text: String, position: Int) {
        when (position) {
            0 -> {
                tv1.text = text
            }
            1 -> {
                tv2.text = text
            }
            2 -> {
                tv3.text = text
            }
            3 -> {
                tv4.text = text
            }
            4 -> {
                tv5.text = text
            }
            5 -> {
                tv6.text = text
            }
            6 -> {
                tv7.text = text
            }
            7 -> {
                tv8.text = text
            }
        }
    }

    /**
     * Keyboard with Chinese provinces
     */
    private fun provinceKeyboard() {
        val (width, _) = context.getScreenWidthAndHeight()
        val spaceWidth = dp2px(6f)
        val startPadding = dp2px(6f)
        val spaceTotalWidth = spaceWidth * (provinces[0].size + 1)
        val textViewWidth = (width - spaceTotalWidth - startPadding * 2) / provinces[0].size
        for (i in provinces.indices) {
            val strings = provinces[i]
            val lp1 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (i == 0) {
                lp1.setMargins(startPadding, spaceWidth, startPadding, spaceWidth)
            }  else if (i == provinces.size - 1) {
                lp1.setMargins(startPadding, 0, startPadding, spaceWidth * 3)
            } else {
                lp1.setMargins(startPadding, 0, startPadding, spaceWidth)
            }
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.layoutParams = lp1
            for (j in strings.indices) {
                val item = strings[j]
                var lp2: LinearLayout.LayoutParams
                if ("delete" == item) {
                    val imageButton = ImageButton(context)
                    imageButton.tag = j
                    lp2 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    imageButton.setBackgroundResource(R.drawable.ld_selector_big_keyboard)
                    imageButton.setImageResource(R.drawable.ld_keyboard_del)
                    imageButton.layoutParams = lp2
                    imageButton.setOnClickListener {
                        keyboardViewClick(item)
                    }
                    linearLayout.addView(imageButton)
                } else {
                    val textView = newTextView()
                    textView.tag = j
                    if ("ABC" == item) {
                        lp2 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                        textView.setBackgroundResource(R.drawable.ld_selector_big_keyboard)
                    } else {
                        lp2 = LinearLayout.LayoutParams(textViewWidth, (textViewWidth * 1.5).toInt())
                        textView.setBackgroundResource(R.drawable.ld_selector_small_keyboard)
                    }
                    textView.text = item
                    textView.layoutParams = lp2
                    textView.setOnClickListener {
                        keyboardViewClick(item)
                    }
                    linearLayout.addView(textView)
                }
                if (j == 0) {
                    lp2.setMargins(spaceWidth, 0, spaceWidth, 0)
                } else {
                    lp2.setMargins(0, 0, spaceWidth, 0)
                }
            }
            ll_provinces_keyboard.addView(linearLayout)
        }
    }

    /**
     * Keyboard with letters
     */
    private fun letterKeyboard() {
        val (width, _) = context.getScreenWidthAndHeight()
        val spaceWidth = dp2px(6f)
        val startPadding = dp2px(6f)
        val spaceTotalWidth = spaceWidth * (letters[0].size + 1)
        val textViewWidth = (width - spaceTotalWidth - startPadding * 2) / letters[0].size
        for (i in letters.indices) {
            val strings = letters[i]
            val lp1 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            if (i == 0) {
                lp1.setMargins(startPadding, spaceWidth, startPadding, spaceWidth)
            }  else if (i == letters.size - 1) {
                lp1.setMargins(startPadding, 0, startPadding, spaceWidth * 3)
            } else {
                lp1.setMargins(startPadding, 0, startPadding, spaceWidth)
            }
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.layoutParams = lp1
            for (j in strings.indices) {
                val item = strings[j]
                var lp2: LinearLayout.LayoutParams
                if ("delete" == item) {
                    val imageButton = ImageButton(context)
                    imageButton.tag = j
                    lp2 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    imageButton.setBackgroundResource(R.drawable.ld_selector_big_keyboard)
                    imageButton.setImageResource(R.drawable.ld_keyboard_del)
                    imageButton.layoutParams = lp2
                    imageButton.setOnClickListener {
                        keyboardViewClick(item)
                    }
                    linearLayout.addView(imageButton)
                } else {
                    val textView = newTextView()
                    textView.tag = j
                    if ("省份" == item) {
                        lp2 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                        textView.setBackgroundResource(R.drawable.ld_selector_big_keyboard)
                    } else {
                        lp2 = LinearLayout.LayoutParams(textViewWidth, (textViewWidth * 1.5).toInt())
                        textView.setBackgroundResource(R.drawable.ld_selector_small_keyboard)
                        textView.isEnabled = "I" != item
                    }
                    textView.text = item
                    textView.layoutParams = lp2
                    textView.setOnClickListener {
                        keyboardViewClick(item)
                    }
                    linearLayout.addView(textView)
                }
                if (j == 0) {
                    lp2.setMargins(spaceWidth, 0, spaceWidth, 0)
                } else {
                    lp2.setMargins(0, 0, spaceWidth, 0)
                }
            }
            ll_letter_keyboard.addView(linearLayout)
        }
    }

    private fun newTextView(): TextView {
        val textView = TextView(context)
        textView.textSize = 14f
        textView.setTextColor(ContextCompat.getColor(context, R.color.ld_colorText))
        textView.gravity = Gravity.CENTER
        return textView
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Set data (license plate number)
     */
    fun setData(plate: String?): PlateDialog {
        if (plate != null) {
            for (i in plate.indices) {
                val charAt = plate[i]
                setChildContentText(charAt.toString(), i)
            }
            setInputIndex(plate.length)
            setKeyboardStatus(plate.length)
        }
        return this
    }

    /**
     * Get the entered license plate number
     */
    fun getData() = getContentText()

    private var onConfirmClickListener: ((data: String) -> Unit)? = null
    fun setOnConfirmClickListener(listener: ((data: String) -> Unit)?): PlateDialog {
        onConfirmClickListener = listener
        return this
    }

    override fun show() {
        super.show()
    }

    /**
     * You can set multiple properties and open dialog in this way
     */
    inline fun show(block: PlateDialog.() -> Unit): PlateDialog = apply {
        this.block()
        this.show()
    }

}