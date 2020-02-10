package com.whalez.programmerslineplus

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.whalez.programmerslineplus.data.Memo

@Database(entities = [Memo::class], version = 1)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

//    companion object {
//        private var instance: MemoDatabase? = null
//        @Synchronized
//        fun getInstance(context: Context): MemoDatabase? {
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MemoDatabase::class.java, "memo_database"
//                )
//                    .fallbackToDestructiveMigration()
//                    .addCallback(roomCallback)
//                    .build()
//            }
//            return instance
//        }
//
//        private val roomCallback = object: RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
////                new xxxAsyncTask(instance).execute();
//            }
//        }
//    }

}