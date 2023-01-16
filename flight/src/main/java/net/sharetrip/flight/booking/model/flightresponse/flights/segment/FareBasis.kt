package net.sharetrip.flight.booking.model.flightresponse.flights.segment

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FareBasis(
        @Json(name = "adult")
        var adult: String? = null,
        @Json(name = "child")
        var child: String? = null,
        @Json(name = "infant")
        var infant: String? = null
) : Parcelable
