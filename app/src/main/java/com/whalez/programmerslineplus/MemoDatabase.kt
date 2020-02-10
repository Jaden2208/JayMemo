package com.whalez.programmerslineplus

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Memo::class], version = 1)
abstract class MemoDatabase: RoomDatabase() {
    abstract fun memeoDao(): MemoDao
}