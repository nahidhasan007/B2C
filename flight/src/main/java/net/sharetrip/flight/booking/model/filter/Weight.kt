package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class Weight(
        @Json(name = "key")
        var key: Int? = null,
        @Json(name = "weight")
        var weight: Int? = null,
        @Json(name = "unit")
        var unit: String? = null,
        @Json(name = "note")
        var note: String? = null
)
