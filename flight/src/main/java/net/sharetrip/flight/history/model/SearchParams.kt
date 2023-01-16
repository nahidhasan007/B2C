package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchParams(
        @Json(name = "currency")
        var currency: String? = null,
        @Json(name = "stop")
        var stop: String? = null,
        @Json(name = "adult")
        var adult: Int? = null,
        @Json(name = "child")
        var child: Int? = null,
        @Json(name = "infant")
        var infant: Int? = null,
        @field:Json(name = "class")
        var class_: String? = null,
        @Json(name = "preferredAirlines")
        var preferredAirlines: String? = null,
        @Json(name = "flightType")
        var flightType: String? = null,
        @Json(name = "tripType")
        var tripType: String? = null,
        @Json(name = "airlines")
        var airlines: String? = null,
        @Json(name = "nextLink")
        var nextLink: String? = null,
        @Json(name = "deviceType")
        var deviceType: String? = null
) : Parcelable
