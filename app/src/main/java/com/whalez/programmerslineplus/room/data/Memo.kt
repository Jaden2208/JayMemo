package com.whalez.programmerslineplus.room.data

import androidx.room.*

@Entity
data class Memo(
    var title: String,
    var content: String,
    var photos: ArrayList<String>,
    var timestamp: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}