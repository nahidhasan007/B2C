package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Duration(
    val hour: Long = 0,
    val minute: Long = 0
) : Parcelable
