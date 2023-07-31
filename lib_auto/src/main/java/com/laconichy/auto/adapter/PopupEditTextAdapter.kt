package com.laconichy.auto.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.laconichy.auto.R

/**
 * <pre>
 *   author：laconichy
 *   time：2023/7/27
 *   desc：
 * </pre>
 */
class PopupEditTextAdapter<T>(
    var data: List<T> = emptyList()
) : RecyclerView.Adapter<PopupViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupViewHolder {
        mContext = parent.context
        val viewHolder = PopupViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.auto_item_popup_edit_text,
                parent,
                false
            )
        )
        onItemClickListener?.let {
            viewHolder.itemView.setOnClickListener { v ->
                it.invoke(this, v, viewHolder.layoutPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PopupViewHolder, position: Int) {
        val item = data[position]
        holder.apply {
            onBindViewListener?.invoke(this@PopupEditTextAdapter, tv_content, position) ?: run {
                tv_content.text = getContentText(item)
            }
            itemView.minimumHeight = 1
        }
    }

    override fun getItemCount(): Int = data.size

    private fun getContentText(item: T?): String {
        return item?.toString() ?: ""
    }

    private var onBindViewListener: ((adapter: PopupEditTextAdapter<T>, tv: TextView, position: Int) -> Unit)? = null
    fun setOnBindViewListener(onBindViewListener: ((adapter: PopupEditTextAdapter<T>, tv: TextView, position: Int) -> Unit)?) {
        this.onBindViewListener = onBindViewListener
    }

    private var onItemClickListener: ((adapter: PopupEditTextAdapter<T>, tv: View, position: Int) -> Unit)? = null
    fun setOnItemClickListener(onItemClickListener: ((adapter: PopupEditTextAdapter<T>, v: View, position: Int) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

}

class PopupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_content: TextView = itemView.findViewById(R.id.tv_content)
    val view_line: View = itemView.findViewById(R.id.view_line)
}