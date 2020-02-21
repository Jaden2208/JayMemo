package com.whalez.programmerslineplus.ui.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.ui.edit.ImageLoadOptionsFactory.Companion.FROM_ALBUM
import com.whalez.programmerslineplus.ui.edit.ImageLoadOptionsFactory.Companion.FROM_CAMERA
import com.whalez.programmerslineplus.ui.edit.ImageLoadOptionsFactory.Companion.FROM_URL
import com.whalez.programmerslineplus.utils.ConstValues.Companion.ADD_MODE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EDIT_MODE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_PHOTO
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TIMESTAMP
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import com.whalez.programmerslineplus.utils.isInternetAvailable
import com.whalez.programmerslineplus.utils.longToast
import com.whalez.programmerslineplus.utils.shortToast
import kotlinx.android.synthetic.main.activity_edit_memo.*
import kotlinx.android.synthetic.main.activity_edit_memo.progressbar_layout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class EditMemoActivity : AppCompatActivity() {

    private var mode = ADD_MODE

    private val imgLoadOptionsMenu by powerMenu(ImageLoadOptionsFactory::class)

    private val photoList = ArrayList<Uri>()
    private val photoAdapter = PhotoAdapter(photoList)

    private var permissionChecked = false

    private var photoFileFromCamera: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memo)

        var originalTitle = ""
        var originalContent = ""
        val originalImage: ArrayList<String>
        val originalImageUri = ArrayList<Uri>()

        // 수정버튼을 클릭해서 들어온 경우 intent로 전달받은 텍스트, 이미지 불러오기.
        if (intent.hasExtra(EXTRA_ID)) {
            mode = EDIT_MODE
            originalTitle = intent.getStringExtra(EXTRA_TITLE)!!
            originalContent = intent.getStringExtra(EXTRA_CONTENT)!!
            originalImage = intent.getStringArrayListExtra(EXTRA_PHOTO)!!
            tv_bar_title.visibility = View.GONE
            val params = btn_add_photo.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ALIGN_PARENT_END, R.id.rl_appbar)
            et_title.setText(originalTitle)
            et_content.setText(originalContent)
            for (imgName in originalImage) {
                val imgUri = Uri.fromFile(File("${File(cacheDir.toString())}/${imgName}.jpg"))
                photoList.add(imgUri)
                originalImageUri.add(imgUri)
            }
        }

        et_title.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if((event!!.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                et_content.requestFocus()
                return@OnKeyListener true
            }
            false
        })

        // RecyclerView 초기화
        rv_photo.apply {
            val photoLayoutManager = LinearLayoutManager(applicationContext)
            photoLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = photoLayoutManager
            adapter = photoAdapter
//            itemAnimator = DefaultItemAnimator()
        }

        callPermissions()

        // 사진 추가 버튼
        btn_add_photo.setOnClickListener { imgLoadOptionsMenu.showAsAnchorCenter(it) }
        imgLoadOptionsMenu.setOnMenuItemClickListener { position, _ ->
            when (position) {
                FROM_CAMERA -> {
                    if (permissionChecked) {
                        takePicture()
                    } else {
                        shortToast(this, resources.getString(R.string.permission_require_msg))
                    }
                }
                FROM_ALBUM -> {
                    if (permissionChecked) {
                        getPictureFromGallery()
                    } else {
                        shortToast(this, resources.getString(R.string.permission_require_msg))
                    }
                }
                FROM_URL -> {
                    // URL 입력을 위한 다이얼로그 띄움
                    val builder = AlertDialog.Builder(this).create()
                    val nullParent = null
                    val dialogView = layoutInflater
                        .inflate(R.layout.input_img_url_layout, nullParent)
                    var imageUri: Uri? = null

                    val btnCancel = dialogView.findViewById<ImageButton>(R.id.btn_cancel)
                    val etUrl = dialogView.findViewById<EditText>(R.id.et_url)
                    val btnDownloadImg = dialogView.findViewById<ImageButton>(R.id.btn_download_img)
                    val imageView = dialogView.findViewById<ImageView>(R.id.iv_img_from_url)
                    val btnAddUrlImg = dialogView.findViewById<Button>(R.id.btn_add_url_img)

                    // x 버튼 클릭
                    btnCancel.setOnClickListener {
                        builder.dismiss()
                    }

                    // 이미지 다운로드 버튼 클릭
                    btnDownloadImg.setOnClickListener {
                        val imageUrl = etUrl.text.toString().trim()
                        if (imageUrl.isEmpty()) {
                            shortToast(this, "링크를 입력해주세요!")
                            return@setOnClickListener
                        }
                        if (!isInternetAvailable(this)) {
                            shortToast(this, "인터넷 연결 상태를 확인해주세요!")
                            return@setOnClickListener
                        }

                        val circularProgressDrawable = CircularProgressDrawable(this)
                        circularProgressDrawable.apply {
                            strokeWidth = 5f
                            centerRadius = 30f
                            start()
                        }
//                        circularProgressDrawable.strokeWidth = 5f
//                        circularProgressDrawable.centerRadius = 30f
//                        circularProgressDrawable.start()

                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(circularProgressDrawable)
                            .error(R.drawable.load_fail_img)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    shortToast(
                                        this@EditMemoActivity,
                                        "이미지를 로드할 수 없습니다. URL 주소 또는 인터넷 연결 상태를 확인해주세요."
                                    )
                                    imageUri = null
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    imageUri = Uri.parse(imageUrl)
                                    return false
                                }
                            }).into(imageView)
                    }

                    // 추가하기 버튼 클릭
                    btnAddUrlImg.setOnClickListener {
                        if (imageUri == null) {
                            shortToast(this, "추가할 사진이 없습니다.")
                            return@setOnClickListener
                        }
                        photoList.add(imageUri!!)
                        photoAdapter.notifyItemInserted(position)
//                        photoAdapter.notifyDataSetChanged()
                        builder.dismiss()
                    }
                    builder.setView(dialogView)
                    builder.show()
                }
            }
        }

        // 뒤로가기 버튼 클릭
        btn_back.setOnClickListener { finish() }

        // 저장하기 버튼 클릭
        btn_save.setOnClickListener {

            val title = et_title.text.toString().trim()
            val content = et_content.text.toString().trim()
            if (title.isEmpty() && content.isEmpty() && photoList.isEmpty()) {
                shortToast(this, "저장할 내용이 없습니다!")
                return@setOnClickListener
            }

            if (mode == EDIT_MODE) {
                if (originalTitle == title && originalContent == content && originalImageUri == photoList) {
                    shortToast(this, "변경된 사항이 없습니다.")
                    return@setOnClickListener
                }
            }

            startSaveProgress()

            val photos = ArrayList<String>()

            lifecycleScope.launch(Dispatchers.IO) {
                for (photoUri in photoList) {
                    val bitmap =
                        if (photoUri.toString().substring(0, 4) != "http") {
                            if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(
                                    this@EditMemoActivity.contentResolver, photoUri
                                )
                            } else {
                                val source = ImageDecoder.createSource(
                                    this@EditMemoActivity.contentResolver, photoUri
                                )
                                ImageDecoder.decodeBitmap(source)
                            }
                        } else {
                            if (!isInternetAvailable(this@EditMemoActivity)) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    longToast(
                                        this@EditMemoActivity,
                                        "첨부된 사진 중에 URL을 통해 받아오는 사진이 있습니다.\n" +
                                                "인터넷 연결 상태를 확인해주세요."
                                    )
                                    stopSaveProgress()
                                }
                                photos.clear()
                                return@launch
                            }
                            BitmapFactory.decodeStream(
                                URL(photoUri.toString()).content as InputStream
                            )
                        }

                    val imgName = UUID.randomUUID().toString()
                    saveBitmapOnCache(bitmap, imgName)
                    photos.add(imgName)
                }

                intent.putExtra(EXTRA_TITLE, title)
                intent.putExtra(EXTRA_CONTENT, content)
                intent.putExtra(EXTRA_PHOTO, photos)
                intent.putExtra(EXTRA_TIMESTAMP, DateTime().millis)

                val id = intent.getIntExtra(EXTRA_ID, -1)
                if (id != -1) {
                    intent.putExtra(EXTRA_ID, id)
                }

                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            shortToast(this, "취소 되었습니다.")
            return
        }
        when (requestCode) {
            FROM_CAMERA -> {
                val photoUriFromCamera = Uri.fromFile(photoFileFromCamera)
                photoList.add(photoUriFromCamera)
                val lastPosition = photoList.size - 1
                photoAdapter.notifyItemInserted(lastPosition)
                rv_photo.smoothScrollToPosition(lastPosition)
//                photoAdapter.notifyDataSetChanged()
                photoFileFromCamera = null
            }
            FROM_ALBUM -> {
                photoList.add(data!!.data!!)
                val lastPosition = photoList.size - 1
                photoAdapter.notifyItemInserted(lastPosition)
                rv_photo.smoothScrollToPosition(lastPosition)
//                photoAdapter.notifyDataSetChanged()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp = DateTime.now().toLocalDateTime().toString("yyyyMMdd_HHmmss")
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                photoFileFromCamera = createImageFile()
                // Continue only if the File was successfully created
                photoFileFromCamera!!.also {
                    val photoURI: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(
                            this,
                            "com.whalez.programmerslineplus.provider", it
                        )
                    } else {
                        Uri.fromFile(it)
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, FROM_CAMERA)
                }
            }
        }

    }

    private fun getPictureFromGallery() {
        Intent(Intent.ACTION_PICK).also { getPictureIntent ->
            getPictureIntent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            ).also {
                startActivityForResult(getPictureIntent, FROM_ALBUM)
            }
        }
    }

    private fun startSaveProgress() {
        progressbar_layout.visibility = View.VISIBLE
        progressbar_layout.bringToFront()
        et_title.isEnabled = false
        et_content.isEnabled = false
        btn_add_photo.isEnabled = false
        btn_back.isEnabled = false
        btn_save.isEnabled = false
    }

    private fun stopSaveProgress() {
        progressbar_layout.visibility = View.GONE
        et_title.isEnabled = true
        et_content.isEnabled = true
        btn_add_photo.isEnabled = true
        btn_back.isEnabled = true
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
                Log.d(TAG, "저장소 접근 권한 주어짐")
                permissionChecked = true
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.d(TAG, "저장소 접근 권한 거절")
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
