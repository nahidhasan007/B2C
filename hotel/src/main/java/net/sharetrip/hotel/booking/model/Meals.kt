package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meals(
    @Json(name = "id")
    var id: String,
    @Json(name = "name")
    var name: String,
    var isSelected: Boolean = false
) : Parcelable
