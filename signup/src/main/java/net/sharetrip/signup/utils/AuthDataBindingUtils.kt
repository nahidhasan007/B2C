package net.sharetrip.signup.utils

import android.util.Patterns
import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("isEmptyText")
fun checkEditText(inputLayout: TextInputLayout, text: String?) {
    if (text != null && text.trim { it <= ' ' }.isEmpty()) {
        inputLayout.isErrorEnabled = true
        inputLayout.error = " "
    } else {
        inputLayout.isErrorEnabled = false
    }
}

@BindingAdapter("showSnackBar")
fun showSnackBar(view: View?, msg: String?) {
    if (msg == null) return
    val snackBar = Snackbar.make(view!!, msg, Snackbar.LENGTH_LONG)
    snackBar.show()
}

@BindingAdapter("isEmailId")
fun checkEmailID(inputLayout: TextInputLayout, emailId: String?) {
    inputLayout.isErrorEnabled = false
    if (emailId == null) return
    if (emailId.trim { it <= ' ' }.isEmpty() || !isValidEmailId(emailId)) {
        inputLayout.isErrorEnabled = true
        return
    }
}

fun isValidEmailId(string: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(string).matches()
}

@BindingAdapter("isEmptyWithFocus")
fun checkEditTextWithCheckFocus(inputLayout: TextInputLayout, text: String?) {
    if (inputLayout.hasFocus() && text != null && text.trim { it <= ' ' }.isEmpty()) {
        inputLayout.isErrorEnabled = true
        inputLayout.error = " "
    } else {
        inputLayout.isErrorEnabled = false
    }
}