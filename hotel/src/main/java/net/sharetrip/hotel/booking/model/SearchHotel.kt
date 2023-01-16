package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchHotel(
    var name: String = ""
) : Parcelable