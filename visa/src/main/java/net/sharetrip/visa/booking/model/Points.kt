package net.sharetrip.visa.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Points(
    val shared: Int = 0,
    val shareLink: String = "",
    val earning: Int = 0
) : Parcelable
