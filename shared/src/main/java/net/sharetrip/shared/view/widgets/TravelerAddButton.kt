package net.sharetrip.shared.view.widgets

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatButton
import net.sharetrip.shared.R

class TravelerAddButton : AppCompatButton, Checkable {
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

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    private fun initUi() {
        isChecked = false
    }

    private fun updateDrawable() {
        if (mChecked) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_done_indigo_24dp,
                    0,
                    R.drawable.ic_edit_person_gray_700_24dp,
                    0
                )
            } else {
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.done_mono,
                    0,
                    R.drawable.edit_profile_mono,
                    0
                )
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_person_add_gray_700_24dp,
                    0
                )
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.add_person_mono, 0)
            }
        }
    }
}