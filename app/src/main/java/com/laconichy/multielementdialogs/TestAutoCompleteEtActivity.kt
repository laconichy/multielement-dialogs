package com.laconichy.multielementdialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.laconichy.auto.AutoCompleteEditText
import com.laconichy.multielementdialogs.adapter.TestAutoAdapter


class TestAutoCompleteEtActivity : AppCompatActivity() {

    private lateinit var et_input: AutoCompleteEditText<String>
    private lateinit var bt_dialog: Button
    private lateinit var mAdapter: TestAutoAdapter
    private lateinit var recycler_view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_auto_complete_et)

        recycler_view = findViewById(R.id.recycler_view)
        bt_dialog = findViewById(R.id.bt_dialog)
        bt_dialog.setOnClickListener {
            showTestDialog()
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        mAdapter = TestAutoAdapter().apply {
            data = getTestData()
            recycler_view.adapter = this
        }
    }

    fun getTestData(): MutableList<Person> {
        val list = mutableListOf<Person>()
        for (i in 0..30) {
            val person = Person(age = i + 1)
            list.add(person)
        }
        return list
    }

    fun showTestDialog() {
        val dialog = Dialog(this).apply {
            et_input = AutoCompleteEditText<String>(this@TestAutoCompleteEtActivity, null)
            et_input.setOnInputChangedListener {
                request()
            }.setOnItemClickListener { adapter, v, position ->
                Toast.makeText(context, adapter.data[position], Toast.LENGTH_SHORT).show()
            }
            setContentView(et_input)
            show()
        }
    }

    fun request() {
        et_input.showLoadingView()
        requestNetwork(
            success = {
                et_input.data = it
        },
            error = {
                et_input.showErrorView(it)
        })
    }

    var tag: Int = 0
    /**
     * 模拟网络请求
     */
    fun requestNetwork(success: (data: List<String>?) -> Unit, error: (msg: String) -> Unit) {
        et_input.showLoadingView()
        val inputContent = et_input.text.toString().trim()
        Handler(mainLooper).postDelayed({
            tag++
            if (tag != 2) {
                val responseData = responseData2().filter { it.contains(inputContent) }
                success.invoke(responseData)
            } else {
                error.invoke("数据获取失败，请重试！")
            }
        }, 1000)
    }

}

fun responseData(): MutableList<Person> {
    val list = mutableListOf<Person>()
    for (i in 0..50) {
        val person = Person(name = "姓名123$i", age = i + 1)
        list.add(person)
    }
    return list
}

fun responseData2(): MutableList<String> =
    responseData().mapNotNull { it.name }.toMutableList()


data class Person(var name: String? = null, var age: Int? = null)
