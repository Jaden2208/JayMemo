package com.whalez.programmerslineplus.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.keyIterator
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

class MemoAdapter(private val context: Context) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>()
    , Filterable {

    private var memos: List<Memo> = ArrayList()
    private var filteredMemos: List<Memo> = ArrayList()
    private lateinit var listener: OnItemClickListener
    var selectable = false
    private var selectedItems = SparseBooleanArray(0)
    var selectedItemsIds = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.memo_item, parent, false)
        return MemoViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
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

        val isMemoSelected = isItemSelected(position)
        if(isMemoSelected){
            selectedItemsIds.add(currentMemo.id)
        } else {
            selectedItemsIds.remove(currentMemo.id)
        }
        holder.itemView.isSelected = isMemoSelected
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

    inner class MemoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tv_title
        val content: TextView = itemView.tv_content
        val thumbnail: ImageView = itemView.iv_thumbnail
        val timestamp: TextView = itemView.tv_timestamp
        init {
            itemView.setOnClickListener {
                if (!selectable && adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(filteredMemos[adapterPosition], itemView)
                }
                if (selectable) {
                    toggleItemSelected(adapterPosition)

                }
            }
        }
    }

    fun toggleItemSelected(position: Int){
        if(selectedItems.get(position, false)) {
            selectedItems.delete(position)
            notifyItemChanged(position)
        } else {
            selectedItems.put(position, true)
            notifyItemChanged(position)
        }
    }
    private fun isItemSelected(position: Int): Boolean {
        return selectedItems.get(position,false)
    }
    fun clearSelectedItems() {
        for(position in selectedItems.keyIterator()) {
            selectedItems.put(position, false)
            notifyItemChanged(position)
        }
        selectedItems.clear()
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