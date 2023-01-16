package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HotelProperty(
    @Json(name = "id")
    var id: String? = null,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "countryCode")
    var countryCode: String? = null,
    @Json(name = "countryName")
    var countryName: String? = null,
    @Json(name = "cityName")
    var cityName: String? = null,
    @Json(name = "center")
    var center: Center? = null,
    @Json(name = "type")
    var type: String? = null
) : Parcelable
