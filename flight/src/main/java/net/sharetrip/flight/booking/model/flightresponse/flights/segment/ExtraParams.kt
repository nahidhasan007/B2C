package net.sharetrip.flight.booking.model.flightresponse.flights.segment

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtraParams(
        @Json(name = "fareBasis")
        var fareBasis: FareBasis
) : Parcelable
