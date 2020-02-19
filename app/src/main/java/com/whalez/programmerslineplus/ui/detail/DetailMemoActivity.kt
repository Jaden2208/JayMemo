package com.whalez.programmerslineplus.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.ui.edit.EditMemoActivity
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EDIT_MEMO_REQUEST
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_PHOTO
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TIMESTAMP
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import kotlinx.android.synthetic.main.activity_detail_memo.*
import kotlinx.android.synthetic.main.activity_detail_memo.tv_content
import kotlinx.android.synthetic.main.activity_detail_memo.tv_title
import org.joda.time.DateTime
import java.io.File
import kotlin.collections.ArrayList

class DetailMemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_memo)

        val title = intent.getStringExtra(EXTRA_TITLE)
        val content = intent.getStringExtra(EXTRA_CONTENT)
        val imgNames = intent.getStringArrayListExtra(EXTRA_PHOTO)!!
        val timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, -1)

        tv_title.text = title
        tv_content.text = content
        tv_detail_timestamp.text = DateTime(timestamp).toString("yyyy년 MM월 dd일 HH:mm:ss")
        if(imgNames.isEmpty()) {
            imageSlider.visibility = View.GONE
            val params = line2.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.line1)
        }

        val photoList = ArrayList<Uri>()
        for(imgName in imgNames) {
            val imgUri = Uri.fromFile(File("${File(cacheDir.toString())}/${imgName}.jpg"))
            photoList.add(imgUri)
        }

        // imgSlider 초기화
        val imgSliderAdapter = ImageSliderAdapter()
        imgSliderAdapter.renewItems(photoList)
        imageSlider.sliderAdapter = imgSliderAdapter
        imageSlider.setIndicatorAnimation(IndicatorAnimations.SLIDE)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)

        // 수정 버튼 클릭
        btn_edit.setOnClickListener {
            val intent = Intent(this@DetailMemoActivity, EditMemoActivity::class.java)
            intent.putExtra(EXTRA_ID, intent.getIntExtra(EXTRA_ID, -1))
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_CONTENT, content)
            intent.putExtra(EXTRA_PHOTO, imgNames)
            startActivityForResult(intent, EDIT_MEMO_REQUEST)
        }

        // 뒤로가기 버튼 클릭
        btn_back.setOnClickListener { finish() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_MEMO_REQUEST && resultCode == RESULT_OK){
            val title = data!!.getStringExtra(EXTRA_TITLE)!!
            val content = data.getStringExtra(EXTRA_CONTENT)!!
            val photos = data.getStringArrayListExtra(EXTRA_PHOTO)!!
            val timestamp = data.getLongExtra(EXTRA_TIMESTAMP, -1)

            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_CONTENT, content)
            intent.putExtra(EXTRA_PHOTO, photos)
            intent.putExtra(EXTRA_TIMESTAMP, timestamp)
            val id = intent.getIntExtra(EXTRA_ID, -1)
            if (id != -1) {
                intent.putExtra(EXTRA_ID, id)
            }
            setResult(RESULT_OK, intent)

            finish()
        }
    }
}
