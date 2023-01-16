package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightAddress (
    @Json(name = "city")
    var city: String? = null,
    @Json(name = "airport")
    var airport: String? = null,
    @Json(name = "code")
    var code: String? = null
): Parcelable
