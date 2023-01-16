package net.sharetrip.shared.utils

import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun String?.getStrikePriceStringWithCurrency(price: Int = 0): SpannableString {
    val priceString = NumberFormat.getNumberInstance(Locale.US).format(price)
    val string = SpannableString("$this $priceString")
    string.setSpan(StrikethroughSpan(), 0, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return string
}

fun String?.isNameValid(): Boolean {
    try {
        if (this == null || this.isEmpty()) {
            return false
        }
        return RegexValidation.validRegex(this, NAME_REGEX)
    } catch (e: Exception) {
        return false
    }
}

fun String?.isGivenNameValid(): Boolean {
    return try {
        if (this == null || this.isEmpty()) {
            true
        } else RegexValidation.validRegex(this, NAME_REGEX)
    } catch (e: Exception) {
        false
    }
}

fun String?.isPassportNumberValid(): Boolean {
    if (this == null || this.isEmpty())
        return false
    return RegexValidation.validRegex(this, PASSPORT_REGEX)
}

fun String?.isPhoneNumberValid(): Boolean {
    return !isNullOrEmpty() && this.matches(PHONE_VALIDATION_REGEX.toRegex()) && this.length >= 11
}

fun String?.isEmailValid(): Boolean {
    return !isNullOrEmpty() && this.matches(EMAIL_REGEX.toRegex())
}

fun String.isPasswordValid(minChar: Int): Boolean {
    return if (this.isNotEmpty()) this.length >= minChar else false
}

fun String.isPasswordMatchesInstruction(): Boolean {
    return (this.isNotEmpty()) && this.matches(PASSWORD_REGEX.toRegex())
}


fun String?.getUserTitleForFlight(dateOfBirth: String, flightDate: String): String {
    return if (DateUtil.getAgeForFlight(dateOfBirth, flightDate) <= 11 && this.equals("Male")) {
        "MSTR"
    } else if (DateUtil.getAgeForFlight(dateOfBirth, flightDate) > 11 && this.equals("Male")) {
        "MR"
    } else if (DateUtil.getAgeForFlight(dateOfBirth, flightDate) <= 11 && this.equals("Female")) {
        "MISS"
    } else if (DateUtil.getAgeForFlight(dateOfBirth, flightDate) > 11 && this.equals("Female")) {
        "MS"
    } else {
        ""
    }
}

fun String?.getUserTitle(dateOfBirth: String): String {
    return if (DateUtil.getAge(dateOfBirth) <= 11 && this.equals("Male")) {
        "Master"
    } else if (DateUtil.getAge(dateOfBirth) > 11 && this.equals("Male")) {
        "Mr"
    } else if (DateUtil.getAge(dateOfBirth) <= 11 && this.equals("Female")) {
        "Miss"
    } else if (DateUtil.getAge(dateOfBirth) > 11 && this.equals("Female")) {
        "Ms"
    } else {
        ""
    }
}

fun Int?.formatValueToUS(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun String?.isCancelDateExpire(): Boolean {
    this?.let {
        val dateString = it.split("T")[0]
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, dateString.split("-")[1].toInt())
        cal.set(Calendar.DATE, dateString.split("-")[2].toInt())
        cal.set(Calendar.YEAR, dateString.split("-")[0].toInt())
        val dateOne = cal.time
        val currentDate = Date()
        return currentDate.after(dateOne)
    }
    return false
}

@BindingAdapter("strikeThrough")
fun strikeThrough(textView: TextView, strikeThrough: Boolean) {
    if (strikeThrough) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("formattedDate")
fun TextView.formatDateString(text: String?) {
    text?.let {
        this.text = "- Last Update: " + DateUtil.parseDisplayDateFormatFromLongDateString(text)
    }
}

fun String.parseTextAfterColon(): String {
    return this.split(":")[1]
}

fun fromHtml(string: String): Spanned? {
    return if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT) // for 24 api and more
    } else {
        Html.fromHtml(string)
    }
}

fun formatToTwoDigit(input: Any): String {
    return String.format("%02d", input)
}

fun Double.twoDigitDecimal(): Double {
    val df = DecimalFormat("#.##")
    return try {
        df.format(this).toDouble()
    } catch (e: Exception) {
        this
    }
}

fun String?.isPassportExpiryDateValid(): Boolean {
    return try {
        !(this == null || this.isEmpty())

    } catch (e: Exception) {
        false
    }
}

fun String?.toIntExt(): Int {
    return if (this.isNullOrEmpty()) {
        0
    } else {
        Integer.parseInt(this)
    }
}
