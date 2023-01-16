package net.sharetrip.profile.utils

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class MyClickableSpan : ClickableSpan() {

    override fun onClick(view: View) {}

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
        ds.color = -0xe77d01
    }
}
