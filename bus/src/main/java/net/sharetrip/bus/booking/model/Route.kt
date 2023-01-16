package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(
    val code: String,
    var from: From,
    val id: String,
    var to: To
) : Parcelable
