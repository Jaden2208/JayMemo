package com.whalez.programmerslineplus.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList


class TypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromString(value: String?): ArrayList<String> {
            val listType =
                object : TypeToken<ArrayList<String?>?>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromArrayList(list: ArrayList<String?>?): String {
            val gson = Gson()
            return gson.toJson(list)
        }

//        @TypeConverter
//        @JvmStatic
//        fun fromTimestamp(value: Long): Date {
//            return Date(value)
//        }
//
//        @TypeConverter
//        @JvmStatic
//        fun dateToTimestamp(date: Date): Long {
//            return date.time
//        }
    }

}