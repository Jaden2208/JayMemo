package com.whalez.programmerslineplus.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.whalez.programmerslineplus.room.data.Memo
import com.whalez.programmerslineplus.room.MemoDatabase
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel(application: Application): AndroidViewModel(application) {
    // 앱 데이터베이스 생성
    private val db = Room.databaseBuilder(
        application,
        MemoDatabase::class.java, "memo-db"
    ).build()

    fun insert(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().insert(memo)
            Log.d(TAG, "add:"+ memo.id)
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
        viewModelScope.launch(Dispatchers.IO) {
            db.memoDao().deleteAllMemos()
        }
    }


}