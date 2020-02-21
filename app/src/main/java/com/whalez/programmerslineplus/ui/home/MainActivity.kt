package com.whalez.programmerslineplus.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.skydoves.powermenu.kotlin.powerMenu
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.room.data.Memo
import com.whalez.programmerslineplus.ui.detail.DetailMemoActivity
import com.whalez.programmerslineplus.ui.edit.EditMemoActivity
import com.whalez.programmerslineplus.ui.home.menu.MenuFactory
import com.whalez.programmerslineplus.ui.home.menu.MenuFactory.Companion.APP_INFO
import com.whalez.programmerslineplus.ui.home.menu.MenuFactory.Companion.DELETE_ALL
import com.whalez.programmerslineplus.utils.ConstValues.Companion.ADD_MEMO_REQUEST
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EDIT_MEMO_REQUEST
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_CONTENT
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_ID
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_PHOTO
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TIMESTAMP
import com.whalez.programmerslineplus.utils.ConstValues.Companion.EXTRA_TITLE
import com.whalez.programmerslineplus.utils.isDoubleClicked
import com.whalez.programmerslineplus.utils.shortToast
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    lateinit var memoViewModel: MemoViewModel

    private val mainMenu by powerMenu(MenuFactory::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val memoAdapter = MemoAdapter(applicationContext)
        // RecyclerView 초기화
        rv_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = memoAdapter
            setHasFixedSize(true)
        }

        memoViewModel = ViewModelProvider(this)[MemoViewModel::class.java]
        memoViewModel.getAll().observe(this,
            Observer<List<Memo>> { memos -> memoAdapter.submitList(memos) })

        // 메뉴 버튼 클릭
        btn_menu.setOnClickListener { mainMenu.showAsDropDown(it) }
        mainMenu.setOnMenuItemClickListener { position, item ->
            when (position) {
                DELETE_ALL -> {
                    val builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            this@MainActivity,
                            R.style.MyAlertDialogStyle
                        )
                    )
                    builder.setMessage("모든 메모를 삭제하시겠습니까?")
                        .setPositiveButton("예") { _, _ ->
                            memoViewModel.deleteAll()
                            shortToast(this, "모든 메모가 삭제되었습니다.")
                        }
                        .setNegativeButton("아니요") { _, _ -> }
                        .show()
                }
                APP_INFO -> {
                    shortToast(this, item.title)
                }

            }
        }

        // 슬라이드를 통한 아이템 삭제
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val builder = AlertDialog.Builder(
                    ContextThemeWrapper(
                        this@MainActivity,
                        R.style.MyAlertDialogStyle
                    )
                )
                builder.setMessage("정말 삭제하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("예") { _, _ ->
                        memoViewModel.delete(memoAdapter.getMemoAt(viewHolder.adapterPosition))
                        shortToast(this@MainActivity, "삭제되었습니다.")
                    }
                    .setNegativeButton("아니요") { _, _ ->
                        memoAdapter.notifyDataSetChanged()
                    }
                    .show()
            }
        }).attachToRecyclerView(rv_main)

        // 아이템 클릭
        memoAdapter.setOnItemClickListener(object :
            MemoAdapter.OnItemClickListener {
            override fun onItemClick(memo: Memo) {
                if(isDoubleClicked()) return
                val intent = Intent(this@MainActivity, DetailMemoActivity::class.java)
                intent.putExtra(EXTRA_ID, memo.id)
                intent.putExtra(EXTRA_TITLE, memo.title)
                intent.putExtra(EXTRA_CONTENT, memo.content)
                intent.putExtra(EXTRA_PHOTO, memo.photos)
                intent.putExtra(EXTRA_TIMESTAMP, memo.timestamp)
                startActivityForResult(intent, EDIT_MEMO_REQUEST)
            }
        })

        // 메모 추가 버튼 클릭
        btn_add.setOnClickListener {
            val intent = Intent(this, EditMemoActivity::class.java)
            startActivityForResult(intent, ADD_MEMO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_MEMO_REQUEST && resultCode == RESULT_OK) {
            val title = data!!.getStringExtra(EXTRA_TITLE)!!
            val content = data.getStringExtra(EXTRA_CONTENT)!!
            val photos = data.getStringArrayListExtra(EXTRA_PHOTO)!!
            val timestamp = data.getLongExtra(EXTRA_TIMESTAMP, -1)

            val memo =
                Memo(
                    title,
                    content,
                    photos,
                    timestamp
                )

            memoViewModel.insert(memo)
            shortToast(this, "메모 저장완료")

        } else if (requestCode == EDIT_MEMO_REQUEST && resultCode == RESULT_OK) {
            val id = data!!.getIntExtra(EXTRA_ID, -1)
            if (id == -1) {
                shortToast(this, "메모가 수정되지 않았습니다!")
                return
            }

            val title = data.getStringExtra(EXTRA_TITLE)!!
            val content = data.getStringExtra(EXTRA_CONTENT)!!
            val photos = data.getStringArrayListExtra(EXTRA_PHOTO)!!
            val timestamp = DateTime().millis
            val memo = Memo(title, content, photos, timestamp)
            memo.id = id

            memoViewModel.update(memo)
            shortToast(this, "메모가 수정되었습니다.")
        } else {
            shortToast(this, "새 메모가 저장되지 않았습니다!")
        }
    }
}
