package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomQuantities(
    @field:Json(name = "name")
    var name: String,
    @Json(name = "quantity")
    var quantity: Int
) : Parcelable
