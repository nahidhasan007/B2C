package net.sharetrip.flight.history.model


import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AirFareRule(
        @Json(name = "origin")
        var origin: String? = null,
        @Json(name = "originCode")
        var originCode: String? = null,
        @Json(name = "destination")
        var destination: String? = null,
        @Json(name = "destinationCode")
        var destinationCode: String? = null,
        @Json(name = "policy")
        var policy: Policy? = null) : Parcelable

