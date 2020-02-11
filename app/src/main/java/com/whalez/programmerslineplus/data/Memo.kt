package com.whalez.programmerslineplus.data

import android.view.View
import android.widget.Toast
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo(
    var title: String,
    var content: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}