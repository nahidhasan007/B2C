package net.sharetrip.flight.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatButton
import net.sharetrip.flight.R

class SelectionButton : AppCompatButton, Checkable {
    private var mChecked = false

    constructor(context: Context) : super(context) {
        initUi()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initUi()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        initUi()
    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked
        updateDrawable()
    }

    fun checked(checked: Boolean) {
        mChecked = checked
        updateDrawable()
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    private fun initUi() {
        isChecked = false
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    private fun updateDrawable() {
        if (mChecked) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_indigo_24dp, 0)
        } else {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}