package net.sharetrip.flight.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.sharetrip.flight.booking.model.flightresponse.flights.segment.HiddenStop

@Parcelize
data class ItemSegment(
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
    @Json(name = "flightNumber")
    var flightNumber: String? = null,
    @Json(name = "resBookDesigCode")
    var resBookDesigCode: String? = null,
    @Json(name = "logo")
    var logo: String? = null,
    @Json(name = "aircraft")
    var aircraft: String? = null,
    @Json(name = "aircraftCode")
    var aircraftCode: String? = null,
    @Json(name = "operatedBy")
    var operatedBy: String? = null,
    @Json(name = "marketingAirline")
    var marketingAirline: String? = null,
    @Json(name = "originCode")
    var originCode: String? = null,
    @Json(name = "originName")
    var originName: FlightAddress? = null,
    @Json(name = "originTerminal")
    var originTerminal: String? = null,
    @Json(name = "destinationCode")
    var destinationCode: String? = null,
    @Json(name = "destinationName")
    var destinationName: FlightAddress? = null,
    @Json(name = "destinationTerminal")
    var destinationTerminal: String? = null,
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
    @Json(name = "marriageGrp")
    var marriageGrp: String? = null,
    @Json(name = "baggageWeight")
    var baggageWeight: String? = null,
    @Json(name = "baggageUnit")
    var baggageUnit: String? = null,
    @Json(name = "infantBaggageWeight")
    var infantBaggageWeight: String? = null,
    @Json(name = "infantBaggageUnit")
    var infantBaggageUnit: String? = null,
    @Json(name = "fareReference")
    var fareReference: String? = null,
    @Json(name = "seatsRemaining")
    var seatsRemaining: String? = null,
    @Json(name = "seatsBelowMin")
    var seatsBelowMin: String? = null,
    @Json(name = "cabin")
    var cabin: String? = null,
    var cabinCode: String = "",
    @Json(name = "mealCode")
    var mealCode: String? = null,
    @Json(name = "dayCount")
    var dayCount: String? = null,
    @Json(name = "transitTime")
    var transitTime: String? = null,
    var classType: String = "",
    @Json(name = "hiddenStop")
    var hiddenStop: HiddenStop? = null
) : Parcelable
