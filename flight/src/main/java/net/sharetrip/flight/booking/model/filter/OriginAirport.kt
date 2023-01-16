package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class OriginAirport(
        @Json(name = "iata")
        var iata: String? = null,
        @Json(name = "name")
        var name: String? = null
)
