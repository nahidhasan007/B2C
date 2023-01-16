package net.sharetrip.view.home

import android.content.Context
import android.view.View

interface TreasureBoxListener {

    fun showTimer()

    fun showWinCoin(context: Context, view: View)
}