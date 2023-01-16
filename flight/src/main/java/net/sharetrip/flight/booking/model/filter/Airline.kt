package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class Airline (
        @Json(name = "records")
        var records: Int = 0,
        @Json(name = "code")
        var code: String,
        @Json(name = "full")
        var full: String,
        @Json(name = "short")
        var short: String
)
