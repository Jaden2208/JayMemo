package com.whalez.programmerslineplus.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.room.data.Memo
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import kotlinx.android.synthetic.main.memo_item.view.*
import org.joda.time.DateTime
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MemoAdapter(private val context: Context) : RecyclerView.Adapter<MemoAdapter.MemoHolder>(), Filterable {

    private var memos: List<Memo> = ArrayList()
    private var filteredMemos: List<Memo> = ArrayList()
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.memo_item, parent, false)
        return MemoHolder(inflate)
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        val currentMemo = filteredMemos[position]
        holder.title.text = currentMemo.title
        holder.content.text = currentMemo.content
        holder.timestamp.text = DateTime(currentMemo.timestamp).toString("yyyy년 MM월 dd일 HH:mm:ss")
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

    override fun getItemCount(): Int = filteredMemos.size

    fun getMemoAt(position: Int): Memo {
        return filteredMemos[position]
    }

    fun setMemos(memos: List<Memo>) {
        this.filteredMemos = memos
        this.memos = memos
        notifyDataSetChanged()
    }

    inner class MemoHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title
        val content: TextView = itemView.tv_content
        val thumbnail: ImageView = itemView.iv_thumbnail
        val timestamp: TextView = itemView.tv_timestamp
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(filteredMemos[adapterPosition], itemView)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(memo: Memo, view: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var searchText = constraint.toString()
                if(searchText.isEmpty()) {
                    filteredMemos = memos
                } else {
                    val filteringMemos = ArrayList<Memo>()
                    searchText = searchText.toLowerCase(Locale.getDefault())
                    Log.d(TAG, "searchText : $searchText")
                    for(memo in memos){
                        val title = memo.title.toLowerCase(Locale.getDefault())
                        val content = memo.content.toLowerCase(Locale.getDefault())
                        if(title.contains(searchText) || content.contains(searchText)){
                            filteringMemos.add(memo)
                        }
                    }
                    filteredMemos = filteringMemos
                }
                val filterResults = FilterResults()
                filterResults.values = filteredMemos
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


}