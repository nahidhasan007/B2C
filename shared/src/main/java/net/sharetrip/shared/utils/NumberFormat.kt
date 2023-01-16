package net.sharetrip.shared.utils

import java.text.NumberFormat
import java.util.*

fun Double.getUSFormat(): String =
    NumberFormat.getNumberInstance(Locale.US).format(this)
