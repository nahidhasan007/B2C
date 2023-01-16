package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterPrice(
    val from: Int,
    val to: Int
) : Parcelable
