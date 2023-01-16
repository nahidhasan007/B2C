package net.sharetrip.flight.history.model

import com.squareup.moshi.Json

data class AirFareResponse (
        @field:Json(name = "airFareRules")
        var airFareRules: List<AirFareRule>? = null,
        @field:Json(name = "baggages")
        var baggages: List<Baggage>? = null,
        @field:Json(name = "fareDetails")
        var fareDetails: String? = null
)
