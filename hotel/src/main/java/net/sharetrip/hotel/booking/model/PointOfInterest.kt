package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointOfInterest(
    @Json(name = "id")
    var id: String,
    @Json(name = "hotelsCount")
    var hotelsCount: Int? = 0,
    @Json(name = "name")
    var name: String? = null,
    var isSelected: Boolean = false
) : Parcelable
