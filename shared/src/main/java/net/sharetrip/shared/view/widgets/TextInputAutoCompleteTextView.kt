package net.sharetrip.shared.view.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class TextInputAutoCompleteTextView : AppCompatAutoCompleteTextView {
    private var isPopup = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection? {
        val mInputConnection = super.onCreateInputConnection(outAttrs)

        if (mInputConnection != null && outAttrs!!.hintText == null) {
            var mParent = parent
            while (mParent is View) {
                if (mParent is TextInputLayout) {
                    outAttrs.hintText = mParent.hint
                    break
                }
                mParent = mParent.getParent()
            }
        }

        return mInputConnection
    }

    override fun enoughToFilter(): Boolean {
        return true
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (focused) {
            if (filter != null) {
                performFiltering("", 0)
            }

            val mInputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            keyListener = null

            if (!isPopup) {
                showDropDown()
            } else {
                dismissDropDown()
            }
        } else {
            isPopup = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (isPopup) {
                    dismissDropDown()
                } else {
                    requestFocus()
                    showDropDown()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun showDropDown() {
        super.showDropDown()
        isPopup = true
    }

    override fun dismissDropDown() {
        super.dismissDropDown()
        isPopup = false
    }
}
