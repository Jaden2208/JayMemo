package com.whalez.programmerslineplus.ui.edit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.utils.ConstValues.Companion.ADD_MODE
import com.whalez.programmerslineplus.utils.ConstValues.Companion.VIEW_MODE
import kotlinx.android.synthetic.main.activity_edit_memo.*

class EditMemoActivity : AppCompatActivity() {

    private var mode = ADD_MODE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_memo)

        val intent = intent
        // 아이템을 눌러서 들어온 경우 보기 모드로 변경
        if(intent.hasExtra(EXTRA_ID)){
            mode = VIEW_MODE
            setViewMode(intent)
        }

        // 보기 모드에서 수정 버튼 클릭
        if (mode == VIEW_MODE) {
            btn_edit.setOnClickListener { setEditMode(intent) }
        }

        // 뒤로가기 버튼 클릭
        btn_back.setOnClickListener { finish() }
    }

    fun saveMemo(view: View) {
        val title = et_title.text.toString().trim()
        val content = et_content.text.toString().trim()
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_CONTENT, content)

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }

        setResult(RESULT_OK, data)
        finish()
    }

    private fun setViewMode(intent: Intent) {
        tv_bar_title.visibility = View.GONE
        btn_edit.visibility = View.VISIBLE
        et_title.setText(intent.getStringExtra(EXTRA_TITLE))
        et_title.isEnabled = false
        et_content.setText(intent.getStringExtra(EXTRA_CONTENT))
        et_content.isEnabled = false
        btn_save.visibility = View.GONE
    }

    private fun setEditMode(intent: Intent) {
        btn_edit.visibility = View.GONE
        et_title.isEnabled = true
        et_content.isEnabled = true
        btn_save.visibility = View.VISIBLE
    }


}
