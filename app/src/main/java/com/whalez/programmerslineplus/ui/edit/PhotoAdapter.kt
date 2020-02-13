package com.whalez.programmerslineplus.ui.edit

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whalez.programmerslineplus.R
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoAdapter(val photoList: ArrayList<Uri>): RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)
        return PhotoHolder(inflate)
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val photo = photoList[position]
        Glide.with(holder.itemView)
            .load(photo)
            .into(holder.imageView)

        holder.btnDelete.setOnClickListener {
            val position = holder.adapterPosition
            photoList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    inner class PhotoHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.iv_photo_item
        val btnDelete: ImageButton = itemView.btn_delete_item
    }
}