package net.sharetrip.shared.view

import android.app.Dialog
import android.content.Context
import android.view.Window

class ViewDialog(context: Context) : Dialog(context) {

    init {
        //this.window?.decorView?.setBackgroundResource(android.R.color.transparent)
        this.window?.setDimAmount(0.0f)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //setContentView(R.layout.custom_loading_layout)
        setCancelable(false)
    }
}
