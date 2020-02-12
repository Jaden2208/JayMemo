package com.whalez.programmerslineplus.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
    var title: String,
    var content: String,
    @ColumnInfo(defaultValue = "none")
    var thumbnailName: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}