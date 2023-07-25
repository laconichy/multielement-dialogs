package com.laconichy.dialog.dialog

import android.content.Context
import android.view.Gravity
import android.widget.CalendarView
import android.widget.TextView
import com.laconichy.dialog.R
import com.laconichy.dialog.extension.getScreenWidthAndHeight
import com.laconichy.dialog.extension.onClick
import com.laconichy.dialog.extension.toCalendar
import com.laconichy.dialog.extension.toString
import com.laconichy.dialog.extension.toWeekStr
import java.util.Calendar
import java.util.Locale

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/24
 *   desc：CalendarDialog
 * </pre>
 */
class CalendarDialog(
    context: Context,
    private val pattern: String = "yyyy-MM-dd",
    themeResId: Int = R.style.ld_DialogNormalStyle
) : LDBaseDialog(context, themeResId) {

    private lateinit var tv_year: TextView
    private lateinit var tv_month_day: TextView
    private lateinit var calendar_view: CalendarView
    private lateinit var tv_cancel: TextView
    private lateinit var tv_confirm: TextView

    init {
        setContentView(R.layout.ld_dialog_calendar)
        initViews()
        initListeners()
        setWindowManager()
    }

    /**
     * initialize views
     */
    private fun initViews() {
        tv_year = findViewById(R.id.tv_year)
        tv_month_day = findViewById(R.id.tv_month_day)
        calendar_view = findViewById(R.id.calendar_view)
        tv_cancel = findViewById(R.id.tv_cancel)
        tv_confirm = findViewById(R.id.tv_confirm)
    }

    /**
     * initialize listeners
     */
    private fun initListeners() {
        calendar_view.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            mCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
                setDate(this)
            }
        }
        
        tv_cancel.onClick {
            dismiss()
        }

        tv_confirm.onClick {
            onConfirmClickListener?.invoke(mCalendar, mCalendar.toString(pattern)!!)
            dismiss()
        }
    }

    /**
     * Set the properties of the window
     */
    private fun setWindowManager() {
        window?.let {
            val (width, height) = it.context.getScreenWidthAndHeight()
            val lp = it.attributes
            lp.width = (width * 0.80).toInt()
            lp.height = (height * 0.85).toInt()
            lp.gravity = Gravity.CENTER
            it.attributes = lp
        }
    }

    /**
     * Set the text data for the View
     *
     * @param calendar Calendar instance
     */
    private fun setViewData(calendar: Calendar) {
        calendar.apply {
            tv_year.text = "${get(Calendar.YEAR)}年"
            tv_month_day.text = String.format(
                Locale.getDefault(),
                "%d月%d日%s",
                get(Calendar.MONTH) + 1,
                get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.DAY_OF_WEEK).toWeekStr()
            )
        }
    }

    /**
     * Set a date for dialog
     *
     * @param yearOfMonthOfDay The characters of the year, month and day. The format can be specified by using pattern in the instance
     */
    fun setDate(yearOfMonthOfDay: String?): CalendarDialog {
        return setDate(yearOfMonthOfDay.toCalendar(pattern))
    }

    private var mCalendar: Calendar = Calendar.getInstance()
    /**
     * Set a date for dialog
     *
     * @param calendar Calendar instance
     */
    fun setDate(calendar: Calendar?): CalendarDialog {
        mCalendar = (calendar ?: Calendar.getInstance()).apply {
            calendar_view.date = timeInMillis
            setViewData(this)
        }
        return this
    }

    private var onConfirmClickListener: ((calendar: Calendar, yearOfMonthOfDay: String) -> Unit)? = null

    /**
     * Confirm button click event
     */
    fun setOnConfirmClickListener(listener: ((calendar: Calendar, yearOfMonthOfDay: String) -> Unit)?) {
        this.onConfirmClickListener = listener
    }

    /**
     * You can set multiple properties and open dialog in this way
     */
    inline fun show(block: CalendarDialog.() -> Unit): CalendarDialog = apply {
        this.block()
        this.show()
    }

}