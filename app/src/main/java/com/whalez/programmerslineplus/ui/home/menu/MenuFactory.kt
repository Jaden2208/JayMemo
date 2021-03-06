package com.whalez.programmerslineplus.ui.home.menu

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import androidx.lifecycle.LifecycleOwner
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.createPowerMenu
import com.whalez.programmerslineplus.R

class MenuFactory: PowerMenu.Factory() {

    companion object { // 메뉴 옵션
        const val DELETE_SELECTED = 0
        const val DELETE_ALL = 1
        const val APP_INFO = 2
    }

    override fun create(context: Context, lifecycle: LifecycleOwner): PowerMenu {
        return createPowerMenu(context) {
            addItem(PowerMenuItem("선택 삭제", false))
            addItem(PowerMenuItem("모두 삭제", false))
            addItem(PowerMenuItem("앱 정보", false))
            /* PowerMenu methods link
             * https://github.com/skydoves/PowerMenu#powermenu-methods
             */
            setAutoDismiss(true)
            setLifecycleOwner(lifecycle)
            setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            setMenuRadius(10f)
            setMenuShadow(10f)
            setTextColorResource(R.color.colorBlack)
            setTextSize(15)
            setTextGravity(Gravity.CENTER)
            setTextTypeface(Typeface.create("sans-serif-light", Typeface.BOLD))
            setSelectedTextColor(Color.WHITE)
            setMenuColor(Color.WHITE)
            setSelectedMenuColorResource(R.color.colorPrimary)
            setPreferenceName("BasicMenu")
        }
    }
}