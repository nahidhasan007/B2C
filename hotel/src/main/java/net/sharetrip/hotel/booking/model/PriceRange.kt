package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceRange(
    var low: Long = 0,
    var high: Long = 0
) : Parcelable
