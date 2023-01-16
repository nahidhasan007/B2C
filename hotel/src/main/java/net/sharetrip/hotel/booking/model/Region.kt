package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Region(
    @Json(name = "iata")
    var iata: String? = null,
    @Json(name = "countryCode")
    var countryCode: String? = null,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "type")
    var type: String? = null
) : Parcelable
