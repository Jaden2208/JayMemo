package com.whalez.programmerslineplus.ui.detail

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.thefuntasty.hauler.setOnDragDismissedListener
import com.whalez.programmerslineplus.R
import kotlinx.android.synthetic.main.activity_full_screen_photo.*

class FullScreenPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_photo)

        val photo = intent.getStringExtra("IMAGE_NAME")
        val imgUri = Uri.parse(photo)
        supportPostponeEnterTransition()
        iv_fullscreen.load(imgUri) {
            supportStartPostponedEnterTransition()
        }

        haulerView.setOnDragDismissedListener {
            finish()
        }
    }

    private fun ImageView.load(uri: Uri, onLoadingFinished: () -> Unit = {}) {
        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                onLoadingFinished()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                onLoadingFinished()
                return false
            }
        }

        Glide.with(this)
            .load(uri)
            .listener(listener)
            .into(this)
    }
}
