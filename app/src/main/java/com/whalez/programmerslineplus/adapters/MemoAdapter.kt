package com.whalez.programmerslineplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.data.Memo
import kotlinx.android.synthetic.main.memo_item.view.*
//import com.whalez.programmerslineplus.databinding.MemoItemBinding

class MemoAdapter: RecyclerView.Adapter<MemoAdapter.MemoHolder>() {

    private var memos: List<Memo> = ArrayList()
    private lateinit var listener: OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.memo_item, parent, false)
        return MemoHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        val currentMemo = memos[position]
        holder.title.text = currentMemo.title
        holder.content.text = currentMemo.content
//        holder.apply {
//            bind(memo)
//            itemView.tag = memo
//        }
//        holder.textViewTitle.text = currentMemo.title
//        holder.textViewContent.text = currentMemo.content
    }

    override fun getItemCount() = memos.size

    fun setMemos(memos: List<Memo>){
        this.memos = memos
        notifyDataSetChanged()
    }

    fun getMemoAt(position: Int): Memo {
        return memos[position]
    }

    inner class MemoHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.tv_title
        val content: TextView = itemView.tv_content
        init {
            
            itemView.setOnClickListener {
                val position = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(memos[position])
                }

            }
        }
//        fun bind(item: Memo) {
//            binding.apply {
//                memoItem = item
//            }
//        }
    }
    interface OnItemClickListener {
        fun onItemClick(memo: Memo)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}