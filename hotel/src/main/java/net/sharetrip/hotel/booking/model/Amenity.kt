package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Amenity(
    @field:Json(name = "id")
    var id: String,
    @field:Json(name = "logoPng")
    var logo: String,
    @field:Json(name = "title")
    var name: String,
    var isSelected: Boolean = false
) : Parcelable
