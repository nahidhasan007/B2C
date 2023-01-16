package net.sharetrip.flight.booking.model.flightresponse

import com.squareup.moshi.Json
import net.sharetrip.flight.booking.model.filter.FlightFilter
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights

data class FlightsResponse (
    var searchId: String,
    var sessionId: String,
    var advice: String,
    var items: Int = 0,
    var totalRecords: Int = 0,
    @field:Json(name = "class")
    var travelClass: String,
    val filterDeal:String,
    var tripType: String,
    var filters: FlightFilter,
    val flights: List<Flights>
)
