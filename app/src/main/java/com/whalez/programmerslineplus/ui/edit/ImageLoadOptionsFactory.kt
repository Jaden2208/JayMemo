package com.whalez.programmerslineplus.ui.edit

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import androidx.lifecycle.LifecycleOwner
import com.skydoves.powermenu.CircularEffect
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.createPowerMenu
import com.whalez.programmerslineplus.R

class ImageLoadOptionsFactory: PowerMenu.Factory() {

    companion object { // 메뉴 옵션
        const val FROM_CAMERA = 0
        const val FROM_ALBUM = 1
        const val FROM_URL = 2
    }

    override fun create(context: Context, lifecycle: LifecycleOwner): PowerMenu {
        return createPowerMenu(context) {
            addItem(PowerMenuItem("카메라", false))
            addItem(PowerMenuItem("앨범", false))
            addItem(PowerMenuItem("이미지 링크", false))
            /* PowerMenu methods link
             * https://github.com/skydoves/PowerMenu#powermenu-methods
             */
            setAutoDismiss(true)
            setLifecycleOwner(lifecycle)
            setAnimation(MenuAnimation.SHOW_UP_CENTER)
            setCircularEffect(CircularEffect.BODY)
            setMenuRadius(10f)
            setMenuShadow(10f)
            setTextColorResource(R.color.colorBlack)
            setTextSize(12)
            setTextGravity(Gravity.CENTER)
            setTextTypeface(Typeface.create("sans-serif-light", Typeface.BOLD))
            setSelectedTextColor(Color.WHITE)
            setMenuColor(Color.WHITE)
            setSelectedMenuColorResource(R.color.colorPrimary)
            setPreferenceName("ImageLoadOptions")
//            setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
        }
    }
}