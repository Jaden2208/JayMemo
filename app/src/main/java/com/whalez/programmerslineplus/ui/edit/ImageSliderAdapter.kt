package com.whalez.programmerslineplus.ui.edit

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderView
import com.smarteist.autoimageslider.SliderViewAdapter
import com.whalez.programmerslineplus.R
import kotlinx.android.synthetic.main.image_slider_layout_item.view.*


class ImageSliderAdapter(private val context: Context) :
    SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH>() {

    private var sliderItems = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_slider_layout_item, parent, false)
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

    fun renewItems(sliderItems: ArrayList<Bitmap>) {
        this.sliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        this.sliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: Bitmap){
        this.sliderItems.add(sliderItem)
        notifyDataSetChanged()
    }


    inner class SliderAdapterVH(val itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {
        var imageView: ImageView = itemView.iv_auto_image_slider
    }

}