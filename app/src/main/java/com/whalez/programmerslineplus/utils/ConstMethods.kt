package com.whalez.programmerslineplus.utils

import android.content.Context
import android.os.SystemClock
import android.widget.Toast

var mLastClickTime: Long = 0

fun isDoubleClicked(): Boolean {
    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        return true
    }
    mLastClickTime = SystemClock.elapsedRealtime()
    return false
}

fun showToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}