package com.whalez.programmerslineplus

import android.app.Application
import androidx.lifecycle.LiveData
import com.whalez.programmerslineplus.data.Memo

//class MemoRepository(application: Application) {
//    private var allMemos: LiveData<List<Memo>>
//    init {
//        val database = MemoDatabase.getInstance(application)
//        val memoDao = database!!.memoDao()
//        allMemos = memoDao!!.getAllMemos()
//    }
//
//    fun insert(memo: Memo){
//
//    }
//
//    fun update(memo: Memo){
//
//    }
//
//    fun delete(memo: Memo){
//
//    }
//
//    fun getAllMemos(): LiveData<List<Memo>> {
//        return allMemos
//    }
//
//    fun deleteAllMemos() {
//
//    }
//
//
//}