package net.sharetrip.hotel.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookedRoom(
    @Json(name = "images")
    var images: String? = null,
    @Json(name = "price")
    var price: Int = 0,
    @Json(name = "adults")
    var adults: Int = 0,
    @Json(name = "name")
    var name: String? = null,
    @Json(name = "childs")
    var childs: Int = 0,
    @Json(name = "points")
    var points: String? = null
) : Parcelable
