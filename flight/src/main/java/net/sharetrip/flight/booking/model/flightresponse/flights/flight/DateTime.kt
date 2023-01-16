package net.sharetrip.flight.booking.model.flightresponse.flights.flight

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateTime(
        @Json(name = "date")
        var date: String? = null,
        @Json(name = "time")
        var time: String? = null
) : Parcelable