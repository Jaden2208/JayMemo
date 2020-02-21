package com.whalez.programmerslineplus.ui.detail

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.whalez.programmerslineplus.R
import kotlinx.android.synthetic.main.activity_full_screen_photo.*
import java.io.File

class FullScreenPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_photo)

        val imgName = intent.getStringExtra("IMAGE_NAME")
        val imgUri = Uri.fromFile(File("${File(cacheDir.toString())}/${imgName}.jpg"))
        supportPostponeEnterTransition()
        iv_fullscreen.load(imgUri) {
            supportStartPostponedEnterTransition()
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

//    class Gesture(private val context: Context) : GestureDetector.OnGestureListener {
//        override fun onShowPress(e: MotionEvent?) {
//        }
//
//        override fun onSingleTapUp(e: MotionEvent?): Boolean {
//            return false
//        }
//
//        override fun onDown(e: MotionEvent?): Boolean {
//            return false
//        }
//
//        override fun onFling(
//            e1: MotionEvent?,
//            e2: MotionEvent?,
//            velocityX: Float,
//            velocityY: Float
//        ): Boolean {
//            return false
//        }
//
//        override fun onScroll(
//            e1: MotionEvent?,
//            e2: MotionEvent?,
//            distanceX: Float,
//            distanceY: Float
//        ): Boolean {
//
//            (context as Activity).finish()
//            return true
//        }
//
//        override fun onLongPress(e: MotionEvent?) {
//        }
//
//    }
}
