package net.sharetrip.flight.booking.model.flightresponse.flights.flight

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightAddress(
        @Json(name = "city")
        var city: String? = null,
        @Json(name = "airport")
        var airport: String? = null,
        @Json(name = "code")
        var code: String? = null
) : Parcelable
