package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomAmenities(
    @field:Json(name = "name")
    var name: String,
    @Json(name = "logo")
    var logo: String,
    @field:Json(name = "logoPng")
    var logoPng: String,
    @field:Json(name = "logoPdf")
    var logoPdf: String
) : Parcelable
