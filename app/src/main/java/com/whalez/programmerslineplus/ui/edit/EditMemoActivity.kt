package com.whalez.programmerslineplus.ui.edit

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
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
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_PHOTO
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import kotlinx.android.synthetic.main.activity_edit_memo.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class EditMemoActivity : AppCompatActivity() {

    private val imgLoadOptionsMenu by powerMenu(ImageLoadOptionsFactory::class)

    private val photoList = ArrayList<Uri>()
    private val photoAdapter = PhotoAdapter(photoList)

    private var permissionChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memo)

        val intent = intent

        // 수정버튼을 클릭해서 들어온 경우 intent로 전달받은 텍스트, 이미지 불러오기.
        if (intent.hasExtra(EXTRA_ID)) {
            tv_bar_title.visibility = View.GONE
            val params = btn_add_photo.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_END, R.id.rl_appbar)
            et_title.setText(intent.getStringExtra(EXTRA_TITLE))
            et_content.setText(intent.getStringExtra(EXTRA_CONTENT))
            val imgNames = intent.getStringArrayListExtra(EXTRA_PHOTO)!!
            for (imgName in imgNames) {
                val imgUri = Uri.fromFile(File("${File(cacheDir.toString())}/${imgName}.jpg"))
                photoList.add(imgUri)
            }
        }

        val photoLayoutManager = LinearLayoutManager(applicationContext)
        photoLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_photo.layoutManager = photoLayoutManager
        rv_photo.itemAnimator = DefaultItemAnimator()
        rv_photo.adapter = photoAdapter

        callPermissions()

        // 사진 추가 버튼
        btn_add_photo.setOnClickListener { imgLoadOptionsMenu.showAsAnchorCenter(it) }
        imgLoadOptionsMenu.setOnMenuItemClickListener { position, item ->

            when (position) {
                ImageLoadOptionsFactory.FROM_CAMERA -> {
                    if (permissionChecked) {
                        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@EditMemoActivity,
                            resources.getString(R.string.permission_require_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ImageLoadOptionsFactory.FROM_ALBUM -> {
                    if (permissionChecked) {
                        goToAlbum()
                    } else {
                        Toast.makeText(
                            this@EditMemoActivity,
                            resources.getString(R.string.permission_require_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ImageLoadOptionsFactory.FROM_URL -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 뒤로가기 버튼 클릭
        btn_back.setOnClickListener { finish() }

        // 저장하기 버튼 클릭
        btn_save.setOnClickListener {
            startSaveProgress()
            val title = et_title.text.toString().trim()
            val content = et_content.text.toString().trim()
            if (title.isEmpty() && content.isEmpty() && photoList.isEmpty()) {
                Toast.makeText(this, "저장할 내용이 없습니다!", Toast.LENGTH_SHORT).show()
                stopSaveProgress()
                return@setOnClickListener
            }
            val photos = ArrayList<String>()
            for (photoUri in photoList) {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, photoUri)
                    ImageDecoder.decodeBitmap(source)
                }
                val imgName = UUID.randomUUID().toString()
                saveBitmapOnCache(bitmap, imgName)
                photos.add(imgName)
            }

            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_CONTENT, content)
            intent.putExtra(EXTRA_PHOTO, photos)

            val id = intent.getIntExtra(EXTRA_ID, -1)
            if (id != -1) {
                intent.putExtra(EXTRA_ID, id)
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == ImageLoadOptionsFactory.FROM_ALBUM) {
            photoList.add(data.data!!)
            photoAdapter.notifyDataSetChanged()
        }
    }

    private fun goToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
        )
        startActivityForResult(intent, ImageLoadOptionsFactory.FROM_ALBUM)
    }

    private fun startSaveProgress() {
        fl_progressbar.visibility = View.VISIBLE
        fl_progressbar.bringToFront()
        et_title.isEnabled = false
        et_content.isEnabled = false
        btn_save.isEnabled = false
    }

    private fun stopSaveProgress() {
        fl_progressbar.visibility = View.GONE
        et_title.isEnabled = true
        et_content.isEnabled = true
        btn_save.isEnabled = true
    }

    private fun saveBitmapOnCache(bitmap: Bitmap, imgName: String) {
        // storage에 파일 인스턴스 생성
        val tempFile = File(cacheDir, "$imgName.jpg")
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
            Log.d(TAG, "exception: " + e.message)
        }
    }

    private fun callPermissions() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.d("kkk", "저장소 접근 권한 주어짐")
                permissionChecked = true
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.d("kkk", "저장소 접근 권한 거절")
                permissionChecked = false

            }
        }
        TedPermission.with(this@EditMemoActivity)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_require_msg))
            .setDeniedMessage(resources.getString(R.string.permission_denied_msg))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).check()
    }
}
