package com.whalez.programmerslineplus.ui.edit

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EDIT_MODE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_PHOTO
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_PHOTO_CNT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.VIEW_MODE
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.activity_edit_memo.*
import kotlinx.android.synthetic.main.memo_item.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class EditMemoActivity : AppCompatActivity() {

    private var mode = ADD_MODE

    // Switch Add Photo Options
    private val on = "RIGHT"
    private val off = "LEFT"

    private val imgLoadOptionsMenu by powerMenu(ImageLoadOptionsFactory::class)
    private val imgSlideradapter = ImageSliderAdapter(this)

    private val photoList = ArrayList<Uri>()
    private val photoAdapter =
        PhotoAdapter(photoList)

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
            btn_edit.setOnClickListener {
                setEditMode()
                mode = EDIT_MODE
            }
        }

        if (mode != VIEW_MODE) {
            callExternalStoragePermission()
        }

        switch_add_photo.onSelectedChangeListener = object: StickySwitch.OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                when(direction.name) {
                    on -> {
                        image_scrollview.visibility = View.VISIBLE
                    }
                    off -> {
                        image_scrollview.visibility = View.GONE
                        if(mode == ADD_MODE) {
                            photoAdapter.photoList.clear()
                            photoAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }


        val photoLayoutManager = LinearLayoutManager(applicationContext)
        photoLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_photo.layoutManager = photoLayoutManager
        rv_photo.itemAnimator = DefaultItemAnimator()
        rv_photo.adapter = photoAdapter

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
        btn_back.setOnClickListener {
            when (mode) {
                EDIT_MODE -> {
                    setViewMode(intent)
                    mode = VIEW_MODE
                    photoList.clear()
                }
                else -> finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == FROM_ALBUM) {
            photoList.add(data.data!!)
            photoAdapter.notifyDataSetChanged()
        }
    }

    // 저장하기 버튼 클릭
    fun saveMemo(view: View) {
        val title = et_title.text.toString().trim()
        val content = et_content.text.toString().trim()

        val photoList = photoAdapter.photoList
        if (title.isEmpty() && content.isEmpty() && photoList.isEmpty()) {
            Toast.makeText(this, "저장할 내용이 없습니다!", Toast.LENGTH_SHORT).show()
            return
        }
        // 비트 맵을 캐시에 저장
        val photoNameList = ArrayList<String>()
        for (photoUri in photoList) {
            lateinit var bitmap: Bitmap
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media
                    .getBitmap(this.contentResolver, photoUri)
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, photoUri)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
            val imgName = UUID.randomUUID().toString()
            saveBitmapOnCache(bitmap, imgName)
            photoNameList.add(imgName)
        }

        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_CONTENT, content)
        data.putExtra(EXTRA_PHOTO, photoNameList)

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
        switch_add_photo.visibility = View.GONE

        val params = et_content.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.cv_imgSlider)

        val imgName = intent.getSerializableExtra(EXTRA_PHOTO) as ArrayList<String>
        imageSlider.sliderAdapter = imgSlideradapter
        renewItems(imgName)
        imageSlider.setIndicatorAnimation(IndicatorAnimations.SLIDE)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)

        if(imgSlideradapter.count == 0) {
            cv_imgSlider.visibility = View.GONE
        } else {
            cv_imgSlider.visibility = View.VISIBLE
        }
    }

    private fun setEditMode() {
        btn_edit.visibility = View.GONE
        et_title.isEnabled = true
        et_content.isEnabled = true
        btn_save.visibility = View.VISIBLE
        cv_imgSlider.visibility = View.GONE
        switch_add_photo.visibility = View.VISIBLE
        var params = et_content.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.BELOW, R.id.image_scrollview)
        params = switch_add_photo.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_END, R.id.rl_appbar)

        val imgNames = intent.getSerializableExtra(EXTRA_PHOTO) as ArrayList<String>
        for(imgName in imgNames) {
            val imgUri = getUriFromCacheDir(imgName)
            photoList.add(imgUri)
        }
        photoAdapter.notifyDataSetChanged()

        if (photoAdapter.itemCount == 0) {
            image_scrollview.visibility = View.GONE
        }
        else {
            image_scrollview.visibility = View.VISIBLE
            switch_add_photo.setDirection(StickySwitch.Direction.RIGHT)
        }
    }

    private fun renewItems(imgNames: ArrayList<String>) {
        val sliderItemList = ArrayList<Bitmap>()
        for(imgName in imgNames) {
            val img = getBitmapFromCacheDir(imgName)
            sliderItemList.add(img)
        }
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
                break
            }
        }
        return imgBitmap
    }

    private fun getUriFromCacheDir(imgName: String): Uri {
        val fileDir = File(cacheDir.toString())
        return Uri.fromFile(File("${fileDir}/${imgName}.jpg"))
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
