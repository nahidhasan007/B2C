package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Point(
    @field:Json(name = "shared")
    var shared: Int = 0,
    @field:Json(name = "shareLink")
    var shareLink: String? = null,
    @field:Json(name = "earning")
    var earning: Int = 0
) : Parcelable
