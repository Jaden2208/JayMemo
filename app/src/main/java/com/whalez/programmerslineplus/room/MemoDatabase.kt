package com.whalez.programmerslineplus.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.whalez.programmerslineplus.room.data.Memo

@Database(entities = [Memo::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}