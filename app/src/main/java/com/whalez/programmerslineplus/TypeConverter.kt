package com.whalez.programmerslineplus

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
    }

}