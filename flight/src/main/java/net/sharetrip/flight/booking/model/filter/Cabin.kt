package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class Cabin (
        @Json(name = "code")
        var code: String,
        @Json(name = "name")
        var name: String? = null
)
