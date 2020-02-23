package com.whalez.programmerslineplus.ui.detail

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smarteist.autoimageslider.SliderViewAdapter
import com.whalez.programmerslineplus.R
import kotlinx.android.synthetic.main.image_slider_item.view.*

class ImageSliderAdapter : SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH>() {

    private var sliderItems = ArrayList<Uri>()
    lateinit var itemClick: ItemClick

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_slider_item, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(
        viewHolder: SliderAdapterVH,
        position: Int
    ) {
        val sliderItem = sliderItems[position]
         Glide.with(viewHolder.itemView)
             .load(sliderItem)
             .into(viewHolder.imageView)
        viewHolder.imageView.setOnClickListener {
            itemClick.onClick(it, position)
        }
    }

    override fun getCount(): Int { //slider view count could be dynamic size
        return sliderItems.size
    }

    fun renewItems(sliderItems: ArrayList<Uri>) {
        this.sliderItems = sliderItems
        notifyDataSetChanged()
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    inner class SliderAdapterVH(val itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {
        var imageView: ImageView = itemView.iv_image_slider_item
    }

}