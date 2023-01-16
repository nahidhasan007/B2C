package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rooms(
    @field:Json(name = "rooms")
    var rooms: List<RoomDetails>,
    @Json(name = "images")
    var images: List<String>,
    @field:Json(name = "rates")
    var rates: List<Rate>,
): Parcelable
