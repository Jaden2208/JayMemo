package com.whalez.programmerslineplus.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.data.Memo
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import kotlinx.android.synthetic.main.memo_item.view.*
import java.io.File

class MemoAdapter(private val context: Context) : ListAdapter<Memo, MemoAdapter.MemoHolder>(
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
        if (currentMemo.photos.size > 0) {
            val thumbnailName = currentMemo.photos[0]
            val fileDir = File(context.cacheDir.toString())
            val imgUri = Uri.fromFile(File("${fileDir}/${thumbnailName}.jpg"))
            Glide.with(holder.thumbnail.context)
                .load(imgUri)
                .into(holder.thumbnail)
            holder.thumbnail.visibility = View.VISIBLE
        } else {
            holder.thumbnail.visibility = View.GONE
        }
    }

    fun getMemoAt(position: Int): Memo {
        return getItem(position)
    }

    inner class MemoHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title
        val content: TextView = itemView.tv_content
        val thumbnail: ImageView = itemView.iv_thumbnail
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

class DiffCallback : DiffUtil.ItemCallback<Memo>() {
    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        if(oldItem.id != newItem.id){
            Log.d(TAG, "새로운 메모 추가하기 : " + newItem.title)
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.content == newItem.content &&
                oldItem.photos == newItem.photos
    }

}