package com.whalez.programmerslineplus.ui.edit

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import kotlinx.android.synthetic.main.photo_item.view.*
import org.joda.time.DateTime

class PhotoAdapter(private val photoList: ArrayList<String>) : RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)
        return PhotoHolder(inflate)
    }

    override fun getItemCount(): Int = photoList.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val photo = photoList[position]

        Glide.with(holder.itemView)
            .load(Uri.parse(photo))
            .into(holder.imageView)

        holder.btnDelete.setOnClickListener {
            photoList.removeAt(position)
            notifyItemRangeChanged(position, itemCount)
            notifyItemRemoved(position)
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

//    private fun ImageView.load(uri: Uri, onLoadingFinished: () -> Unit = {}) {
//        val listener = object : RequestListener<Drawable> {
//            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                onLoadingFinished()
//                return false
//            }
//
//            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                onLoadingFinished()
//                return false
//            }
//        }
//
//        Glide.with(this)
//            .load(uri)
//            .listener(listener)
//            .into(this)
//    }
}