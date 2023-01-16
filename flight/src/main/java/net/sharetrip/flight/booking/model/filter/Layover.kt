package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class Layover (
        @Json(name = "iata")
        var iata: String,
        @Json(name = "name")
        var name: String
)
