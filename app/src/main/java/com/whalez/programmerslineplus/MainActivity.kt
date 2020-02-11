package com.whalez.programmerslineplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.programmerslineplus.adapters.MemoAdapter
import com.whalez.programmerslineplus.data.Memo
import com.whalez.programmerslineplus.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_NOTE_REQUEST = 1
    }

    private lateinit var memoViewModel: MemoViewModel
    private val mainMenu by powerMenu(MenuFactory::class)

    private val DELETE_ALL = 0
    private val APP_INFO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_main.layoutManager = LinearLayoutManager(this)
        rv_main.setHasFixedSize(true)

        val memoAdapter = MemoAdapter()
        rv_main.adapter = memoAdapter

        memoViewModel = ViewModelProviders.of(this)[MemoViewModel::class.java]
        memoViewModel.getAll().observe(this,
            Observer<List<Memo>> { memos -> memoAdapter.setMemos(memos) })

        // 메모 추가
        btn_add.setOnClickListener {
            val intent = Intent(this, AddMemoActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        btn_menu.setOnClickListener { mainMenu.showAsDropDown(it) }
        mainMenu.setOnMenuItemClickListener { position, item ->
            when (position) {
                DELETE_ALL -> {
                    // 쿼리문 비동기 처리
                    lifecycleScope.launch(Dispatchers.IO) {
                        memoViewModel.deleteAll()
                    }
                    Toast.makeText(this, "모두 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                APP_INFO -> {
                    Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
                }

            }
        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder = AlertDialog.Builder(
                    ContextThemeWrapper(
                        this@MainActivity,
                        R.style.MyAlertDialogStyle
                    )
                )
                builder.setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("예") { _, _ ->
                        memoViewModel.delete(memoAdapter.getMemoAt(viewHolder.adapterPosition))
                        Toast.makeText(this@MainActivity, "삭제 완료", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("아니요") {_, _ ->
                        memoAdapter.notifyDataSetChanged()
                    }
                    .show()
            }
        }).attachToRecyclerView(rv_main)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data!!)

        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            val title = data.getStringExtra(AddMemoActivity.EXTRA_TITLE)
            val content = data.getStringExtra(AddMemoActivity.EXTRA_CONTENT)

            // 쿼리문 비동기 처리
            lifecycleScope.launch(Dispatchers.IO) {
                memoViewModel.insert(title!!, content!!)
            }

            Toast.makeText(this, "메모 저장완료", Toast.LENGTH_SHORT).show()
        }
    }

}
