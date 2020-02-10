package com.whalez.programmerslineplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.whalez.programmerslineplus.databinding.ActivityAddMemoBinding
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

        val binding = DataBindingUtil.setContentView<ActivityAddMemoBinding>(this, R.layout.activity_add_memo)
        binding.lifecycleOwner = this

        val viewModel = ViewModelProviders.of(this)[MemoViewModel::class.java]
        binding.viewModel = viewModel

        btn_save.setOnClickListener {
            finish()
        }
    }





}
