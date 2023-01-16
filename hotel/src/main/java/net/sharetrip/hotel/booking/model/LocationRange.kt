package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationRange(
    var range: Int = 0,
    var progress: Int = 0
) : Parcelable
