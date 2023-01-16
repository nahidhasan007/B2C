package net.sharetrip.hotel.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HotelLocation(
    @Json(name = "lon")
    val lon: Double? = null,
    @Json(name = "lat")
    val lat: Double? = null
) : Parcelable
