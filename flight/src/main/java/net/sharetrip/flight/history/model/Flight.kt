package net.sharetrip.flight.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Flight(
        @Json(name = "searchCode")
        var searchCode: String? = null,
        @Json(name = "sequenceNumber")
        var sequenceNumber: String? = null,
        @Json(name = "sequenceCode")
        var sequenceCode: String? = null,
        @Json(name = "airlines")
        var airlines: Airlines? = null,
        @Json(name = "airlinesCode")
        var airlinesCode: String? = null,
        @Json(name = "logo")
        var logo: String? = null,
        @Json(name = "aircraft")
        var aircraft: String? = null,
        @Json(name = "aircraftCode")
        var aircraftCode: String? = null,
        @Json(name = "operatedBy")
        var operatedBy: String? = null,
        @Json(name = "originCode")
        var originCode: String? = null,
        @Json(name = "originName")
        var originName: FlightAddress? = null,
        @Json(name = "destinationName")
        var destinationName: FlightAddress? = null,
        @Json(name = "destinationCode")
        var destinationCode: String? = null,
        @Json(name = "FlightAddress")
        var flightAddress: FlightAddress? = null,
        @Json(name = "arrivalDateTime")
        var arrivalDateTime: FlightDateTime? = null,
        @Json(name = "departureDateTime")
        var departureDateTime: FlightDateTime? = null,
        @Json(name = "departureTimeZone")
        var departureTimeZone: String? = null,
        @Json(name = "arrivalTimeZone")
        var arrivalTimeZone: String? = null,
        @Json(name = "duration")
        var duration: String? = null,
        @Json(name = "availableSeat")
        var availableSeat: String? = null,
        @Json(name = "stop")
        var stop: String? = null,
        @Json(name = "stopSegment")
        var stopSegment: List<StopSegment>? = null,
        @Json(name = "dayCount")
        var dayCount: String? = null,
        @Json(name = "transitTime")
        var transitTime: String? = null

) : Parcelable {

    var flightCode: String? = null
        get() {

            return "$originCode - $destinationCode"

        }
}



