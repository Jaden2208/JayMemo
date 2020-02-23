package com.whalez.programmerslineplus.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.whalez.programmerslineplus.R
import com.whalez.programmerslineplus.utils.ConstValues.Companion.TAG


var mLastClickTime: Long = 0

fun isDoubleClicked(): Boolean {
    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        Log.d(TAG, "double clicked")
        return true
    }
    mLastClickTime = SystemClock.elapsedRealtime()
    return false
}

fun simpleBuilder(context: Context): AlertDialog.Builder {
    return AlertDialog.Builder(
        ContextThemeWrapper(
            context,
            R.style.MyAlertDialogStyle
        )
    )
}

fun shortToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun longToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}