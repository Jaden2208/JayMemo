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

    var allMemos: LiveData<List<Memo>>

    var newTitle: String? = null
    var newContent: String? = null

    init {
        allMemos = getAll()
    }

    fun insert(title: String, content: String) {
        Log.d("kkk", "insert: ${title}, ${content}")
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().insert(
                Memo(
                    title,
                    content
                )
            )
        }
    }

    fun update(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().update(
                Memo(
                    title,
                    content
                )
            )
        }
    }

    fun delete(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().delete(
                Memo(
                    title,
                    content
                )
            )
        }
    }

    fun getAll(): LiveData<List<Memo>> {
        return db.memoDao().getAllMemos()
    }

    fun deleteAll() {
        return db.memoDao().deleteAllMemos()
    }


}