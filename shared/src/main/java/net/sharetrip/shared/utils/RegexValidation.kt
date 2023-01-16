package net.sharetrip.shared.utils

import java.util.regex.Pattern

object RegexValidation {

    fun validRegex(input : String, regex: String) : Boolean {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(input)
        return matcher.matches()
    }
}
