package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Fares(
    val discountedFare: String,
    val originalFare: String
) : Parcelable
