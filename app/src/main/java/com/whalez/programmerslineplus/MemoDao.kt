package com.whalez.programmerslineplus

import androidx.lifecycle.LiveData
import androidx.room.*
import com.whalez.programmerslineplus.data.Memo

@Dao
interface MemoDao {

    @Insert
    suspend fun insert(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Query("select * from Memo")
    fun getAllMemos(): LiveData<List<Memo>>

    @Query("delete from Memo")
    fun deleteAllMemos()
}