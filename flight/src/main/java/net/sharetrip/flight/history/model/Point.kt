package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Point(
        @Json(name = "shared")
        var shared: Int = 0,

        @Json(name = "shareLink")
        var shareLink: String? = null,

        @Json(name = "earning")
        var earning: Int = 0
) : Parcelable