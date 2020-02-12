package com.whalez.programmerslineplus.ui.edit

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.skydoves.powermenu.kotlin.powerMenu
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.ui.edit.ImageLoadOptionsFactory.Companion.FROM_ALBUM
import com.whalez.programmerslineplus.ui.edit.ImageLoadOptionsFactory.Companion.FROM_CAMERA
import com.whalez.programmerslineplus.ui.edit.ImageLoadOptionsFactory.Companion.FROM_URL
import com.whalez.programmerslineplus.utils.ConstValues.Companion.ADD_MODE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_THUMBNAIL
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.VIEW_MODE
import kotlinx.android.synthetic.main.activity_edit_memo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class EditMemoActivity : AppCompatActivity() {

    private var mode = ADD_MODE

    private val imgLoadOptionsMenu by powerMenu(ImageLoadOptionsFactory::class)
    private val imgSlideradapter = ImageSliderAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memo)

        val intent = intent
        // 아이템을 눌러서 들어온 경우 보기 모드로 변경
        if (intent.hasExtra(EXTRA_ID)) {
            mode = VIEW_MODE
            setViewMode(intent)
        }

        // 보기 모드에서 수정 버튼 클릭
        if (mode == VIEW_MODE) {
            btn_edit.setOnClickListener { setEditMode() }
        }

        if (mode != VIEW_MODE) {
            callExternalStoragePermission()
        }


        // 이미지 로드
        btn_add_img.setOnClickListener { imgLoadOptionsMenu.showAsAnchorCenter(it) }
        imgLoadOptionsMenu.setOnMenuItemClickListener { position, item ->
            when (position) {
                FROM_CAMERA -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                }
                FROM_ALBUM -> {
                    goToAlbum()
                }
                FROM_URL -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 뒤로가기 버튼 클릭
        btn_back.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == FROM_ALBUM) {
            Glide.with(this@EditMemoActivity)
                .load(data.data)
                .into(iv_thumbnail)
        }
    }

    // 저장하기 버튼 클릭
    fun saveMemo(view: View) {
        val title = et_title.text.toString().trim()
        val content = et_content.text.toString().trim()
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }
        // 비트 맵을 캐시에 저장
        val bitmap = (iv_thumbnail.drawable as BitmapDrawable).bitmap
        val imgName = UUID.randomUUID().toString()
        saveBitmapOnCache(bitmap, imgName)

        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_CONTENT, content)
        data.putExtra(EXTRA_THUMBNAIL, imgName)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }

        setResult(RESULT_OK, data)
        finish()
    }

    private fun saveBitmapOnCache(bitmap: Bitmap, imgName: String) {
        // 내부저장소 캐시 경로 받아오기
        val cacheDir = cacheDir
        Log.d("kkk cacheDir when Save", cacheDir.toString())

        // 저장할 파일 이름
        val fileName = "$imgName.jpg"

        // storage에 파일 인스턴스 생성
        val tempFile = File(cacheDir, fileName)

        try {
            // 자동으로 빈 파일 생성
            tempFile.createNewFile()
            // 파일을 쓸 수 있는 스트림을 준비
            val fileOutputStream = FileOutputStream(tempFile)
            // compress 함수를 사용해 스트림에 비트맵을 저장
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            // 스트림 사용후 close
            fileOutputStream.close()
        } catch (e: Exception) {
            Log.d("kkk", "exception: " + e.message)
        }
    }

    private fun setViewMode(intent: Intent) {
        tv_bar_title.visibility = View.GONE
        btn_edit.visibility = View.VISIBLE
        et_title.setText(intent.getStringExtra(EXTRA_TITLE))
        et_title.isEnabled = false
        et_content.setText(intent.getStringExtra(EXTRA_CONTENT))
        et_content.isEnabled = false
        btn_save.visibility = View.GONE
        image_scrollview.visibility = View.GONE
        cv_imgSlider.visibility = View.VISIBLE

//        var params = cv_imgSlider.layoutParams as RelativeLayout.LayoutParams
//        params.addRule(RelativeLayout.BELOW, R.id.et_title)
        val params = et_content.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.cv_imgSlider)

        val imgName = intent.getStringExtra(EXTRA_THUMBNAIL)
        if (imgName != null) {
            imageSlider.sliderAdapter = imgSlideradapter
            renewItems(imgName)
            imageSlider.setIndicatorAnimation(IndicatorAnimations.SLIDE)
            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        }
    }

    private fun setEditMode() {
        btn_edit.visibility = View.GONE
        et_title.isEnabled = true
        et_content.isEnabled = true
        btn_save.visibility = View.VISIBLE
        image_scrollview.visibility = View.VISIBLE
        cv_imgSlider.visibility = View.GONE
        val params = et_content.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.image_scrollview)
        val imgName = intent.getStringExtra(EXTRA_THUMBNAIL)
        if (imgName != null) {
            val imgBitmap = getBitmapFromCacheDir(imgName)
            Glide.with(this@EditMemoActivity)
                .load(imgBitmap)
                .into(iv_thumbnail)
        }
    }

    private fun renewItems(imgName: String) {
        val img = getBitmapFromCacheDir(imgName)
        val sliderItemList = ArrayList<Bitmap>()
//        imgName을 배열로 받으면 그 배열 길이만큼 반복
        sliderItemList.add(img)
        imgSlideradapter.renewItems(sliderItemList)
    }

    private fun getBitmapFromCacheDir(imgName: String): Bitmap {
        val file = File(cacheDir.toString())
        Log.d("kkk cacheDir in Adapter", cacheDir.toString())

        val files: Array<File> = file.listFiles()!!
        lateinit var imgBitmap: Bitmap
        for (tempFile in files) {
            if (tempFile.name.contains(imgName)) {
                imgBitmap = BitmapFactory.decodeFile("${file}/${tempFile.name}")
            }
        }
        return imgBitmap
    }

    private fun callExternalStoragePermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.d("kkk", "저장소 접근 권한 주어짐")
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.d("kkk", "저장소 접근 권한 거절")
            }
        }
        TedPermission.with(this@EditMemoActivity)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_require_msg))
            .setDeniedMessage(resources.getString(R.string.permission_denied_msg))
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }


    private fun goToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
        )
        startActivityForResult(intent, FROM_ALBUM)
    }


}
