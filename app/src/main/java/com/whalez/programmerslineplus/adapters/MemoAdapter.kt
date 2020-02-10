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
import com.whalez.programmerslineplus.databinding.MemoItemBinding

class MemoAdapter: RecyclerView.Adapter<MemoAdapter.MemoHolder>() {

    private var memos: List<Memo> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        return MemoHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.memo_item, parent, false))
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        val memo = memos[position]
        View.OnClickListener {
            Toast.makeText(it.context, "Clicked: ${memo.title}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(memo)
            itemView.tag = memo
        }
//        holder.textViewTitle.text = currentMemo.title
//        holder.textViewContent.text = currentMemo.content
    }

    override fun getItemCount() = memos.size

    fun setMemos(memos: List<Memo>){
        this.memos = memos
        notifyDataSetChanged()
    }

    class MemoHolder(
        private val binding: MemoItemBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Memo) {
            binding.apply {
                memoItem = item
            }
        }
    }
}