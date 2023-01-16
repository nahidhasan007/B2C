package net.sharetrip.shared.view.widgets

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TimePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.NumberFormat

class TimePickerTextInputEditText : TextInputEditText, OnTimeSetListener, DialogInterface.OnDismissListener {
    private var isPopup = false
    private var mTimePickerDialog: TimePickerDialog? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mTimePickerDialog = TimePickerDialog(context, this,
                0, 0, false)
        mTimePickerDialog!!.setOnDismissListener(this)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
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

    private fun showDropDown() {
        mTimePickerDialog!!.show()
        isPopup = true
    }

    private fun dismissDropDown() {
        mTimePickerDialog!!.dismiss()
        isPopup = false
    }

    override fun onDismiss(dialog: DialogInterface) {
        isPopup = false
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val mFormat: NumberFormat = DecimalFormat("00")
        val format: String
        val hour: Int
        if (hourOfDay > 12) {
            hour = hourOfDay - 12
            format = "PM"
        } else {
            hour = hourOfDay
            format = "AM"
        }
        val mHour = mFormat.format(hour.toLong())
        val mMinute = mFormat.format(minute.toLong())
        val mTime = "$mHour:$mMinute $format"
        mTimePickerDialog!!.updateTime(hourOfDay, minute)
        setText(mTime)
    }
}