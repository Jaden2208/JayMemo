package com.whalez.programmerslineplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.whalez.programmerslineplus.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_add_memo.*

class AddMemoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_CONTENT = "EXTRA_CONTENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memo)

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
        setResult(RESULT_OK, data)
        finish()

    }


}
