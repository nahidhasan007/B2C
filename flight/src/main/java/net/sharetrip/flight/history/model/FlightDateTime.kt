package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightDateTime(
    @Json(name = "date")
    var date: String? = null,
    @Json(name = "time")
    var time: String? = null
) : Parcelable