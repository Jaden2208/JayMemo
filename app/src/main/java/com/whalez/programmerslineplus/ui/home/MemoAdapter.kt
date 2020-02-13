package com.whalez.programmerslineplus.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
        if (currentMemo.photos[0] != "none") {
            val img = getBitmapFromCacheDir(currentMemo.photos[0])
            Glide.with(holder.thumbnail.context)
                .load(img)
                .into(holder.thumbnail)
        }

    }

    private fun getBitmapFromCacheDir(imgName: String): Bitmap {
        val file = File(context.cacheDir.toString())
        Log.d("kkk cacheDir in Adapter", context.cacheDir.toString())

        val files: Array<File> = file.listFiles()!!
        lateinit var imgBitmap: Bitmap
        for (tempFile in files) {
            if (tempFile.name.contains(imgName)) {
                Log.d("kkk find!", tempFile.name)
                Log.d("kkk file", file.toString())
                Log.d("kkk tempFile", tempFile.toString())
                imgBitmap = BitmapFactory.decodeFile("${file}/${tempFile.name}")
            }
        }
        return imgBitmap
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
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.content == newItem.content
    }

}