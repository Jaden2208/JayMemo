package com.whalez.programmerslineplus.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.data.Memo
import kotlinx.android.synthetic.main.memo_item.view.*

class MemoAdapter: ListAdapter<Memo, MemoAdapter.MemoHolder>(
    DiffCallback()
) {

    private lateinit var listener: OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.memo_item, parent, false)
        return MemoHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        val currentMemo = getItem(position)
        holder.title.text = currentMemo.title
        holder.content.text = currentMemo.content
    }

    fun getMemoAt(position: Int): Memo {
        return getItem(position)
    }

    inner class MemoHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.tv_title
        val content: TextView = itemView.tv_content
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(memo: Memo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}

class DiffCallback: DiffUtil.ItemCallback<Memo>() {
    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.content == newItem.content
    }

}