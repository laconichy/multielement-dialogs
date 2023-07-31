package com.laconichy.multielementdialogs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.laconichy.auto.AutoCompleteEditText
import com.laconichy.multielementdialogs.Person
import com.laconichy.multielementdialogs.R
import com.laconichy.multielementdialogs.responseData

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/28
 *   desc：
 * </pre>
 */
class TestAutoAdapter : RecyclerView.Adapter<TestAutoAdapter.MyViewHolder>() {

    lateinit var context: Context
    var data: MutableList<Person> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_auto_complete_et, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            tv_code.text = data[position].age.toString()

            acet_input.setOnInputChangedListener {
                val responseData = responseData().filter { p ->
                    p.name?.contains(it.toString()) ?: false
                }
                acet_input.data = responseData
            }.setOnBindViewListener { adapter, tv, position ->
                val item = adapter.data[position]
                tv.text = "${item.name}"
            }.setOnItemClickListener { adapter, v, position ->
                Toast.makeText(context, adapter.data[position].name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_code: TextView = itemView.findViewById(R.id.tv_code)
        val acet_input: AutoCompleteEditText<Person> = itemView.findViewById(R.id.acet_input)
    }
}