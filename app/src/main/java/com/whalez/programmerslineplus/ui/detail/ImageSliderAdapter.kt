package com.whalez.programmerslineplus.ui.detail

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.whalez.programmerslineplus.R
import kotlinx.android.synthetic.main.image_slider_item.view.*

class ImageSliderAdapter : SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH>() {

    private var sliderItems = ArrayList<Uri>()

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
    }

    override fun getCount(): Int { //slider view count could be dynamic size
        return sliderItems.size
    }

    fun renewItems(sliderItems: ArrayList<Uri>) {
        this.sliderItems = sliderItems
        notifyDataSetChanged()
    }

//    fun deleteItem(position: Int) {
//        this.sliderItems.removeAt(position)
//        notifyDataSetChanged()
//    }
//
//    fun addItem(sliderItem: Uri){
//        this.sliderItems.add(sliderItem)
//        notifyDataSetChanged()
//    }
//
//    fun getAllItems() = sliderItems


    inner class SliderAdapterVH(val itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {
        var imageView: ImageView = itemView.iv_auto_image_slider
    }

}