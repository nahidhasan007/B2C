package net.sharetrip.shared.utils

import android.util.Patterns

object Strings {
    const val LINE_BREAK = "\n"
    const val SPACE = " "
    @JvmStatic
    fun isBlank(string: CharSequence?): Boolean {
        return string == null || string.toString().trim { it <= ' ' }.isEmpty()
    }

    @JvmStatic
    fun isNull(string: CharSequence?): Boolean {
        return string == null || string.toString().trim { it <= ' ' }.isEmpty() || string == "null"
    }

    fun isProperEmail(string: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(string).matches()
    }
}