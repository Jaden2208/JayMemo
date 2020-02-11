package com.whalez.programmerslineplus

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import androidx.room.Room
import com.whalez.programmerslineplus.data.Memo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel(application: Application): AndroidViewModel(application) {
    // 앱 데이터베이스 생성
    private val db = Room.databaseBuilder(
        application,
        MemoDatabase::class.java, "memo-db"
    ).build()

//    var allMemos: LiveData<List<Memo>>

    var newTitle: String? = null
    var newContent: String? = null

//    init {
//        allMemos = getAll()
//    }

    fun insert(memo: Memo) {
        Log.d("kkk", "insert: ${memo.title}, ${memo.content}")
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().insert(memo)
        }
    }

    fun update(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().update(memo)
        }
    }

    fun delete(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().delete(memo)
        }
    }

    fun getAll(): LiveData<List<Memo>> {
        return db.memoDao().getAllMemos()
    }

    fun deleteAll() {
        return db.memoDao().deleteAllMemos()
    }


}