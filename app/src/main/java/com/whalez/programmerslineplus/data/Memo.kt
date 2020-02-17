package com.whalez.programmerslineplus.data

import androidx.room.*

@Entity
data class Memo(
    var title: String,
    var content: String,
    var photos: ArrayList<String>
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}