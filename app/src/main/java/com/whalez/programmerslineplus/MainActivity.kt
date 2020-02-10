package com.whalez.programmerslineplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whalez.programmerslineplus.adapters.MemoAdapter
import com.whalez.programmerslineplus.data.Memo
import com.whalez.programmerslineplus.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_NOTE_REQUEST = 1
    }

    private lateinit var memoViewModel: MemoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        // get the view model
//        memoViewModel = ViewModelProviders.of(this)[MemoViewModel::class.java]
//
//        // Specify layout for recycler view
//        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        rv_main.layoutManager = linearLayoutManager
//
//        // Observe the model
//        memoViewModel.allMemos.observe(this, Observer {
//            // Data bind the recycler view
//            rv_main.adapter = MemoAdapter()
//        })

        rv_main.layoutManager = LinearLayoutManager(this)
        rv_main.setHasFixedSize(true)

        val memoAdapter = MemoAdapter()
        rv_main.adapter = memoAdapter

        memoViewModel = ViewModelProviders.of(this).get(MemoViewModel::class.java)
        memoViewModel.getAll().observe(this, object: Observer<List<Memo>> {
            override fun onChanged(memos: List<Memo>) {
                memoAdapter.setMemos(memos)
            }

        })

        btn_add.setOnClickListener {
            val intent = Intent(this, AddMemoActivity::class.java)
            startActivity(intent)
//            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d("kkk", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("kkk", "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("kkk", "onRestart")
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data!!)
//
//        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
//            onRestart()
////            val title: String = data.getStringExtra(AddMemoActivity.EXTRA_TITLE)
////            val content: String = data.getStringExtra(AddMemoActivity.EXTRA_CONTENT)
////
////            val memo = Memo(title, content)
////            val memoViewModel = ViewModelProviders.of(this)[MemoViewModel::class.java]
////            binding.
////            memoViewModel.insert(memo)
////
////            Toast.makeText(this, "메모 저장완료", Toast.LENGTH_SHORT).show()
//        }
//    }

}
