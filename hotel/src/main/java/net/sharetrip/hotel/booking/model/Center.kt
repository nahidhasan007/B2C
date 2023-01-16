package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Center(
    @Json(name = "lon")
    var lon: String? = null,
    @Json(name = "lat")
    var lat: String? = null
) : Parcelable
