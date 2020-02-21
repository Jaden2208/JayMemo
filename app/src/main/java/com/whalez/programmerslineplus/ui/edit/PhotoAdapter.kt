package com.whalez.programmerslineplus.ui.edit

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoAdapter(private val photoList: ArrayList<Uri>) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)
        return PhotoHolder(inflate)
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val photoUri = photoList[position]
        Glide.with(holder.itemView)
            .load(photoUri)
            .into(holder.imageView)

        holder.btnDelete.setOnClickListener {
            photoList.removeAt(position)
            notifyItemRangeChanged(position, itemCount)
            notifyItemRemoved(position)
//            notifyDataSetChanged()
            Log.d(TAG, "item removed at $position")
            Log.d(TAG, "photoList size : ${photoList.size}")
        }
    }

    inner class PhotoHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.iv_photo_item
        val btnDelete: ImageButton = itemView.btn_delete_item
    }
}