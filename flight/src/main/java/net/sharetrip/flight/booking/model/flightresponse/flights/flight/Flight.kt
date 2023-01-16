package net.sharetrip.flight.booking.model.flightresponse.flights.flight

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Flight(
    @Json(name = "searchCode")
        var searchCode: String? = null,
    @Json(name = "sequenceCode")
        var sequenceCode: String,
    @Json(name = "airlines")
        var airlines: Airlines,
    @Json(name = "logo")
        var logo: String,
    @Json(name = "aircraft")
        var aircraft: String? = null,
    @Json(name = "aircraftCode")
        var aircraftCode: String? = null,
    @Json(name = "originName")
        var originName: FlightAddress,
    @Json(name = "destinationName")
        var destinationName: FlightAddress,
    @Json(name = "arrivalDateTime")
        var arrivalDateTime: DateTime,
    @Json(name = "departureDateTime")
        var departureDateTime: DateTime,
    @Json(name = "duration")
        var duration: String? = null,
    @Json(name = "stop")
        var stop: Int = 0,
    @Json(name = "stopSegment")
        var stopSegment: List<StopSegment>? = null,
    @Json(name = "weight")
        var weight: String? = null,
    @Json(name = "dayCount")
        var dayCount: String? = null,
    @Json(name = "seatsLeft")
        var seatsLeft: Int = 0,
    @Json(name= "hiddenStops")
        var hiddenStops: Boolean = false
) : Parcelable
