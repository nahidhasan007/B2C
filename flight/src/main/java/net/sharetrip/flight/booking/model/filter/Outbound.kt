package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class Outbound(
        @Json(name = "key")
        var key: String,
        @Json(name = "value")
        var value: String
)
