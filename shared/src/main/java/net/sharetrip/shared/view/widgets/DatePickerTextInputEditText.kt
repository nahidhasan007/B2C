package net.sharetrip.shared.view.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import net.sharetrip.shared.utils.DateUtil.getYearfromDate
import net.sharetrip.shared.utils.DateUtil.getdayfromDate
import net.sharetrip.shared.utils.DateUtil.getmonthfromDate
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import net.sharetrip.shared.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class DatePickerTextInputEditText : TextInputEditText, DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener {
    private var isPopup = false
    private var mDatePickerDialog: SpinnerDatePickerDialogBuilder? = null
    private var invalidDate = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val mCalendar = Calendar.getInstance(TimeZone.getDefault())
        mDatePickerDialog = SpinnerDatePickerDialogBuilder()
                .context(getContext())
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], mCalendar[Calendar.DAY_OF_MONTH])
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            val mInputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mInputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            keyListener = null
            if (!isPopup) {
                 //showDropDown();
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

    fun showDropDown() {
        if (!invalidDate) {
            mDatePickerDialog!!.build().show()
            isPopup = false
        } else {
            Toast.makeText(context, "Booking date has been expire", Toast.LENGTH_SHORT).show()
        }
    }

    fun dismissDropDown() {
        mDatePickerDialog!!.build().dismiss()
        isPopup = false
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val mFormat: NumberFormat = DecimalFormat("00")
        val mMonthFormat = mFormat.format(month + 1.toLong())
        val mDateFormat = mFormat.format(dayOfMonth.toLong())
        val mDate = "$mDateFormat-$mMonthFormat-$year"
        setText(mDate)
    }

    fun setRange(startDate: String?, endDate: String?) {
        try {
            mDatePickerDialog!!.minDate(getYearfromDate(startDate), getmonthfromDate(startDate), getdayfromDate(startDate))
            mDatePickerDialog!!.maxDate(getYearfromDate(endDate), getmonthfromDate(endDate), getdayfromDate(endDate))
        } catch (e: ParseException) {
        }
    }

    fun setBirthdayRange(endDate: String?) {
        try {
            mDatePickerDialog!!.maxDate(getYearfromDate(endDate), getmonthfromDate(endDate), getdayfromDate(endDate))
        } catch (e: ParseException) {
        }
    }

    fun setPassportRange(startDate: String?) {
        try {
            mDatePickerDialog!!.minDate(getYearfromDate(startDate), getmonthfromDate(startDate), getdayfromDate(startDate))
        } catch (e: ParseException) {
        }
    }

    fun setExistingDate(birthdate: String?) {
        try {
            if (birthdate == null || birthdate.length <= 2) {
                val mCalendar = Calendar.getInstance(TimeZone.getDefault())
                mDatePickerDialog!!.defaultDate(mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], mCalendar[Calendar.DAY_OF_MONTH])
            } else mDatePickerDialog!!.defaultDate(getYearfromDate(birthdate), getmonthfromDate(birthdate), getdayfromDate(birthdate))
        } catch (e: ParseException) {
        }
    }

    fun dialogClose() {
        invalidDate = true
    }

    override fun onCancel(dialogInterface: DialogInterface) {}
}
