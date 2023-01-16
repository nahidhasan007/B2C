package net.sharetrip.bus.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import net.sharetrip.bus.R

fun getAlertDialog(layoutInflater: LayoutInflater, context: Context): AlertDialog {
    val loader = layoutInflater.inflate(R.layout.layout_please_wait, null, false)
    loader.layout(0, 0, 0, 0)
    val builder = AlertDialog.Builder(context)
    builder.setTitle("")
        .setView(loader)
        .setCancelable(true)
    return builder.create()
}
