package net.sharetrip.shared.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import net.sharetrip.shared.R

class LockableViewPager : ViewPager {
    private var swipeable = false

    constructor(context: Context?) : super(context!!) {}

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        val mTheme = getContext().theme
        val mTypedArray = mTheme.obtainStyledAttributes(attrs, R.styleable.LockableViewPager,
                0, 0)
        swipeable = mTypedArray.getBoolean(R.styleable.LockableViewPager_lvp_swipeable, false)
    }

    override fun onTouchEvent(mMotionEvent: MotionEvent): Boolean {
        return swipeable && super.onTouchEvent(mMotionEvent)
    }

    override fun onInterceptTouchEvent(mMotionEvent: MotionEvent): Boolean {
        return swipeable && super.onInterceptTouchEvent(mMotionEvent)
    }

    fun setSwipeable(swipeable: Boolean) {
        this.swipeable = swipeable
    }
}