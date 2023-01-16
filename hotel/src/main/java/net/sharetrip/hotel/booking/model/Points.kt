package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Points(
    var shared: Int = 0,
    var shareLink: String? = null,
    var earning: Int = 0
) : Parcelable
