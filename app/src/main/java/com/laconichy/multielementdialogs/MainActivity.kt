package com.laconichy.multielementdialogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.laconichy.dialog.PlateDialog

class MainActivity : AppCompatActivity() {

    private lateinit var bt_plate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListeners()
    }

    private fun initViews() {
        bt_plate = findViewById(R.id.bt_plate);
    }

    private fun initListeners() {
        bt_plate.setOnClickListener {
            showPlateDialog()
        }
    }

    private fun showPlateDialog() {
        PlateDialog(this).apply {
            setData("川")
            setOnConfirmClickListener { dialog, data ->
                Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
            }
        }.show()

        val a = PlateDialog(this)
    }

}