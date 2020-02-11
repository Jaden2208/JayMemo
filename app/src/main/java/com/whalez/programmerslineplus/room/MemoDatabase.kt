package com.whalez.programmerslineplus.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.whalez.programmerslineplus.data.Memo

@Database(entities = [Memo::class], version = 1)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}