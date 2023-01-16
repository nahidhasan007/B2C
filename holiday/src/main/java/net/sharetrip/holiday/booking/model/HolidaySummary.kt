package net.sharetrip.holiday.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HolidaySummary(
    var title: String = "",
    var address: String = "",
    var adult: String = "",
    var luggage: Int = 0,
    var date: String = "",
    var time: String = "4 Hours",
    var totalCost: Double,
    var earnTripCoin: Int,
    var room: Int = 0,
    var discountedPrice: Double
) : Parcelable