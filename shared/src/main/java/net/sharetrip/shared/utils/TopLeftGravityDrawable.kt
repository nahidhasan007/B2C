package net.sharetrip.shared.utils

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class TopLeftGravityDrawable(private val mDrawable: Drawable) : BitmapDrawable() {
    override fun getIntrinsicWidth(): Int {
        return mDrawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mDrawable.intrinsicHeight
    }

    override fun draw(canvas: Canvas) {
        val halfCanvas = canvas.height / 2
        val halfDrawable = mDrawable.intrinsicHeight / 2

        // align to top
        canvas.save()
        canvas.translate(0f, -halfCanvas + halfDrawable.toFloat())
        mDrawable.draw(canvas)
        canvas.restore()
    }
}