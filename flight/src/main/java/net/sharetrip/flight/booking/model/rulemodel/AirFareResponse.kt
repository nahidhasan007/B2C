package net.sharetrip.flight.booking.model.rulemodel

import com.squareup.moshi.Json
import net.sharetrip.flight.booking.model.AirFareRule
import net.sharetrip.flight.booking.model.Baggage

data class AirFareResponse (
        @field:Json(name = "airFareRules")
        var airFareRules: List<AirFareRule>? = null,
        @field:Json(name = "baggages")
        var baggages: List<Baggage>? = null,
        @field:Json(name = "fareDetails")
        var fareDetails: String? = null
)
