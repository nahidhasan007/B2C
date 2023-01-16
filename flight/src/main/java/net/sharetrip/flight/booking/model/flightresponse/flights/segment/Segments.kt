package net.sharetrip.flight.booking.model.flightresponse.flights.segment

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Segments(
        @Json(name = "type")
        var type: String? = null,
        @Json(name = "segment")
        var segment: List<Segment>
) : Parcelable