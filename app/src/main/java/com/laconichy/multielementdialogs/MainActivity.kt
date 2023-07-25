package com.laconichy.multielementdialogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.laconichy.dialog.dialog.CalendarDialog
import com.laconichy.dialog.dialog.PlateDialog

class MainActivity : AppCompatActivity() {

    private lateinit var bt_plate: Button
    private lateinit var bt_calendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListeners()
    }

    private fun initViews() {
        bt_plate = findViewById(R.id.bt_plate)
        bt_calendar = findViewById(R.id.bt_calendar)
    }

    private fun initListeners() {
        bt_plate.setOnClickListener {
            showPlateDialog()
        }

        bt_calendar.setOnClickListener {
            showCalendarDialog()
        }
    }

    private fun showPlateDialog() {
        // example 1
        PlateDialog(this).show {
            setData("川A88888")
            setOnConfirmClickListener {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        // example 2
//        PlateDialog(this).apply {
//            setData("川A88888")
//            setOnConfirmClickListener {
//                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
//            }
//        }.show()
    }

    var date: String = "2023-7-24"
    private fun showCalendarDialog() {
        // example 1
        CalendarDialog(this).show {
            setDate(date)
            setOnConfirmClickListener { calendar, yearOfMonthOfDay ->
                date = yearOfMonthOfDay
                Toast.makeText(this@MainActivity, yearOfMonthOfDay, Toast.LENGTH_SHORT).show()
            }
        }

        // example 2
//        CalendarDialog(this, "yyyy/MM/dd").show {
//            setDate(date)
//            setOnConfirmClickListener { calendar, yearOfMonthOfDay ->
//                date = yearOfMonthOfDay
//                Toast.makeText(this@MainActivity, yearOfMonthOfDay, Toast.LENGTH_SHORT).show()
//            }
//        }
    }

}