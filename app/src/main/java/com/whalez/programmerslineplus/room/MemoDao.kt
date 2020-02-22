package com.whalez.programmerslineplus.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.whalez.programmerslineplus.room.data.Memo

@Dao
interface MemoDao {

    @Insert
    suspend fun insert(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Query("select * from Memo order by timestamp desc")
    fun getAllMemos(): LiveData<List<Memo>>

    @Query("delete from Memo")
    fun deleteAllMemos()

    @Query("delete from Memo where id in (:idList)")
    fun deleteSelectedMemos(idList: List<Int>)
}