package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StopSegment(
        @Json(name = "iata")
        var iata: String? = null,
        @Json(name = "city")
        var city: String? = null,
        @Json(name = "name")
        var name: String? = null
) : Parcelable

