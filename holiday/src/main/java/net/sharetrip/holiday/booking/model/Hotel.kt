package net.sharetrip.holiday.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hotel(
    val id: Int,
    val cityName: String,
    var hotelId: String,
    var hotelName: String
) : Parcelable
